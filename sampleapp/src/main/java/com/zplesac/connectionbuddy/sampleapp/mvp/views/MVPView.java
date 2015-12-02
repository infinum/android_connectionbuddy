package com.zplesac.connectionbuddy.sampleapp.mvp.views;


import com.zplesac.connectionbuddy.models.ConnectivityEvent;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public interface MVPView {

    void initUI();

    void onConnectionChangeEvent(ConnectivityEvent event);
}
