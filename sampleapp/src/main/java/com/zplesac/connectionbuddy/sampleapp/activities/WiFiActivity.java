package com.zplesac.connectionbuddy.sampleapp.activities;

import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.interfaces.WifiConnectivityListener;
import com.zplesac.connectionbuddy.sampleapp.R;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Željko Plesac on 15/11/16.
 */
public class WiFiActivity extends AppCompatActivity implements WifiConnectivityListener {

    private static final String TAG = "WiFiActivity";

    private static final int RC_LOCATION = 147;

    private EditText etSsid;

    private EditText etPassword;

    private Button buttonConnect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi);

        etSsid = (EditText) findViewById(R.id.et_ssid);
        etPassword = (EditText) findViewById(R.id.et_password);
        buttonConnect = (Button) findViewById(R.id.button_connect);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToWifi();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_LOCATION)
    private void connectToWifi() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            try {
                ConnectionBuddy.getInstance()
                        .connectToWifiConfiguration(this, etSsid.getText().toString(), etPassword.getText().toString(), true,
                                WiFiActivity.this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.change_wifi_state_rationale),
                    RC_LOCATION, perms);
        }
    }

    @Override
    public void onConnected() {
        Toast.makeText(this, getString(R.string.connected_to_wifi), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNotFound() {
        Toast.makeText(this, getString(R.string.wifi_not_found), Toast.LENGTH_LONG).show();
    }
}
