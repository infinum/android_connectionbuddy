package com.zplesac.connectionbuddy.sampleapp.mvp.presenters.impl;

import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.cache.ConnectionBuddyCache;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.sampleapp.mvp.presenters.MVPPresenter;
import com.zplesac.connectionbuddy.sampleapp.mvp.views.MVPView;

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
            ConnectionBuddyCache.clearLastNetworkState(this);
        }

        view.initUI();
    }

    @Override
    public void registerForNetworkUpdates() {
        ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this);
    }

    @Override
    public void unregisterFromNetworkUpdates() {
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        view.onConnectionChangeEvent(event);
    }
}
