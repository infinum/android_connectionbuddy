package co.infinum.connectionutils;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashMap;

import co.infinum.connectionutils.receivers.NetworkChangeReceiver;
import de.greenrobot.event.EventBus;

/**
 * Created by zeljkoplesac on 06/10/14.
 */
public class ConnectionUtils {

    private static HashMap<String, NetworkChangeReceiver> receiversHashMap = new HashMap<String, NetworkChangeReceiver>();

    /**
     * Register for connectivity events. Must be called separately for each activity/context.
     * @param context
     * @param object
     */

    public static void registerForConnectivityEvents(Context context, Object object) {
        EventBus.getDefault().register(object);

        boolean hasConnection = hasNetworkConnection(context);

        if (ConnectionPreferences.containsInternetConnection(context, object) && ConnectionPreferences.getInternetConnection(context, object) != hasConnection) {
            ConnectionPreferences.setInternetConnection(context, object, hasConnection);

            if (hasConnection) {
                EventBus.getDefault().post(NetworkChangeReceiver.ConnectivityEvent.CONNECTED);
            } else {
                EventBus.getDefault().post(NetworkChangeReceiver.ConnectivityEvent.DISCONNECTED);
            }
        }
        else if(!ConnectionPreferences.containsInternetConnection(context, object)){
            ConnectionPreferences.setInternetConnection(context, object, hasConnection);

            if (hasConnection) {
                EventBus.getDefault().post(NetworkChangeReceiver.ConnectivityEvent.CONNECTED);
            } else {
                EventBus.getDefault().post(NetworkChangeReceiver.ConnectivityEvent.DISCONNECTED);
            }
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");

        NetworkChangeReceiver receiver = new NetworkChangeReceiver(object);

        if(!receiversHashMap.containsKey(object.toString())){
            receiversHashMap.put(object.toString(), receiver);
        }

        context.registerReceiver(receiver, filter);
    }

    /**
     * Unregister from connectivity events.
     * @param context
     * @param object
     */
    public static void unregisterFromConnectivityEvents(Context context, Object object) {
        NetworkChangeReceiver receiver = receiversHashMap.get(object.toString());
        context.unregisterReceiver(receiver);

        receiversHashMap.remove(object.toString());
        receiver = null;
        EventBus.getDefault().unregister(object);
    }

    /**
     * Returns true if application has internet connection.
     *
     * @param context
     * @return
     */
    public static boolean hasNetworkConnection(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfoMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo networkInfoWiFi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (networkInfoMobile != null && networkInfoMobile.isConnected()) {
                return true;
            } else if (networkInfoWiFi != null && networkInfoWiFi.isConnected()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
