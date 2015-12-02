package com.zplesac.networkinspector.sampleapp.activities;


import com.zplesac.networkinspector.NetworkInspector;
import com.zplesac.networkinspector.cache.NetworkInspectorCache;
import com.zplesac.networkinspector.interfaces.ConnectivityChangeListener;
import com.zplesac.networkinspector.models.ConnectivityEvent;
import com.zplesac.networkinspector.sampleapp.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Željko Plesac on 08/09/15.
 */
public class SimpleActivity extends Activity implements ConnectivityChangeListener {

    private TextView tvTitle;

    private TextView tvConnectionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp);

        if (savedInstanceState != null) {
            NetworkInspectorCache.clearLastNetworkState(this);
        }

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvConnectionType = (TextView) findViewById(R.id.tv_connection_type);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Omit the default configuration - we want to obtain the current network connection state
        // after we register for network connectivity events.
        NetworkInspector.getInstance().registerForConnectivityEvents(this, true, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        NetworkInspector.getInstance().unregisterFromConnectivityEvents(this);
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        tvTitle.setText("Connection status: " + event.getState());
        tvConnectionType.setText("Connection type: " + event.getType());
    }
}
