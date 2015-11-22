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
        ConnectifyConfiguration connectifyConfiguration = new ConnectifyConfiguration.Builder(this).build();
        Connectify.getInstance().init(connectifyConfiguration);
    }
}
