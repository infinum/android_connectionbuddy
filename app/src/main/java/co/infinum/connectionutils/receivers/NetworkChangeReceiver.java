package co.infinum.connectionutils.receivers;

/**
 * Created by zeljkoplesac on 06/10/14.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import co.infinum.connectionutils.ConnectionPreferences;
import co.infinum.connectionutils.ConnectionUtils;
import de.greenrobot.event.EventBus;


/**
 * Broadcast receiver that listens to network connectivity changes.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private Object object;

    public NetworkChangeReceiver(Object object) {
        this.object = object;
    }

    public enum ConnectivityEvent {
        CONNECTED,
        DISCONNECTED
    }

    /**
     * Receive network connectivity change event.
     * @param context
     * @param intent
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean hasConnectivity = ConnectionUtils.hasNetworkConnection(context);

        if(hasConnectivity && ConnectionPreferences.getInternetConnection(context, object) != hasConnectivity) {
            ConnectionPreferences.setInternetConnection(context, object, hasConnectivity);
            EventBus.getDefault().post(ConnectivityEvent.CONNECTED);
        } else if(!hasConnectivity && ConnectionPreferences.getInternetConnection(context, object) != hasConnectivity){
            ConnectionPreferences.setInternetConnection(context, object, hasConnectivity);
            EventBus.getDefault().post(ConnectivityEvent.DISCONNECTED);
        }
    }
}
