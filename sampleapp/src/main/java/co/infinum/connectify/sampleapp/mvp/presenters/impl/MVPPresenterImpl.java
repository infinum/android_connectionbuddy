package co.infinum.connectify.sampleapp.mvp.presenters.impl;

import android.content.Context;

import javax.inject.Inject;

import co.infinum.connectify.ConnectifyPreferences;
import co.infinum.connectify.ConnectifyUtils;
import co.infinum.connectify.interfaces.ConnectivityChangeListener;
import co.infinum.connectify.receivers.NetworkChangeReceiver;
import co.infinum.connectify.sampleapp.mvp.presenters.MVPPresenter;
import co.infinum.connectify.sampleapp.mvp.views.MVPView;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public class MVPPresenterImpl implements MVPPresenter, ConnectivityChangeListener {

    private MVPView view;

    private Context context;

    @Inject
    public MVPPresenterImpl(MVPView view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void init(boolean hasSavedInstanceState) {
        if (!hasSavedInstanceState) {
            ConnectifyPreferences.clearInternetConnection(context, this);
        }

        view.initUI();
    }

    @Override
    public void registerForNetworkUpdates() {
        ConnectifyUtils.registerForConnectivityEvents(context, this, this);
    }

    @Override
    public void unregisterFromNetworkUpdates() {
        ConnectifyUtils.unregisterFromConnectivityEvents(context, this);
    }

    @Override
    public void onConnectionChange(NetworkChangeReceiver.ConnectivityEvent event) {
        view.onConnectionChangeEvent(event == NetworkChangeReceiver.ConnectivityEvent.CONNECTED);
    }
}
