package co.infinum.connectionutils.presenters;

import android.content.Context;

import co.infinum.connectionutils.receivers.NetworkChangeReceiver;

/**
 * Created by zeljkoplesac on 06/01/15.

    Default ConnectionPresenter, which must be extended by our application BasePresenter

 */
public interface ConnectionPresenter {

    /**
     * Activity or fragment should register for network updates on its onStart() method.
     * @param context Activity context
     */
    void registerForNetworkUpdates(Context context);

    /**
     * Activity or fragment should unregister for network updates on its onStop() method.
     * @param context Activity context
     */
    void unregisterFromNetworkUpdates(Context context);

    /**
     * Called when there is a connectivity change.
     * {@link co.infinum.connectionutils.receivers.NetworkChangeReceiver.ConnectivityEvent} that is passed
     * notifies if the connection has just been established or broke.
     *
     * @param event has the application just gained or lost internet connection
     */
    void onEventMainThread(NetworkChangeReceiver.ConnectivityEvent event);
}
