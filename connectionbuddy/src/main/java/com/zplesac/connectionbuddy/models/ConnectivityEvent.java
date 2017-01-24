package com.zplesac.connectionbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Željko Plesac on 23/09/15.
 * Connectivity event which holds all the necessary data about network connection.
 */
public class ConnectivityEvent implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.state, flags);
        dest.writeParcelable(this.type, flags);
        dest.writeParcelable(this.strength, flags);
    }

    protected ConnectivityEvent(Parcel in) {
        this.state = in.readParcelable(ConnectivityState.class.getClassLoader());
        this.type = in.readParcelable(ConnectivityType.class.getClassLoader());
        this.strength = in.readParcelable(ConnectivityStrength.class.getClassLoader());
    }

    public static final Creator<ConnectivityEvent> CREATOR = new Creator<ConnectivityEvent>() {
        @Override
        public ConnectivityEvent createFromParcel(Parcel source) {
            return new ConnectivityEvent(source);
        }

        @Override
        public ConnectivityEvent[] newArray(int size) {
            return new ConnectivityEvent[size];
        }
    };
}