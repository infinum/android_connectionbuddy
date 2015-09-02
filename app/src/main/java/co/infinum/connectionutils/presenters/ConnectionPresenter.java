package co.infinum.connectionutils.presenters;

/**
 * Created by zeljkoplesac on 06/01/15.
 *
 * Default ConnectionPresenter, which must be extended by our application BasePresenter
 */
public interface ConnectionPresenter {

    /**
     * Activity or fragment should register for network updates on its onStart() method.
     */
    void registerForNetworkUpdates();

    /**
     * Activity or fragment should unregister for network updates on its onStop() method.
     */
    void unregisterFromNetworkUpdates();
}
