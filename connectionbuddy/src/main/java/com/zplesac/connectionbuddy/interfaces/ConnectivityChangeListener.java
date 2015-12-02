package com.zplesac.connectionbuddy.interfaces;


import com.zplesac.connectionbuddy.models.ConnectivityEvent;

/**
 * Created by Željko Plesac on 01/09/15.
 */
public interface ConnectivityChangeListener {

    /**
     * Interface method which is called when there is change in internet connection state.
     *
     * @param event ConnectivityEvent which holds all data about network connection state.
     */
    void onConnectionChange(ConnectivityEvent event);
}
