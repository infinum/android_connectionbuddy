package com.zplesac.connectify.sampleapp.mvp.presenters.impl;

import com.zplesac.connectifty.Connectify;
import com.zplesac.connectifty.cache.ConnectifyCache;
import com.zplesac.connectifty.interfaces.ConnectivityChangeListener;
import com.zplesac.connectifty.models.ConnectifyEvent;
import com.zplesac.connectify.sampleapp.mvp.presenters.MVPPresenter;
import com.zplesac.connectify.sampleapp.mvp.views.MVPView;

import javax.inject.Inject;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public class MVPPresenterImpl implements MVPPresenter, ConnectivityChangeListener {

    private MVPView view;

    @Inject
    public MVPPresenterImpl(MVPView view) {
        this.view = view;
    }

    @Override
    public void init(boolean hasSavedInstanceState) {
        if (!hasSavedInstanceState) {
            ConnectifyCache.clearInternetConnection(this);
        }

        view.initUI();
    }

    @Override
    public void registerForNetworkUpdates() {
        Connectify.getInstance().registerForConnectivityEvents(this, this);
    }

    @Override
    public void unregisterFromNetworkUpdates() {
        Connectify.getInstance().unregisterFromConnectivityEvents(this);
    }

    @Override
    public void onConnectionChange(ConnectifyEvent event) {
        view.onConnectionChangeEvent(event);
    }
}
