package com.zplesac.connectify.sampleapp.mvp.views;

import com.zplesac.connectifty.models.ConnectifyEvent;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public interface MVPView {

    void initUI();

    void onConnectionChangeEvent(ConnectifyEvent event);
}
