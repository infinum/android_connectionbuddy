package co.infinum.connectionutils.presenters;

import android.content.Context;

/**
 * Created by zeljkoplesac on 06/01/15.
 */
public interface ConnectionPresenter {

    public void registerForNetworkUpdates(Context context);

    public void unregisterFromNetworkUpdates(Context context);
}
