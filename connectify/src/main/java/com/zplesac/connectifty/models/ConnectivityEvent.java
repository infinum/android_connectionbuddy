package com.zplesac.connectifty.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Željko Plesac on 23/09/15.
 */
public class ConnectivityEvent implements Serializable {

    private ConnectivityState state;

    private ConnectivityType type;

    private Date dateUpadated;

    public ConnectivityEvent() {
    }

    public ConnectivityEvent(ConnectivityState state, ConnectivityType type, Date dateUpadated) {
        this.state = state;
        this.type = type;
        this.dateUpadated = dateUpadated;
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

    public Date getDateUpadated() {
        return dateUpadated;
    }

    public void setDateUpadated(Date dateUpadated) {
        this.dateUpadated = dateUpadated;
    }
}
