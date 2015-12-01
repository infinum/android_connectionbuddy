package com.zplesac.connectify.sampleapp;

import com.zplesac.connectifty.Connectify;
import com.zplesac.connectifty.ConnectifyConfiguration;
import com.zplesac.connectifty.models.ConnectifyStrenght;

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
        ConnectifyConfiguration connectifyConfiguration = new ConnectifyConfiguration.Builder(this)
                .setNotifyImmediately(false)
                .build();
        Connectify.getInstance().init(connectifyConfiguration);
    }
}
