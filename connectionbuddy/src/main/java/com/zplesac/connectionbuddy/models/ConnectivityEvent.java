package com.zplesac.connectionbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Created by Željko Plesac on 23/09/15.
 * Connectivity event which holds all the necessary data about network connection.
 */
public class ConnectivityEvent implements Parcelable {

    @NonNull
    private ConnectivityState state;

    @NonNull
    private ConnectivityType type;

    @NonNull
    private ConnectivityStrength strength;

    /**
     * Initializes the object with DISCONNECTED state, NONE type, and UNDEFINED strength.
     */
    public ConnectivityEvent() {
        state = new ConnectivityState(ConnectivityState.DISCONNECTED);
        type = new ConnectivityType(ConnectivityType.NONE);
        strength = new ConnectivityStrength(ConnectivityStrength.UNDEFINED);
    }

    public ConnectivityEvent(
        @NonNull ConnectivityState state,
        @NonNull ConnectivityType type,
        @NonNull ConnectivityStrength strength
    ) {
        this.state = state;
        this.type = type;
        this.strength = strength;
    }

    @NonNull
    public ConnectivityState getState() {
        return state;
    }

    public void setState(@NonNull ConnectivityState state) {
        this.state = state;
    }

    @NonNull
    public ConnectivityType getType() {
        return type;
    }

    public void setType(@NonNull ConnectivityType type) {
        this.type = type;
    }

    @NonNull
    public ConnectivityStrength getStrength() {
        return strength;
    }

    public void setStrength(@NonNull ConnectivityStrength strength) {
        this.strength = strength;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(this.state, flags);
        dest.writeParcelable(this.type, flags);
        dest.writeParcelable(this.strength, flags);
    }

    protected ConnectivityEvent(@NonNull Parcel in) {
        ConnectivityState state = in.readParcelable(ConnectivityState.class.getClassLoader());
        ConnectivityType type = in.readParcelable(ConnectivityType.class.getClassLoader());
        ConnectivityStrength strength = in.readParcelable(ConnectivityStrength.class.getClassLoader());

        if (state == null || type == null || strength == null) {
            throw new IllegalStateException("Some of the read Parcelable objects were null.");
        }

        this.state = state;
        this.type = type;
        this.strength = strength;
    }

    public static final Creator<ConnectivityEvent> CREATOR = new Creator<ConnectivityEvent>() {

        @NonNull
        @Override
        public ConnectivityEvent createFromParcel(@NonNull Parcel source) {
            return new ConnectivityEvent(source);
        }

        @NonNull
        @Override
        public ConnectivityEvent[] newArray(int size) {
            return new ConnectivityEvent[size];
        }
    };
}
