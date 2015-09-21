package com.zplesac.connectify.sampleapp.mvp.presenters;

import com.zplesac.connectifty.presenters.ConnectifyPresenter;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public interface MVPPresenter extends ConnectifyPresenter {

    void init(boolean hasSavedInstanceState);
}
