package com.zplesac.connectionbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

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

    public ConnectivityStrength(@ConnectivityStrengthDef int value) {
        this.value = value;
    }

    protected ConnectivityStrength(@NonNull Parcel in) {
        this.value = in.readInt();
    }

    @NonNull
    @Override
    public String toString() {
        switch (value) {
            case UNDEFINED:
                return "UNDEFINED";
            case POOR:
                return "POOR";
            case GOOD:
                return "GOOD";
            case EXCELLENT:
                return "EXCELLENT";
            default:
                return "";
        }
    }

    @ConnectivityStrengthDef
    public int getValue() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(this.value);
    }

    public static final Creator<ConnectivityStrength> CREATOR = new Creator<ConnectivityStrength>() {

        @NonNull
        @Override
        public ConnectivityStrength createFromParcel(@NonNull Parcel source) {
            return new ConnectivityStrength(source);
        }

        @NonNull
        @Override
        public ConnectivityStrength[] newArray(int size) {
            return new ConnectivityStrength[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UNDEFINED, POOR, GOOD, EXCELLENT})
    public @interface ConnectivityStrengthDef {}
}
