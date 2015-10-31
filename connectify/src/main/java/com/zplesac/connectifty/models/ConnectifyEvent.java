package com.zplesac.connectifty.models;

import com.zplesac.connectifty.Connectify;

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

    public ConnectifyEvent(ConnectifyState state) {
        this.state = state;
        this.type = Connectify.getInstance().getNetworkType();
    }

    public ConnectifyEvent(ConnectifyState state, ConnectifyType type) {
        this.state = state;
        this.type = type;
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

    public ConnectifyStrenght getStrenght() {
        return strenght;
    }

    public void setStrenght(ConnectifyStrenght strenght) {
        this.strenght = strenght;
    }
}
