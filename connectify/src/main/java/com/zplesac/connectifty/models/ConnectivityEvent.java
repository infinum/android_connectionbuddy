package com.zplesac.connectifty.models;

import java.io.Serializable;

/**
 * Created by Željko Plesac on 23/09/15.
 */
public class ConnectivityEvent implements Serializable {

    private ConnectivityState state;

    private ConnectivityType type;

    public ConnectivityEvent() {
    }

    public ConnectivityEvent(ConnectivityState state, ConnectivityType type) {
        this.state = state;
        this.type = type;
    }

    public ConnectivityState getState() {
        return state;
    }

    public void setState(ConnectivityState state) {
        this.state = state;
    }

    public ConnectivityType getType() {
        return type;
    }

    public void setType(ConnectivityType type) {
        this.type = type;
    }
}
