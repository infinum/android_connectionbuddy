package co.infinum.connectionutils.presenters;

import android.content.Context;

/**
 * Created by zeljkoplesac on 06/01/15.

    Default ConnectionPresenter, which must be extended by our application BasePresenter

 */
public interface ConnectionPresenter {

    /**
     * Activity or fragment should register for network updates on its onStart() method.
     * @param context Activity context
     */
    public void registerForNetworkUpdates(Context context);

    /**
     * Activity or fragment should unregister for network updates on its onStop() method.
     * @param context Activity context
     */
    public void unregisterFromNetworkUpdates(Context context);
}
