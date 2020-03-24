package com.zplesac.connectionbuddy;

import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

/**
 * Broadcast receiver that listens to network connectivity changes.
 */
class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String NOT_INITIALIZED_ERROR = "ConnectionBuddy not initialized.";

    @NonNull
    private Object object;

    @NonNull
    private ConnectivityChangeListener mCallback;

    NetworkChangeReceiver(@NonNull Object object, @NonNull ConnectivityChangeListener mCallback) {
        this.object = object;
        this.mCallback = mCallback;
    }

    /**
     * Receive network connectivity change event.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectionBuddy.getInstance().getConfiguration() == null) {
            throw new IllegalStateException(NOT_INITIALIZED_ERROR);
        }

        boolean hasConnectivity = ConnectionBuddy.getInstance().hasNetworkConnection();
        ConnectionBuddyCache cache = ConnectionBuddy.getInstance().getConfiguration().getNetworkEventsCache();

        if (hasConnectivity && !cache.getLastNetworkState(object)) {
            cache.setLastNetworkState(object, true);
            ConnectionBuddy.getInstance().notifyConnectionChange(true, mCallback);
        } else if (!hasConnectivity && cache.getLastNetworkState(object)) {
            cache.setLastNetworkState(object, false);
            ConnectionBuddy.getInstance().notifyConnectionChange(false, mCallback);
        }
    }
}
