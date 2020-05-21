package com.zplesac.connectionbuddy.sampleapp.activities;

import com.zplesac.connectionbuddy.activities.ConnectionBuddyActivity;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.sampleapp.R;

import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Željko Plesac on 21/02/16.
 */
public class SimpleActivity extends ConnectionBuddyActivity{

    private TextView tvTitle;
    private TextView tvConnectionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp);
        tvTitle = findViewById(R.id.tv_title);
        tvConnectionType = findViewById(R.id.tv_connection_type);
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        tvTitle.setText("Connection status: " + event.getState());
        tvConnectionType.setText("Connection type: " + event.getType());
    }
}
