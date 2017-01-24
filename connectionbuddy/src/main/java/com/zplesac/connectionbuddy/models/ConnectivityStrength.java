package com.zplesac.connectionbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Željko Plesac on 25/10/15.
 * Magic constant which defines signal strength of current network connection.
 */
public class ConnectivityStrength implements Parcelable {

    public static final int UNDEFINED = -1;

    public static final int POOR = 0;

    public static final int GOOD = 1;

    public static final int EXCELLENT = 2;

    private final int value;

    public ConnectivityStrength(int value) {
        this.value = value;
    }

    @ConnectivityStrengthDef
    public int getValue() {
        return value;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            UNDEFINED,
            POOR,
            GOOD,
            EXCELLENT
    })
    public @interface ConnectivityStrengthDef {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.value);
    }

    protected ConnectivityStrength(Parcel in) {
        this.value = in.readInt();
    }

    public static final Creator<ConnectivityStrength> CREATOR = new Creator<ConnectivityStrength>() {
        @Override
        public ConnectivityStrength createFromParcel(Parcel source) {
            return new ConnectivityStrength(source);
        }

        @Override
        public ConnectivityStrength[] newArray(int size) {
            return new ConnectivityStrength[size];
        }
    };
}
