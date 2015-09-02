package co.infinum.sampleapp.mvp.presenters.impl;

import android.content.Context;

import javax.inject.Inject;

import co.infinum.connectionutils.ConnectionUtils;
import co.infinum.connectionutils.interfaces.ConnectivityChangeListener;
import co.infinum.connectionutils.receivers.NetworkChangeReceiver;
import co.infinum.sampleapp.mvp.presenters.MainPresenter;
import co.infinum.sampleapp.mvp.views.MainView;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public class MainPresenterImpl implements MainPresenter, ConnectivityChangeListener{

    private MainView view;

    @Inject
    public MainPresenterImpl(MainView view) {
        this.view = view;
    }

    @Override
    public void init() {
        view.initUI();
    }

    @Override
    public void registerForNetworkUpdates(Context context) {
        ConnectionUtils.registerForConnectivityEvents(context, this, this);
    }

    @Override
    public void unregisterFromNetworkUpdates(Context context) {
        ConnectionUtils.unregisterFromConnectivityEvents(context, this);
    }

    @Override
    public void onConnectionChange(NetworkChangeReceiver.ConnectivityEvent event) {
        view.onConnectionChangeEvent(event == NetworkChangeReceiver.ConnectivityEvent.CONNECTED);
    }
}
