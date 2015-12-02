package com.zplesac.networkinspector.sampleapp;


import com.zplesac.networkinspector.NetworkInspector;
import com.zplesac.networkinspector.NetworkInspectorConfiguration;

import android.app.Application;

/**
 * Created by Željko Plesac on 09/10/15.
 */
public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Define global configuration. We'll customize the default behaviour by defining
        // that we don't want to be notified about current network connection state after
        // we register for network connectivity events.
        NetworkInspectorConfiguration configuration = new NetworkInspectorConfiguration.Builder(this)
                .setNotifyImmediately(false)
                .build();
        NetworkInspector.getInstance().init(configuration);
    }
}
