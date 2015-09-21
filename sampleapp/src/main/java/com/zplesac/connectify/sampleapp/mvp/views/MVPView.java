package com.zplesac.connectify.sampleapp.mvp.views;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public interface MVPView {

    void initUI();

    void onConnectionChangeEvent(boolean hasConnection);
}
