package com.zplesac.connectionbuddy.sampleapp.activities;

import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.interfaces.NetworkRequestCheckListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.sampleapp.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Željko Plesac on 08/09/15.
 */
@SuppressWarnings("FieldCanBeLocal")
public class ManualConfigurationActivity extends Activity implements ConnectivityChangeListener {

    private TextView tvTitle;
    private TextView tvConnectionType;
    private Button buttonTestNetworkRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp);

        if (savedInstanceState != null) {
            ConnectionBuddy.getInstance().getConfiguration().getNetworkEventsCache().clearLastNetworkState(this);
        }

        tvTitle = findViewById(R.id.tv_title);
        tvConnectionType = findViewById(R.id.tv_connection_type);
        buttonTestNetworkRequest = findViewById(R.id.button_test_network_request);

        buttonTestNetworkRequest.setVisibility(View.VISIBLE);
        buttonTestNetworkRequest.setOnClickListener(testNetworkRequestButtonClickListener);
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

    private View.OnClickListener testNetworkRequestButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ConnectionBuddy.getInstance().hasNetworkConnection(new NetworkRequestCheckListener() {
                @Override
                public void onResponseObtained() {
                    Toast.makeText(ManualConfigurationActivity.this, "Response obtained!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNoResponse() {
                    Toast.makeText(ManualConfigurationActivity.this, "No response obtained!", Toast.LENGTH_LONG).show();
                }
            });
        }
    };
}
