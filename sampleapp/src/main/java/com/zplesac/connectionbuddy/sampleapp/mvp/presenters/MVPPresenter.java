package com.zplesac.connectionbuddy.sampleapp.mvp.presenters;

import com.zplesac.connectionbuddy.presenters.ConnectivityPresenter;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public interface MVPPresenter extends ConnectivityPresenter {

    void init(boolean hasSavedInstanceState);
}
