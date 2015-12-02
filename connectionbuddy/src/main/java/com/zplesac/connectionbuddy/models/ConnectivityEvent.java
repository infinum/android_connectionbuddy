package com.zplesac.connectionbuddy.models;

import java.io.Serializable;

/**
 * Created by Željko Plesac on 23/09/15.
 * Connectivity event which holds all the necessary data about network connection.
 */
public class ConnectivityEvent implements Serializable {

    private ConnectivityState state;

    private ConnectivityType type;

    private ConnectivityStrength strength;

    public ConnectivityEvent() {
    }

    public ConnectivityEvent(ConnectivityState state, ConnectivityType type, ConnectivityStrength strength) {
        this.state = state;
        this.type = type;
        this.strength = strength;
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

    public ConnectivityStrength getStrength() {
        return strength;
    }

    public void setStrength(ConnectivityStrength strength) {
        this.strength = strength;
    }
}