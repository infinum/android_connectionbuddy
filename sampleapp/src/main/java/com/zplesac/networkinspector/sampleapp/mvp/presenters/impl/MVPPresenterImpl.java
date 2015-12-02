package com.zplesac.networkinspector.sampleapp.mvp.presenters.impl;

import com.zplesac.networkinspector.NetworkInspector;
import com.zplesac.networkinspector.cache.NetworkInspectorCache;
import com.zplesac.networkinspector.interfaces.ConnectivityChangeListener;
import com.zplesac.networkinspector.models.ConnectivityEvent;
import com.zplesac.networkinspector.sampleapp.mvp.presenters.MVPPresenter;
import com.zplesac.networkinspector.sampleapp.mvp.views.MVPView;

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
            NetworkInspectorCache.clearLastNetworkState(this);
        }

        view.initUI();
    }

    @Override
    public void registerForNetworkUpdates() {
        NetworkInspector.getInstance().registerForConnectivityEvents(this, this);
    }

    @Override
    public void unregisterFromNetworkUpdates() {
        NetworkInspector.getInstance().unregisterFromConnectivityEvents(this);
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        view.onConnectionChangeEvent(event);
    }
}
