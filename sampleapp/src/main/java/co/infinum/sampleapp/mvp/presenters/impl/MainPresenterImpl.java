package co.infinum.sampleapp.mvp.presenters.impl;

import android.content.Context;

import javax.inject.Inject;

import co.infinum.connectionutils.ConnectionPreferences;
import co.infinum.connectionutils.ConnectionUtils;
import co.infinum.connectionutils.interfaces.ConnectivityChangeListener;
import co.infinum.connectionutils.receivers.NetworkChangeReceiver;
import co.infinum.sampleapp.mvp.presenters.MainPresenter;
import co.infinum.sampleapp.mvp.views.MainView;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public class MainPresenterImpl implements MainPresenter, ConnectivityChangeListener {

    private MainView view;

    private Context context;

    @Inject
    public MainPresenterImpl(MainView view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void init(boolean hasSavedInstanceState) {
        if (!hasSavedInstanceState) {
            ConnectionPreferences.clearInternetConnection(context, this);
        }

        view.initUI();
    }

    @Override
    public void registerForNetworkUpdates() {
        ConnectionUtils.registerForConnectivityEvents(context, this, this);
    }

    @Override
    public void unregisterFromNetworkUpdates() {
        ConnectionUtils.unregisterFromConnectivityEvents(context, this);
    }

    @Override
    public void onConnectionChange(NetworkChangeReceiver.ConnectivityEvent event) {
        view.onConnectionChangeEvent(event == NetworkChangeReceiver.ConnectivityEvent.CONNECTED);
    }
}
