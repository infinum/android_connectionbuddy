package co.infinum.connectionutils.interfaces;

import co.infinum.connectionutils.receivers.NetworkChangeReceiver;

/**
 * Created by Željko Plesac on 01/09/15.
 */
public interface ConnectivityChangeListener {

    void onConnectionChange(NetworkChangeReceiver.ConnectivityEvent event);
}
