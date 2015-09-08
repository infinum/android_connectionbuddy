package co.infinum.connectify.receivers;

/**
 * Created by zeljkoplesac on 06/10/14.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import co.infinum.connectify.ConnectifyPreferences;
import co.infinum.connectify.interfaces.ConnectivityChangeListener;
import co.infinum.connectify.ConnectifyUtils;


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
        boolean hasConnectivity = ConnectifyUtils.hasNetworkConnection(context);

        if (hasConnectivity && ConnectifyPreferences.getInternetConnection(context, object) != hasConnectivity) {
            ConnectifyPreferences.setInternetConnection(context, object, hasConnectivity);
            mCallback.onConnectionChange(ConnectivityEvent.CONNECTED);
        } else if (!hasConnectivity && ConnectifyPreferences.getInternetConnection(context, object) != hasConnectivity) {
            ConnectifyPreferences.setInternetConnection(context, object, hasConnectivity);
            mCallback.onConnectionChange(ConnectivityEvent.DISCONNECTED);
        }
    }
}
