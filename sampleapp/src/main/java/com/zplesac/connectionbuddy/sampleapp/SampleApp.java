package com.zplesac.connectionbuddy.sampleapp;

import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration;
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
        ConnectionBuddyConfiguration configuration = new ConnectionBuddyConfiguration.Builder(this)
                .setNotifyImmediately(false)
                .build();
        ConnectionBuddy.getInstance().init(configuration);
    }
}
