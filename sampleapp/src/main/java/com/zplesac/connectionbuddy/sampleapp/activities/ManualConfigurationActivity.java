package com.zplesac.connectionbuddy.sampleapp.activities;


import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.cache.ConnectionBuddyCache;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.sampleapp.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Željko Plesac on 08/09/15.
 */
public class ManualConfigurationActivity extends Activity implements ConnectivityChangeListener {

    private TextView tvTitle;

    private TextView tvConnectionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp);

        if (savedInstanceState != null) {
            ConnectionBuddyCache.clearLastNetworkState(this);
        }

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvConnectionType = (TextView) findViewById(R.id.tv_connection_type);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Omit the default configuration - we want to obtain the current network connection state
        // after we register for network connectivity events.
        ConnectionBuddy.getInstance().registerForConnectivityEvents(this, true, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        tvTitle.setText("Connection status: " + event.getState());
        tvConnectionType.setText("Connection type: " + event.getType());
    }
}
