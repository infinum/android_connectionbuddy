package com.zplesac.connectionbuddy.presenters;

/**
 * Created by Željko Plesac on 06/01/15.
 *
 * Default ConnectivityPresenter, which must be extended by our application BasePresenter
 */
public interface ConnectivityPresenter {

    /**
     * Activity or fragment should register for network updates on its onStart() method.
     */
    void registerForNetworkUpdates();

    /**
     * Activity or fragment should unregister for network updates on its onStop() method.
     */
    void unregisterFromNetworkUpdates();
}
