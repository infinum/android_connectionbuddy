package co.infinum.connectionutils.listeners;

import android.content.Context;

/**
 * Created by zeljkoplesac on 06/10/14.
 */
public interface ConnectionListener {

    /**
     * Activity or fragment should register for network updates in onStart() method.
     * In default method implementation, application should call ConnectivityUtils.registerForConnectivityEvents(context, this);
     * @param context
     */
    public void registerForNetworkUpdates(Context context);

    /**
     * Activity or fragment should unregister from network updates in onPause() method.
     * In default method implementation, application should call ConnectivityUtils.unregisterFromConnectivityEvents(context, this);
     * @param context
     */
    public void unregisterFromNetworkUpdates(Context context);
}

