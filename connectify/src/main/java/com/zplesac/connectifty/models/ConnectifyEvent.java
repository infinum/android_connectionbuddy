package com.zplesac.connectifty.models;

import com.zplesac.connectifty.ConnectifyUtils;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by Željko Plesac on 23/09/15.
 * Connectivity event which holds all the necessary data about network connection.
 */
public class ConnectifyEvent implements Serializable {

    private ConnectifyState state;

    private ConnectifyType type;

    public ConnectifyEvent() {
    }

    public ConnectifyEvent(Context context, ConnectifyState state) {
        this.state = state;
        this.type = ConnectifyUtils.getNetworkType(context);
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
}
