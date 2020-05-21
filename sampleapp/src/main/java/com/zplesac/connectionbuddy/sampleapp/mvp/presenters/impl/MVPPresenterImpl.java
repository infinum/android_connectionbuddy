package com.zplesac.connectionbuddy.sampleapp.mvp.presenters.impl;

import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.sampleapp.mvp.presenters.MVPPresenter;
import com.zplesac.connectionbuddy.sampleapp.mvp.views.MVPView;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class MVPPresenterImpl implements MVPPresenter, ConnectivityChangeListener {

    private MVPView view;

    @Inject
    public MVPPresenterImpl(MVPView view) {
        this.view = view;
    }

    @Override
    public void init(boolean hasSavedInstanceState) {

        if (!hasSavedInstanceState) {
            ConnectionBuddy.getInstance().clearNetworkCache(this);
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
    public void onConnectionChange(@NonNull ConnectivityEvent event) {
        view.onConnectionChangeEvent(event);
    }
}
