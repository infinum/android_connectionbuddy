package com.zplesac.connectifty.interfaces;

import com.zplesac.connectifty.models.ConnectifyEvent;

/**
 * Created by Željko Plesac on 01/09/15.
 */
public interface ConnectivityChangeListener {

    /**
     * Interface method which is called when there is change in internet connection state.
     * @param event ConnectifyEvent which holds all data about network connection state.
     */
    void onConnectionChange(ConnectifyEvent event);
}
