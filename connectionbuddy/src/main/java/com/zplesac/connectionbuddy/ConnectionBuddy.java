package com.zplesac.connectionbuddy;

import com.zplesac.connectionbuddy.cache.ConnectionBuddyCache;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
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
import android.telephony.TelephonyManager;
import java.util.HashMap;

/**
 * Created by Željko Plesac on 06/10/14.
 */
public class ConnectionBuddy {

    private static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    private static final String ACTION_WIFI_STATE_CHANGE = "android.net.wifi.WIFI_STATE_CHANGED";

    private static HashMap<String, NetworkChangeReceiver> receiversHashMap = new HashMap<String, NetworkChangeReceiver>();

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
     * Inintialize this instance with provided configuration.
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
    public void notifyConnectionChange(boolean hasConnection, ConnectivityChangeListener listener) {
        if (hasConnection) {
            ConnectivityEvent event = new ConnectivityEvent(ConnectivityState.CONNECTED, getNetworkType(),
                    getSignalStrength());

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
        } else {
            listener.onConnectionChange(new ConnectivityEvent(ConnectivityState.DISCONNECTED, ConnectivityType.NONE,
                    ConnectivityStrength.UNDEFINED));
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
     * Utility method which check current network connection state.
     *
     * @return True if we have active network connection, false otherwise.
     */
    public boolean hasNetworkConnection() {
        if (configuration.getConnectivityManager() != null) {
            NetworkInfo networkInfoMobile = configuration.getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo networkInfoWiFi = configuration.getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            return networkInfoMobile != null && networkInfoMobile.isConnected() || networkInfoWiFi.isConnected();
        } else {
            return false;
        }
    }

    /**
     * Get network connection type from ConnectivityManager.
     *
     * @return ConnectivityType which is available on current device.
     */
    public ConnectivityType getNetworkType() {
        if (configuration.getConnectivityManager() != null) {
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
        if (configuration.getConnectivityManager() != null) {
            NetworkInfo networkInfo = configuration.getConnectivityManager().getActiveNetworkInfo();

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return getWifiStrength();
            } else {
                return getMobileConnectionStrength(networkInfo);
            }
        } else {
            return ConnectivityStrength.UNDEFINED;
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
}
