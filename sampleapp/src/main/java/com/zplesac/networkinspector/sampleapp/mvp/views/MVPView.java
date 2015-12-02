package com.zplesac.networkinspector.sampleapp.mvp.views;


import com.zplesac.networkinspector.models.ConnectivityEvent;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public interface MVPView {

    void initUI();

    void onConnectionChangeEvent(ConnectivityEvent event);
}
