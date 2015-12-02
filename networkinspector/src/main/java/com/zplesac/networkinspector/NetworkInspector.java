package com.zplesac.networkinspector;

import com.zplesac.networkinspector.cache.NetworkInspectorCache;
import com.zplesac.networkinspector.interfaces.ConnectivityChangeListener;
import com.zplesac.networkinspector.models.ConnectivityEvent;
import com.zplesac.networkinspector.models.ConnectivityState;
import com.zplesac.networkinspector.models.ConnectivityInspectorStrenght;
import com.zplesac.networkinspector.models.ConnectivityType;
import com.zplesac.networkinspector.receivers.NetworkChangeReceiver;

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
public class NetworkInspector {

    private static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String ACTION_WIFI_STATE_CHANGE = "android.net.wifi.WIFI_STATE_CHANGED";

    private static HashMap<String, NetworkChangeReceiver> receiversHashMap = new HashMap<String, NetworkChangeReceiver>();

    private static volatile NetworkInspector instance;

    private NetworkInspectorConfiguration configuration;

    protected NetworkInspector() {
        // empty constructor
    }

    /**
     * Returns singleton class instance.
     */
    public static NetworkInspector getInstance() {
        if (instance == null) {
            synchronized (NetworkInspector.class) {
                if (instance == null) {
                    instance = new NetworkInspector();
                }
            }
        }
        return instance;
    }

    /**
     * Inintialize this instance with provided configuration.
     *
     * @param configuration NetworkInspector configuration which is used in instance.
     */
    public synchronized void init(NetworkInspectorConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException();
        }
        if (this.configuration == null) {
            this.configuration = configuration;
        }
    }

    public NetworkInspectorConfiguration getConfiguration() {
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

        if (NetworkInspectorCache.isLastNetworkStateStored(object)
                && NetworkInspectorCache.getLastNetworkState(object) != hasConnection) {
            NetworkInspectorCache.setLastNetworkState(object, hasConnection);

            if (notifyImmediately) {
                notifyConnectionChange(hasConnection, listener);
            }
        } else if (!NetworkInspectorCache.isLastNetworkStateStored(object)) {
            NetworkInspectorCache.setLastNetworkState(object, hasConnection);
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
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) configuration.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            ConnectivityEvent event = new ConnectivityEvent(ConnectivityState.CONNECTED, getNetworkType(connectivityManager),
                    getSignalStrength(connectivityManager));

            // check if signal strength is bellow minimum defined strength
            if (event.getStrenght().ordinal() < configuration.getMinimumSignalStrength().ordinal()) {
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
                    ConnectivityInspectorStrenght.UNDEFINED));
        }
    }

    /**
     * Unregister from connectivity events.
     */
    public void unregisterFromConnectivityEvents(Object object) {
        NetworkChangeReceiver receiver = receiversHashMap.get(object.toString());
        configuration.getContext().unregisterReceiver(receiver);

        receiversHashMap.remove(object.toString());
        receiver = null;
    }

    /**
     * Returns true if application has internet connection.
     */
    public boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) configuration.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfoMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo networkInfoWiFi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

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
    public ConnectivityType getNetworkType(ConnectivityManager connectivityManager) {
        if (connectivityManager != null) {
            NetworkInfo networkInfoMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo networkInfoWiFi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

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
     * @return NetworkInspectorStrenght for current network connection.
     */
    public ConnectivityInspectorStrenght getSignalStrength(ConnectivityManager connectivityManager) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return getWifiStrength();
            } else {
                return getMobileConnectionStrength(networkInfo);
            }
        } else {
            return ConnectivityInspectorStrenght.UNDEFINED;
        }
    }

    /**
     * Get WiFi signal strength.
     */
    private ConnectivityInspectorStrenght getWifiStrength() {
        WifiManager wifiManager = (WifiManager) configuration.getContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo != null) {
            int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(),
                    NetworkInspectorConfiguration.SIGNAL_STRENGTH_NUMBER_OF_LEVELS);

            switch (level) {
                case 0:
                    return ConnectivityInspectorStrenght.POOR;
                case 1:
                    return ConnectivityInspectorStrenght.GOOD;
                case 2:
                    return ConnectivityInspectorStrenght.EXCELLENT;
                default:
                    return ConnectivityInspectorStrenght.UNDEFINED;
            }
        } else {
            return ConnectivityInspectorStrenght.UNDEFINED;
        }
    }

    /**
     * Get mobile network signal strength.
     */
    private ConnectivityInspectorStrenght getMobileConnectionStrength(NetworkInfo info) {
        if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            switch (info.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return ConnectivityInspectorStrenght.POOR;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return ConnectivityInspectorStrenght.POOR;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return ConnectivityInspectorStrenght.POOR;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return ConnectivityInspectorStrenght.GOOD;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return ConnectivityInspectorStrenght.GOOD;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return ConnectivityInspectorStrenght.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return ConnectivityInspectorStrenght.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return ConnectivityInspectorStrenght.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return ConnectivityInspectorStrenght.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return ConnectivityInspectorStrenght.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return ConnectivityInspectorStrenght.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return ConnectivityInspectorStrenght.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return ConnectivityInspectorStrenght.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return ConnectivityInspectorStrenght.EXCELLENT;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return ConnectivityInspectorStrenght.EXCELLENT;
                default:
                    return ConnectivityInspectorStrenght.UNDEFINED;
            }
        } else {
            return ConnectivityInspectorStrenght.UNDEFINED;
        }
    }
}
