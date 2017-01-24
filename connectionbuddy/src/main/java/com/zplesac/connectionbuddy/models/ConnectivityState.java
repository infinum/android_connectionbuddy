package com.zplesac.connectionbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Željko Plesac on 23/09/15.
 * Magic constant which defines network connection states.
 */
public class ConnectivityState implements Parcelable {

    public static final int DISCONNECTED = 0;

    public static final int CONNECTED = 1;

    private final int value;

    public ConnectivityState(int value) {
        this.value = value;
    }

    @ConnectivityStateDef
    public int getValue() {
        return value;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            DISCONNECTED,
            CONNECTED
    })
    public @interface ConnectivityStateDef {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.value);
    }

    protected ConnectivityState(Parcel in) {
        this.value = in.readInt();
    }

    public static final Creator<ConnectivityState> CREATOR = new Creator<ConnectivityState>() {
        @Override
        public ConnectivityState createFromParcel(Parcel source) {
            return new ConnectivityState(source);
        }

        @Override
        public ConnectivityState[] newArray(int size) {
            return new ConnectivityState[size];
        }
    };
}
