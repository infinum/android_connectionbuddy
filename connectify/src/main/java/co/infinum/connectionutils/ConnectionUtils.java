package co.infinum.connectionutils;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashMap;

import co.infinum.connectionutils.interfaces.ConnectivityChangeListener;
import co.infinum.connectionutils.receivers.NetworkChangeReceiver;

/**
 * Created by zeljkoplesac on 06/10/14.
 */
public class ConnectionUtils {

    private static HashMap<String, NetworkChangeReceiver> receiversHashMap = new HashMap<String, NetworkChangeReceiver>();

    private static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String ACTION_WIFI_STATE_CHANGE = "android.net.wifi.WIFI_STATE_CHANGED";

    private ConnectionUtils() {
        // empty constructor
    }

    /**
     * Register for connectivity events. Must be called separately for each activity/context.
     */
    public static void registerForConnectivityEvents(Context context, Object object, ConnectivityChangeListener listener) {
        boolean hasConnection = hasNetworkConnection(context);

        if (ConnectionPreferences.containsInternetConnection(context, object)
                && ConnectionPreferences.getInternetConnection(context, object) != hasConnection) {
            ConnectionPreferences.setInternetConnection(context, object, hasConnection);

            if (hasConnection) {
                listener.onConnectionChange(NetworkChangeReceiver.ConnectivityEvent.CONNECTED);
            } else {
                listener.onConnectionChange(NetworkChangeReceiver.ConnectivityEvent.DISCONNECTED);
            }
        } else if (!ConnectionPreferences.containsInternetConnection(context, object)) {
            ConnectionPreferences.setInternetConnection(context, object, hasConnection);

            if (hasConnection) {
                listener.onConnectionChange(NetworkChangeReceiver.ConnectivityEvent.CONNECTED);
            } else {
                listener.onConnectionChange(NetworkChangeReceiver.ConnectivityEvent.DISCONNECTED);
            }
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CONNECTIVITY_CHANGE);
        filter.addAction(ACTION_WIFI_STATE_CHANGE);

        NetworkChangeReceiver receiver = new NetworkChangeReceiver(object, listener);

        if (!receiversHashMap.containsKey(object.toString())) {
            receiversHashMap.put(object.toString(), receiver);
        }

        context.registerReceiver(receiver, filter);
    }

    /**
     * Unregister from connectivity events.
     */
    public static void unregisterFromConnectivityEvents(Context context, Object object) {
        NetworkChangeReceiver receiver = receiversHashMap.get(object.toString());
        context.unregisterReceiver(receiver);

        receiversHashMap.remove(object.toString());
        receiver = null;
    }

    /**
     * Returns true if application has internet connection.
     */
    public static boolean hasNetworkConnection(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfoMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo networkInfoWiFi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            return (networkInfoMobile != null && networkInfoMobile.isConnected() || networkInfoWiFi.isConnected());
        } else {
            return false;
        }
    }
}
