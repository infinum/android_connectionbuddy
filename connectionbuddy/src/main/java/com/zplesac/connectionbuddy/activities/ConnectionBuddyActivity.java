package com.zplesac.connectionbuddy.activities;

import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Željko Plesac on 21/02/16.
 * Base activity for handling connectivity change events.
 */
public class ConnectionBuddyActivity extends Activity implements ConnectivityChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectionBuddy.getInstance().clearNetworkCache(this, savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Register for connectivity changes
        ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this);
    }

    @Override
    protected void onStop() {
        // Unregister from connectivity events
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);

        super.onStop();
    }

    /**
     * Override this method if you want to manually handle connectivity change events.
     *
     * @param event ConnectivityEvent which holds all data about network connection state.
     */
    @Override
    public void onConnectionChange(ConnectivityEvent event) {

    }
}
