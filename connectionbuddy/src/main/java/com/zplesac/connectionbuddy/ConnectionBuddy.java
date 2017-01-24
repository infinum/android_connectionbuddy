package com.zplesac.connectionbuddy;

import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.interfaces.NetworkRequestCheckListener;
import com.zplesac.connectionbuddy.interfaces.WifiConnectivityListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;
import com.zplesac.connectionbuddy.models.ConnectivityStrength;
import com.zplesac.connectionbuddy.models.ConnectivityType;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by Željko Plesac on 06/10/14.
 */
public class ConnectionBuddy {

    private static final String HEADER_KEY_USER_AGENT = "User-Agent";

    private static final String HEADER_VALUE_USER_AGENT = "Android";

    private static final String HEADER_KEY_CONNECTION = "Connection";

    private static final String HEADER_VALUE_CONNECTION = "close";

    private static final String NETWORK_CHECK_URL = "http://clients3.google.com/generate_204";

    private static final int CONNECTION_TIMEOUT = 1500;

    private static volatile ConnectionBuddy instance;

    private Map<String, NetworkChangeReceiver> networkReceiversHashMap = new HashMap<>();

    private WifiScanResultReceiver wifiScanResultReceiver;

    private WifiConnectionStateChangedReceiver wifiConnectionStateChangedReceiver;

    private ConnectionBuddyConfiguration configuration;

    private ExecutorService executor;


    protected ConnectionBuddy() {
        // empty constructor
    }

    /**
     * Get current library instance.
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
        ConnectionBuddyCache cache = configuration.getNetworkEventsCache();

        if (cache.isLastNetworkStateStored(object)
                && cache.getLastNetworkState(object) != hasConnection) {
            cache.setLastNetworkState(object, hasConnection);

            if (notifyImmediately) {
                notifyConnectionChange(hasConnection, listener);
            }
        } else if (!cache.isLastNetworkStateStored(object)) {
            cache.setLastNetworkState(object, hasConnection);
            if (notifyImmediately) {
                notifyConnectionChange(hasConnection, listener);
            }
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

        NetworkChangeReceiver receiver = new NetworkChangeReceiver(object, listener);

        if (!networkReceiversHashMap.containsKey(object.toString())) {
            networkReceiversHashMap.put(object.toString(), receiver);
        }

        configuration.getContext().registerReceiver(receiver, filter);
    }

    /**
     * Clears network events cache. Has to be called in onCreate() lifecycle methods for activities and fragments, so that we always reset
     * the previous connection state.
     *
     * @param object Activity or fragment, which is registered for connectivity state changes.
     */
    public void clearNetworkCache(Object object) {
        configuration.getNetworkEventsCache().clearLastNetworkState(object);
    }

    /**
     * Clears network events cache. Has to be called in onCreate() lifecycle methods for activities and fragments, so that we always reset
     * the previous connection state.
     *
     * @param object             Activity or fragment, which is registered for connectivity state changes.
     * @param savedInstanceState Activity or fragments bundle.
     */
    public void clearNetworkCache(Object object, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            configuration.getNetworkEventsCache().clearLastNetworkState(object);
        }
    }

    /**
     * Notify the current state of connection to provided interface listener.
     *
     * @param hasConnection Current state of internet connection.
     * @param listener      Interface listener which has to be notified about current internet connection state.
     */
    public void notifyConnectionChange(boolean hasConnection, final ConnectivityChangeListener listener) {
        if (hasConnection) {
            final ConnectivityEvent event = new ConnectivityEvent(new ConnectivityState(ConnectivityState.CONNECTED), getNetworkType(),
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
            listener.onConnectionChange(new ConnectivityEvent(
                    new ConnectivityState(ConnectivityState.DISCONNECTED),
                    new ConnectivityType(ConnectivityType.NONE),
                    new ConnectivityStrength(ConnectivityStrength.UNDEFINED)));
        }
    }

    /**
     * Determine if we should notify the listener about active internet connection, based on configuration values.
     *
     * @param event    ConnectivityEvent which will be posted to listener.
     * @param listener ConnectivityChangeListener which will receive ConnectivityEvent.
     */
    private void handleActiveInternetConnection(ConnectivityEvent event, ConnectivityChangeListener listener) {
        // handle only if signal strength is above or equal minimum defined strength
        if (event.getStrength().getValue() >= configuration.getMinimumSignalStrength().getValue()) {
            if (event.getType().getValue() == ConnectivityType.MOBILE && configuration.isRegisteredForMobileNetworkChanges()) {
                listener.onConnectionChange(event);
            } else if (event.getType().getValue() == ConnectivityType.WIFI && configuration.isRegisteredForWiFiChanges()) {
                listener.onConnectionChange(event);
            }
        }
    }

    /**
     * Unregister from network connectivity events.
     *
     * @param object Object which we want to unregister from connectivity changes.
     */
    public void unregisterFromConnectivityEvents(Object object) {
        NetworkChangeReceiver networkChangeReceiver = networkReceiversHashMap.get(object.toString());
        configuration.getContext().unregisterReceiver(networkChangeReceiver);
        networkReceiversHashMap.remove(object.toString());

        if (wifiScanResultReceiver != null) {
            configuration.getContext().unregisterReceiver(wifiScanResultReceiver);
            wifiScanResultReceiver = null;
        }

        if (wifiConnectionStateChangedReceiver != null) {
            configuration.getContext().unregisterReceiver(wifiConnectionStateChangedReceiver);
            wifiConnectionStateChangedReceiver = null;
        }

        networkChangeReceiver = null;
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

        NetworkInfo networkInfo = configuration.getConnectivityManager().getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
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
        if (executor == null) {
            executor = Executors.newFixedThreadPool(getConfiguration().getTestNetworkRequestExecutorSize());
        }

        executor.execute(new Runnable() {
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
        });
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

        NetworkInfo networkInfo = configuration.getConnectivityManager().getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return new ConnectivityType(ConnectivityType.WIFI);
                case ConnectivityManager.TYPE_MOBILE:
                    return new ConnectivityType(ConnectivityType.MOBILE);
                default:
                    return new ConnectivityType(ConnectivityType.UNDEFINED);
            }
        } else {
            return new ConnectivityType(ConnectivityType.NONE);
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

        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return getWifiStrength();
            } else {
                return getMobileConnectionStrength(networkInfo);
            }
        } else {
            return new ConnectivityStrength(ConnectivityStrength.UNDEFINED);
        }
    }

    /**
     * Get WiFi signal strength.
     */
    private ConnectivityStrength getWifiStrength() {
        WifiManager wifiManager = (WifiManager) configuration.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo != null) {
            int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(),
                    ConnectionBuddyConfiguration.SIGNAL_STRENGTH_NUMBER_OF_LEVELS);

            switch (level) {
                case 0:
                    return new ConnectivityStrength(ConnectivityStrength.POOR);
                case 1:
                    return new ConnectivityStrength(ConnectivityStrength.GOOD);
                case 2:
                    return new ConnectivityStrength(ConnectivityStrength.EXCELLENT);
                default:
                    return new ConnectivityStrength(ConnectivityStrength.UNDEFINED);
            }
        } else {
            return new ConnectivityStrength(ConnectivityStrength.UNDEFINED);
        }
    }

    /**
     * Get mobile network signal strength.
     */
    private ConnectivityStrength getMobileConnectionStrength(NetworkInfo info) {
        if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            switch (info.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return new ConnectivityStrength(ConnectivityStrength.POOR);
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return new ConnectivityStrength(ConnectivityStrength.POOR);
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return new ConnectivityStrength(ConnectivityStrength.POOR);
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return new ConnectivityStrength(ConnectivityStrength.GOOD);
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return new ConnectivityStrength(ConnectivityStrength.GOOD);
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return new ConnectivityStrength(ConnectivityStrength.EXCELLENT);
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return new ConnectivityStrength(ConnectivityStrength.EXCELLENT);
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return new ConnectivityStrength(ConnectivityStrength.EXCELLENT);
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return new ConnectivityStrength(ConnectivityStrength.EXCELLENT);
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return new ConnectivityStrength(ConnectivityStrength.EXCELLENT);
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return new ConnectivityStrength(ConnectivityStrength.EXCELLENT);
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return new ConnectivityStrength(ConnectivityStrength.EXCELLENT);
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return new ConnectivityStrength(ConnectivityStrength.EXCELLENT);
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return new ConnectivityStrength(ConnectivityStrength.EXCELLENT);
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return new ConnectivityStrength(ConnectivityStrength.EXCELLENT);
                default:
                    return new ConnectivityStrength(ConnectivityStrength.UNDEFINED);
            }
        } else {
            return new ConnectivityStrength(ConnectivityStrength.UNDEFINED);
        }
    }

    /**
     * Checks if user is on is in roaming.
     *
     * @return boolean variable, which describes if user is in roaming.
     */
    public boolean isOnRoaming() {
        NetworkInfo networkInfo = getConfiguration().getConnectivityManager().getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isRoaming();
    }

    /**
     * Connects to the WiFi configuration with given {@param networkSsid} as network configuration's SSID and {@param networkPassword} as
     * network configurations's password.
     * {@link android.Manifest.permission#ACCESS_COARSE_LOCATION} and {@link android.Manifest.permission#ACCESS_FINE_LOCATION} permissions
     * are required in order to initiate new access point scan.
     *
     * @param networkSsid     WifiConfiguration network SSID.
     * @param networkPassword WifiConfiguration network password.
     */
    @RequiresPermission(allOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void connectToWifiConfiguration(Context context, String networkSsid, String networkPassword, boolean disconnectIfNotFound)
            throws SecurityException {
        connectToWifiConfiguration(context, networkSsid, networkPassword, disconnectIfNotFound, null);
    }

    /**
     * Connects to the WiFi configuration with given {@param networkSsid} as network configuration's SSID and {@param networkPassword} as
     * network configurations's password and optionaly notifies about the result if {@param listener} has defined value.
     * {@link android.Manifest.permission#ACCESS_COARSE_LOCATION} and {@link android.Manifest.permission#ACCESS_FINE_LOCATION} permissions
     * are required in order to initiate new access point scan.
     *
     * @param networkSsid     WifiConfiguration network SSID.
     * @param networkPassword WifiConfiguration network password.
     * @param listener        Callback listener.
     */
    @RequiresPermission(allOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void connectToWifiConfiguration(Context context, String networkSsid, String networkPassword, boolean disconnectIfNotFound,
            WifiConnectivityListener listener) throws SecurityException {
        // Check if permissions have been granted
        if (ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION permissions have not been granted by the user.");
        } else {
            WifiManager wifiManager = (WifiManager) getConfiguration().getContext().getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }

            // there is no wifi configuration with given data in list of configured networks. Initialize scan for access points.
            wifiScanResultReceiver = new WifiScanResultReceiver(wifiManager, networkSsid, networkPassword, disconnectIfNotFound, listener);
            configuration.getContext()
                    .registerReceiver(wifiScanResultReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
        }
    }

    /**
     * Called from WifiScanResultReceiver, when WiFiManager has finished scanning for active access points. Note that SSIDs of the
     * configured networks are enclosed in double quotes, whilst the SSIDs returned in ScanResults are not.
     */
    private class WifiScanResultReceiver extends BroadcastReceiver {

        private WifiManager wifiManager;

        private WifiConnectivityListener listener;

        private String networkSsid;

        private String networkPassword;

        private boolean disconnectIfNotFound;

        public WifiScanResultReceiver(WifiManager wifiManager, String networkSsid,
                String networkPassword, boolean disconnectIfNotFound, WifiConnectivityListener listener) {
            this.wifiManager = wifiManager;
            this.listener = listener;
            this.networkSsid = networkSsid;
            this.networkPassword = networkPassword;
            this.disconnectIfNotFound = disconnectIfNotFound;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // unregister receiver, so that we are only notified once about the results
            context.unregisterReceiver(this);

            if (wifiManager != null && wifiManager.getScanResults() != null && wifiManager.getScanResults().size() > 0) {
                for (ScanResult scanResult : wifiManager.getScanResults()) {
                    if (scanResult.SSID != null && scanResult.SSID.equals(networkSsid)) {

                        int networkId;
                        WifiConfiguration wifiConfiguration = checkIfWifiAlreadyConfigured(wifiManager.getConfiguredNetworks());

                        if (wifiConfiguration == null) {
                            wifiConfiguration = new WifiConfiguration();
                            wifiConfiguration.SSID = "\"" + networkSsid + "\"";
                            wifiConfiguration.preSharedKey = "\"" + networkPassword + "\"";
                            networkId = wifiManager.addNetwork(wifiConfiguration);
                        } else {
                            // Set new password
                            wifiConfiguration.preSharedKey = "\"" + networkPassword + "\"";
                            networkId = wifiConfiguration.networkId;
                        }

                        // there is no wifi configuration with given data in list of configured networks. Initialize scan for access points.
                        wifiConnectionStateChangedReceiver = new WifiConnectionStateChangedReceiver(networkSsid, wifiManager,
                                disconnectIfNotFound, listener);
                        configuration.getContext()
                                .registerReceiver(wifiConnectionStateChangedReceiver,
                                        new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
                        wifiManager.enableNetwork(networkId, true);
                        return;
                    }
                }
            }

            if (listener != null) {
                listener.onNotFound();
            }
        }

        private WifiConfiguration checkIfWifiAlreadyConfigured(List<WifiConfiguration> wifiConfigurationList) {
            if (wifiConfigurationList != null && !wifiConfigurationList.isEmpty()) {
                for (WifiConfiguration configuration : wifiConfigurationList) {
                    if (configuration.SSID != null && configuration.SSID.equals("\"" + networkSsid + "\"")) {
                        return configuration;
                    }
                }
            }
            return null;
        }
    }

    private class WifiConnectionStateChangedReceiver extends BroadcastReceiver {

        private WifiConnectivityListener listener;

        private String networkSsid;

        private WifiManager wifiManager;

        private boolean disconnectIfNotFound;

        public WifiConnectionStateChangedReceiver(String networkSsid, @NonNull WifiManager wifiManager, boolean disconnectIfNotFound,
                WifiConnectivityListener listener) {
            this.listener = listener;
            this.networkSsid = networkSsid;
            this.wifiManager = wifiManager;
            this.disconnectIfNotFound = disconnectIfNotFound;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // unregister receiver, so that we are only notified once about the results
            context.unregisterReceiver(this);

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            if (listener != null) {
                if (networkInfo.isConnected() && wifiManager.getConnectionInfo().getSSID().replace("\"", "").equals(networkSsid)) {
                    listener.onConnected();
                } else {
                    if (disconnectIfNotFound) {
                        wifiManager.disconnect();
                    }

                    listener.onNotFound();
                }
            }
        }
    }

    /**
     * Callback executor,  which will post the runnable on main thread.
     */
    private Executor callbackExecutor = new Executor() {

        Handler mainHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainHandler.post(command);
        }
    };
}
