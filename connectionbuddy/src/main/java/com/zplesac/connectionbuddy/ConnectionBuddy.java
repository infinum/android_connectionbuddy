package com.zplesac.connectionbuddy;

import com.zplesac.connectionbuddy.cache.ConnectionBuddyCache;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.interfaces.NetworkRequestCheckListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;
import com.zplesac.connectionbuddy.models.ConnectivityStrength;
import com.zplesac.connectionbuddy.models.ConnectivityType;
import com.zplesac.connectionbuddy.receivers.NetworkChangeReceiver;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.Executor;

/**
 * Created by Željko Plesac on 06/10/14.
 */
public class ConnectionBuddy {

    private static final String HEADER_KEY_USER_AGENT = "User-Agent";

    private static final String HEADER_VALUE_USER_AGENT = "Android";

    private static final String HEADER_KEY_CONNECTION = "Connection";

    private static final String HEADER_VALUE_CONNECTION = "close";

    private static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    private static final String ACTION_WIFI_STATE_CHANGE = "android.net.wifi.WIFI_STATE_CHANGED";

    private static final String NETWORK_CHECK_URL = "http://clients3.google.com/generate_204";

    private static final int CONNECTION_TIMEOUT = 1500;

    private static HashMap<String, NetworkChangeReceiver> receiversHashMap = new HashMap<>();

    private static volatile ConnectionBuddy instance;

    private ConnectionBuddyConfiguration configuration;

    protected ConnectionBuddy() {
        // empty constructor
    }

    /**
     * Get current library instace.
     *
     * @return Current library instance.
     */
    public static ConnectionBuddy getInstance() {
        if (instance == null) {
            synchronized (ConnectionBuddy.class) {
                if (instance == null) {
                    instance = new ConnectionBuddy();
                }
            }
        }
        return instance;
    }

    /**
     * Initialize this instance with provided configuration.
     *
     * @param configuration ConnectionBuddy configuration which is used in instance.
     */
    public synchronized void init(ConnectionBuddyConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException();
        }
        if (this.configuration == null) {
            this.configuration = configuration;
        }
    }

    public ConnectionBuddyConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Register for network connectivity events. Must be called separately for each activity/context, and will use
     * global configuration to determine if we should notify the callback immediately about current network connection
     * state.
     *
     * @param object   Object which is registered to network change receiver.
     * @param listener Callback listener.
     */
    public void registerForConnectivityEvents(Object object, ConnectivityChangeListener listener) {
        registerForConnectivityEvents(object, configuration.isNotifyImmediately(), listener);
    }

    /**
     * Register for network connectivity events. Must be called separately for each activity/context.
     *
     * @param object            Object which is registered to network change receiver.
     * @param notifyImmediately Indicates should we immediately notify the callback about current network connection state.
     * @param listener          Callback listener.
     */
    public void registerForConnectivityEvents(Object object, boolean notifyImmediately, ConnectivityChangeListener listener) {
        boolean hasConnection = hasNetworkConnection();

        if (ConnectionBuddyCache.isLastNetworkStateStored(object)
                && ConnectionBuddyCache.getLastNetworkState(object) != hasConnection) {
            ConnectionBuddyCache.setLastNetworkState(object, hasConnection);

            if (notifyImmediately) {
                notifyConnectionChange(hasConnection, listener);
            }
        } else if (!ConnectionBuddyCache.isLastNetworkStateStored(object)) {
            ConnectionBuddyCache.setLastNetworkState(object, hasConnection);
            if (notifyImmediately) {
                notifyConnectionChange(hasConnection, listener);
            }
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CONNECTIVITY_CHANGE);
        filter.addAction(ACTION_WIFI_STATE_CHANGE);

        NetworkChangeReceiver receiver = new NetworkChangeReceiver(object, listener);

        if (!receiversHashMap.containsKey(object.toString())) {
            receiversHashMap.put(object.toString(), receiver);
        }

        configuration.getContext().registerReceiver(receiver, filter);
    }

    /**
     * Notify the current state of connection to provided interface listener.
     *
     * @param hasConnection Current state of internet connection.
     * @param listener      Interface listener which has to be notified about current internet connection state.
     */
    public void notifyConnectionChange(boolean hasConnection, final ConnectivityChangeListener listener) {
        if (hasConnection) {
            final ConnectivityEvent event = new ConnectivityEvent(ConnectivityState.CONNECTED, getNetworkType(),
                    getSignalStrength());

            if (configuration.isNotifyOnlyReliableEvents()) {
                testNetworkRequest(new NetworkRequestCheckListener() {
                    @Override
                    public void onResponseObtained() {
                        handleActiveInternetConnection(event, listener);
                    }

                    @Override
                    public void onNoResponse() {
                        // No response was obtained from test network request, which means that we have active internet connection,
                        // but user can't perform network requests (I.E. he uses mobile network and doesn't have enough credit.
                        // Don't notify about this connection event.
                    }
                });
            } else {
                handleActiveInternetConnection(event, listener);
            }
        } else {
            listener.onConnectionChange(new ConnectivityEvent(ConnectivityState.DISCONNECTED, ConnectivityType.NONE,
                    ConnectivityStrength.UNDEFINED));
        }
    }

    /**
     * Determine if we should notify the listener about active internet connection, based on configuration values.
     *
     * @param event    ConnectivityEvent which will be posted to listener.
     * @param listener ConnectivityChangeListener which will receive ConnectivityEvent.
     */
    private void handleActiveInternetConnection(ConnectivityEvent event, ConnectivityChangeListener listener) {
        // check if signal strength is bellow minimum defined strength
        if (event.getStrength().ordinal() < configuration.getMinimumSignalStrength().ordinal()) {
            return;
        } else if (event.getType() == ConnectivityType.BOTH && configuration.isRegisteredForMobileNetworkChanges()
                && configuration.isRegisteredForWiFiChanges()) {
            listener.onConnectionChange(event);
        } else if (event.getType() == ConnectivityType.MOBILE && configuration.isRegisteredForMobileNetworkChanges()) {
            listener.onConnectionChange(event);
        } else if (event.getType() == ConnectivityType.WIFI && configuration.isRegisteredForWiFiChanges()) {
            listener.onConnectionChange(event);
        }
    }

    /**
     * Unregister from network connectivity events.
     *
     * @param object Object which we want to unregister from connectivity changes.
     */
    public void unregisterFromConnectivityEvents(Object object) {
        NetworkChangeReceiver receiver = receiversHashMap.get(object.toString());
        configuration.getContext().unregisterReceiver(receiver);

        receiversHashMap.remove(object.toString());
        receiver = null;
    }

    /**
     * Utility method which checks current network connection state.
     *
     * @return True if we have active network connection, false otherwise.
     */
    public boolean hasNetworkConnection() {
        if (configuration.getConnectivityManager() == null) {
            throw new IllegalStateException("Connectivity manager is null, library was not properly initialized!");
        }

        NetworkInfo networkInfoMobile = configuration.getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo networkInfoWiFi = configuration.getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return networkInfoMobile != null && networkInfoMobile.isConnected() || networkInfoWiFi.isConnected();
    }

    /**
     * Utility method which checks current network connection state, but will also try to perform test network request, in order
     * to determine if user can actually perform any network operation.
     *
     * @param listener Callback listener.
     */
    public void hasNetworkConnection(NetworkRequestCheckListener listener) {
        if (hasNetworkConnection()) {
            testNetworkRequest(listener);
        } else {
            listener.onNoResponse();
        }
    }

    /**
     * Try to perform test network request to NETWORK_CHECK_URL. This way, we can determine if use is in fact capable of performing
     * any network operations when he has active internet connection.
     *
     * @param listener Callback listener.
     */
    private void testNetworkRequest(final NetworkRequestCheckListener listener) {
        // Send this to background thread
        Thread bgThread = new Thread() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection)
                            (new URL(NETWORK_CHECK_URL).openConnection());
                    httpURLConnection.setRequestProperty(HEADER_KEY_USER_AGENT, HEADER_VALUE_USER_AGENT);
                    httpURLConnection.setRequestProperty(HEADER_KEY_CONNECTION, HEADER_VALUE_CONNECTION);
                    httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                    httpURLConnection.connect();

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT
                            && httpURLConnection.getContentLength() == 0) {
                        callbackExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                listener.onResponseObtained();
                            }
                        });
                    } else {
                        callbackExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                listener.onNoResponse();
                            }
                        });
                    }
                } catch (IOException e) {
                    callbackExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            listener.onNoResponse();
                        }
                    });
                }
            }
        };

        // by default, the new thread inherits the priority of the thread that started it
        bgThread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        bgThread.start();
    }

    /**
     * Get network connection type from ConnectivityManager.
     *
     * @return ConnectivityType which is available on current device.
     */
    public ConnectivityType getNetworkType() {
        if (configuration.getConnectivityManager() == null) {
            throw new IllegalStateException("Connectivity manager is null, library was not properly initialized!");
        }

        NetworkInfo networkInfoMobile = configuration.getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo networkInfoWiFi = configuration.getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (networkInfoMobile != null && networkInfoMobile.isConnected() && networkInfoWiFi.isConnected()) {
            return ConnectivityType.BOTH;
        } else if (networkInfoMobile != null && networkInfoMobile.isConnected()) {
            return ConnectivityType.MOBILE;
        } else if (networkInfoWiFi.isConnected()) {
            return ConnectivityType.WIFI;
        } else {
            return ConnectivityType.NONE;
        }
    }

    /**
     * Get signal strength of current network connection.
     *
     * @return ConnectivityStrength object for current network connection.
     */
    public ConnectivityStrength getSignalStrength() {
        if (configuration.getConnectivityManager() == null) {
            throw new IllegalStateException("Connectivity manager is null, library was not properly initialized!");
        }

        NetworkInfo networkInfo = configuration.getConnectivityManager().getActiveNetworkInfo();

        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return getWifiStrength();
        } else {
            return getMobileConnectionStrength(networkInfo);
        }
    }

    /**
     * Get WiFi signal strength.
     */
    private ConnectivityStrength getWifiStrength() {
        WifiManager wifiManager = (WifiManager) configuration.getContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo != null) {
            int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(),
                    ConnectionBuddyConfiguration.SIGNAL_STRENGTH_NUMBER_OF_LEVELS);

            switch (level) {
                case 0:
                    return ConnectivityStrength.POOR;
                case 1:
                    return ConnectivityStrength.GOOD;
                case 2:
                    return ConnectivityStrength.EXCELLENT;
                default:
                    return ConnectivityStrength.UNDEFINED;
            }
        } else {
            return ConnectivityStrength.UNDEFINED;
        }
    }

    /**
     * Get mobile network signal strength.
     */
    private ConnectivityStrength getMobileConnectionStrength(NetworkInfo info) {
        if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            switch (info.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return ConnectivityStrength.POOR;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return ConnectivityStrength.POOR;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return ConnectivityStrength.POOR;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return ConnectivityStrength.GOOD;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return ConnectivityStrength.GOOD;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return ConnectivityStrength.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return ConnectivityStrength.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return ConnectivityStrength.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return ConnectivityStrength.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return ConnectivityStrength.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return ConnectivityStrength.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return ConnectivityStrength.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return ConnectivityStrength.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return ConnectivityStrength.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return ConnectivityStrength.EXCELLENT;
                default:
                    return ConnectivityStrength.UNDEFINED;
            }
        } else {
            return ConnectivityStrength.UNDEFINED;
        }
    }

    /**
     * Check if user is in roaming.
     */
    public boolean isOnRoaming() {
        TelephonyManager telephonyManager = (TelephonyManager) configuration.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.isNetworkRoaming();
    }

    /**
     * Callback executor,  which will post the runnable on main thread.
     *
     * */
    private Executor callbackExecutor = new Executor() {

        Handler mainHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainHandler.post(command);
        }
    };
}
