package com.zplesac.connectionbuddy.sampleapp.activities;

import com.zplesac.connectionbuddy.sampleapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * Created by Željko Plesac on 08/09/15.
 */
@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends Activity {

    private Button buttonSimpleExample;
    private Button buttonMVPExample;
    private Button buttonManualConfiguration;
    private Button buttonWifiExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonMVPExample = findViewById(R.id.button_mvp);
        buttonSimpleExample = findViewById(R.id.button_simple);
        buttonManualConfiguration = findViewById(R.id.button_manual_configuration);
        buttonWifiExample = findViewById(R.id.button_wifi);

        buttonManualConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManualConfigurationActivity.class);
                startActivity(intent);
            }
        });

        buttonMVPExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MVPActivity.class);
                startActivity(intent);
            }
        });

        buttonSimpleExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SimpleActivity.class);
                startActivity(intent);
            }
        });

        buttonWifiExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WiFiActivity.class);
                startActivity(intent);
            }
        });
    }
}
