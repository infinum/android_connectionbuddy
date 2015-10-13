package com.zplesac.connectifty;

import com.zplesac.connectifty.interfaces.ConnectivityChangeListener;
import com.zplesac.connectifty.models.ConnectifyEvent;
import com.zplesac.connectifty.models.ConnectifyState;
import com.zplesac.connectifty.models.ConnectifyType;
import com.zplesac.connectifty.receivers.NetworkChangeReceiver;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashMap;

/**
 * Created by Željko Plesac on 06/10/14.
 */
public class Connectify {

    private static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String ACTION_WIFI_STATE_CHANGE = "android.net.wifi.WIFI_STATE_CHANGED";

    private static HashMap<String, NetworkChangeReceiver> receiversHashMap = new HashMap<String, NetworkChangeReceiver>();

    private static volatile Connectify instance;

    private ConnectifyConfiguration configuration;

    protected Connectify() {
        // empty constructor
    }

    /**
     * Returns singleton class instance.
     */
    public static Connectify getInstance() {
        if (instance == null) {
            synchronized (Connectify.class) {
                if (instance == null) {
                    instance = new Connectify();
                }
            }
        }
        return instance;
    }

    public synchronized void init(ConnectifyConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException();
        }
        if (this.configuration == null) {
            this.configuration = configuration;
        }
    }

    public ConnectifyConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Register for connectivity events. Must be called separately for each activity/context.
     */
    public void registerForConnectivityEvents(Object object, ConnectivityChangeListener listener) {
        boolean hasConnection = hasNetworkConnection();

        if (ConnectifyPreferences.containsInternetConnection(object)
                && ConnectifyPreferences.getInternetConnection(object) != hasConnection) {
            ConnectifyPreferences.setInternetConnection(object, hasConnection);

            notifyConnectionChange(hasConnection, listener);
        } else if (!ConnectifyPreferences.containsInternetConnection(object)) {
            ConnectifyPreferences.setInternetConnection(object, hasConnection);
            notifyConnectionChange(hasConnection, listener);
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

    public void notifyConnectionChange(boolean hasConnection, ConnectivityChangeListener listener) {
        if (hasConnection) {
            ConnectifyEvent event = new ConnectifyEvent(ConnectifyState.CONNECTED);

            if (event.getType() == ConnectifyType.BOTH && configuration.isRegisteredForMobileNetworkChanges()
                    && configuration.isRegisteredForWiFiChanges()) {
                listener.onConnectionChange(event);
            } else if (event.getType() == ConnectifyType.MOBILE && configuration.isRegisteredForMobileNetworkChanges()) {
                listener.onConnectionChange(event);
            } else if (event.getType() == ConnectifyType.WIFI && configuration.isRegisteredForWiFiChanges()) {
                listener.onConnectionChange(event);
            }
        } else {
            listener.onConnectionChange(new ConnectifyEvent(ConnectifyState.DISCONNECTED));
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

            return (networkInfoMobile != null && networkInfoMobile.isConnected() || networkInfoWiFi.isConnected());
        } else {
            return false;
        }
    }

    /**
     * Get network connection type from ConnectivityManager.
     *
     * @return ConnectivityType which is available on current device.
     */
    public ConnectifyType getNetworkType() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) configuration.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfoMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo networkInfoWiFi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (networkInfoMobile != null && networkInfoMobile.isConnected() && networkInfoWiFi.isConnected()) {
                return ConnectifyType.BOTH;
            } else if (networkInfoMobile != null && networkInfoMobile.isConnected()) {
                return ConnectifyType.MOBILE;
            } else if (networkInfoWiFi.isConnected()) {
                return ConnectifyType.WIFI;
            } else {
                return ConnectifyType.NONE;
            }
        } else {
            return ConnectifyType.NONE;
        }
    }
}
