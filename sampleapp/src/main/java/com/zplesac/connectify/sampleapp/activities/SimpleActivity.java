package com.zplesac.connectify.sampleapp.activities;

import com.zplesac.connectifty.Connectify;
import com.zplesac.connectifty.cache.ConnectifyCache;
import com.zplesac.connectifty.interfaces.ConnectivityChangeListener;
import com.zplesac.connectifty.models.ConnectifyEvent;
import com.zplesac.connectify.sampleapp.R;

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
            ConnectifyCache.clearInternetConnection(this);
        }

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvConnectionType = (TextView) findViewById(R.id.tv_connection_type);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Connectify.getInstance().registerForConnectivityEvents(this, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Connectify.getInstance().unregisterFromConnectivityEvents(this);
    }

    @Override
    public void onConnectionChange(ConnectifyEvent event) {
        tvTitle.setText("Connection status: " + event.getState());
        tvConnectionType.setText("Connection type: " + event.getType());
    }
}
