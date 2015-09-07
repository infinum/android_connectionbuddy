package co.infinum.connectionutils.receivers;

/**
 * Created by zeljkoplesac on 06/10/14.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import co.infinum.connectionutils.ConnectionPreferences;
import co.infinum.connectionutils.ConnectionUtils;
import co.infinum.connectionutils.interfaces.ConnectivityChangeListener;


/**
 * Broadcast receiver that listens to network connectivity changes.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private Object object;

    private ConnectivityChangeListener mCallback;

    public NetworkChangeReceiver(Object object, ConnectivityChangeListener mCallback) {
        this.object = object;
        this.mCallback = mCallback;
    }

    public enum ConnectivityEvent {
        CONNECTED,
        DISCONNECTED
    }

    /**
     * Receive network connectivity change event.
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean hasConnectivity = ConnectionUtils.hasNetworkConnection(context);

        if (hasConnectivity && ConnectionPreferences.getInternetConnection(context, object) != hasConnectivity) {
            ConnectionPreferences.setInternetConnection(context, object, hasConnectivity);
            mCallback.onConnectionChange(ConnectivityEvent.CONNECTED);
        } else if (!hasConnectivity && ConnectionPreferences.getInternetConnection(context, object) != hasConnectivity) {
            ConnectionPreferences.setInternetConnection(context, object, hasConnectivity);
            mCallback.onConnectionChange(ConnectivityEvent.DISCONNECTED);
        }
    }
}
