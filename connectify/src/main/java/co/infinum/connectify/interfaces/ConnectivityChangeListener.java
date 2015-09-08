package co.infinum.connectify.interfaces;

import co.infinum.connectify.receivers.NetworkChangeReceiver;

/**
 * Created by Željko Plesac on 01/09/15.
 */
public interface ConnectivityChangeListener {

    void onConnectionChange(NetworkChangeReceiver.ConnectivityEvent event);
}
