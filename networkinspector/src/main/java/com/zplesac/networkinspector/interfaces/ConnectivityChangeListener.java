package com.zplesac.networkinspector.interfaces;

import com.zplesac.networkinspector.models.ConnectivityEvent;

/**
 * Created by Željko Plesac on 01/09/15.
 */
public interface ConnectivityChangeListener {

    /**
     * Interface method which is called when there is change in internet connection state.
     *
     * @param event NetworkInspectortEvent which holds all data about network connection state.
     */
    void onConnectionChange(ConnectivityEvent event);
}
