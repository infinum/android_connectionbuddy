package com.zplesac.connectionbuddy.receivers;

/**
 * Created by Željko Plesac on 06/10/14.
 */
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.cache.ConnectionBuddyCache;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;

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
        boolean hasConnectivity = ConnectionBuddy.getInstance().hasNetworkConnection();

        if (hasConnectivity && ConnectionBuddyCache.getLastNetworkState(object) != hasConnectivity) {
            ConnectionBuddyCache.setLastNetworkState(object, hasConnectivity);
            ConnectionBuddy.getInstance().notifyConnectionChange(hasConnectivity, mCallback);
        } else if (!hasConnectivity && ConnectionBuddyCache.getLastNetworkState(object) != hasConnectivity) {
            ConnectionBuddyCache.setLastNetworkState(object, hasConnectivity);
            ConnectionBuddy.getInstance().notifyConnectionChange(hasConnectivity, mCallback);
        }
    }
}
