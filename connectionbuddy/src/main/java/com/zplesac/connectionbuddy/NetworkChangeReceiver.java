package com.zplesac.connectionbuddy;

import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Broadcast receiver that listens to network connectivity changes.
 */
class NetworkChangeReceiver extends BroadcastReceiver {

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
        ConnectionBuddyCache cache = ConnectionBuddy.getInstance().getConfiguration().getNetworkEventsCache();

        if (hasConnectivity && cache.getLastNetworkState(object) != hasConnectivity) {
            cache.setLastNetworkState(object, hasConnectivity);
            ConnectionBuddy.getInstance().notifyConnectionChange(hasConnectivity, mCallback);
        } else if (!hasConnectivity && cache.getLastNetworkState(object) != hasConnectivity) {
            cache.setLastNetworkState(object, hasConnectivity);
            ConnectionBuddy.getInstance().notifyConnectionChange(hasConnectivity, mCallback);
        }
    }
}
