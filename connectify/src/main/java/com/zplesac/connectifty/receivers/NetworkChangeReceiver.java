package com.zplesac.connectifty.receivers;

/**
 * Created by Željko Plesac on 06/10/14.
 */

import com.zplesac.connectifty.Connectify;
import com.zplesac.connectifty.cache.ConnectifyCache;
import com.zplesac.connectifty.interfaces.ConnectivityChangeListener;

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
        boolean hasConnectivity = Connectify.getInstance().hasNetworkConnection();

        if (hasConnectivity && ConnectifyCache.getInternetConnection(object) != hasConnectivity) {
            ConnectifyCache.setInternetConnection(object, hasConnectivity);
            Connectify.getInstance().notifyConnectionChange(hasConnectivity, mCallback);
        } else if (!hasConnectivity && ConnectifyCache.getInternetConnection(object) != hasConnectivity) {
            ConnectifyCache.setInternetConnection(object, hasConnectivity);
            Connectify.getInstance().notifyConnectionChange(hasConnectivity, mCallback);
        }
    }
}
