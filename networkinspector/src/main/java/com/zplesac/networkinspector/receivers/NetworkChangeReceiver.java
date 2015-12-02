package com.zplesac.networkinspector.receivers;

/**
 * Created by Željko Plesac on 06/10/14.
 */

import com.zplesac.networkinspector.NetworkInspector;
import com.zplesac.networkinspector.cache.NetworkInspectorCache;
import com.zplesac.networkinspector.interfaces.ConnectivityChangeListener;

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
        boolean hasConnectivity = NetworkInspector.getInstance().hasNetworkConnection();

        if (hasConnectivity && NetworkInspectorCache.getLastNetworkState(object) != hasConnectivity) {
            NetworkInspectorCache.setLastNetworkState(object, hasConnectivity);
            NetworkInspector.getInstance().notifyConnectionChange(hasConnectivity, mCallback);
        } else if (!hasConnectivity && NetworkInspectorCache.getLastNetworkState(object) != hasConnectivity) {
            NetworkInspectorCache.setLastNetworkState(object, hasConnectivity);
            NetworkInspector.getInstance().notifyConnectionChange(hasConnectivity, mCallback);
        }
    }
}
