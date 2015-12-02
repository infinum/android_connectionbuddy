package com.zplesac.networkinspector.models;

import java.io.Serializable;

/**
 * Created by Željko Plesac on 23/09/15.
 * Connectivity event which holds all the necessary data about network connection.
 */
public class ConnectivityEvent implements Serializable {

    private ConnectivityState state;

    private ConnectivityType type;

    private ConnectivityInspectorStrenght strenght;

    public ConnectivityEvent() {
    }

    public ConnectivityEvent(ConnectivityState state, ConnectivityType type, ConnectivityInspectorStrenght strenght) {
        this.state = state;
        this.type = type;
        this.strenght = strenght;
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

    public ConnectivityInspectorStrenght getStrenght() {
        return strenght;
    }

    public void setStrenght(ConnectivityInspectorStrenght strenght) {
        this.strenght = strenght;
    }
}