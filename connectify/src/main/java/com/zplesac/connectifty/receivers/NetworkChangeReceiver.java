package com.zplesac.connectifty.receivers;

/**
 * Created by Željko Plesac on 06/10/14.
 */

import com.zplesac.connectifty.ConnectifyPreferences;
import com.zplesac.connectifty.ConnectifyUtils;
import com.zplesac.connectifty.interfaces.ConnectivityChangeListener;
import com.zplesac.connectifty.models.ConnectivityState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


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

    /**
     * Receive network connectivity change event.
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean hasConnectivity = ConnectifyUtils.hasNetworkConnection(context);

        if (hasConnectivity && ConnectifyPreferences.getInternetConnection(context, object) != hasConnectivity) {
            ConnectifyPreferences.setInternetConnection(context, object, hasConnectivity);
            mCallback.onConnectionChange(ConnectifyUtils.buildConnectifyEvent(context, ConnectivityState.CONNECTED));
        } else if (!hasConnectivity && ConnectifyPreferences.getInternetConnection(context, object) != hasConnectivity) {
            ConnectifyPreferences.setInternetConnection(context, object, hasConnectivity);
            mCallback.onConnectionChange(ConnectifyUtils.buildConnectifyEvent(context, ConnectivityState.DISCONNECTED));
        }
    }
}
