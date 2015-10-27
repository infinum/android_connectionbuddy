package com.zplesac.connectifty.models;

import java.io.Serializable;

/**
 * Created by Željko Plesac on 23/09/15.
 * Connectivity event which holds all the necessary data about network connection.
 */
public class ConnectifyEvent implements Serializable {

    private ConnectifyState state;

    private ConnectifyType type;

    private ConnectifyStrenght strenght;

    public ConnectifyEvent() {
    }

    public ConnectifyEvent(ConnectifyState state, ConnectifyType type, ConnectifyStrenght strenght) {
        this.state = state;
        this.type = type;
        this.strenght = strenght;
    }

    public ConnectifyState getState() {
        return state;
    }

    public void setState(ConnectifyState state) {
        this.state = state;
    }

    public ConnectifyType getType() {
        return type;
    }

    public void setType(ConnectifyType type) {
        this.type = type;
    }
}
