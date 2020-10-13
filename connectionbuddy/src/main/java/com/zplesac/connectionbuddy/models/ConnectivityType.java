package com.zplesac.connectionbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

/**
 * Created by Željko Plesac on 23/09/15.
 * Magic constant which defines different network connection type. Device can only have one ConnectivityType at a time.
 */
public class ConnectivityType implements Parcelable {

    public static final int UNDEFINED = -1;
    public static final int WIFI = 0;
    public static final int MOBILE = 1;
    public static final int NONE = 2;

    private final int value;

    public ConnectivityType(@ConnectivityTypeDef int value) {
        this.value = value;
    }

    protected ConnectivityType(@NonNull Parcel in) {
        this.value = in.readInt();
    }

    @NonNull
    @Override
    public String toString() {
        switch (value) {
            case UNDEFINED:
                return "UNDEFINED";
            case WIFI:
                return "WIFI";
            case MOBILE:
                return "MOBILE";
            case NONE:
                return "NONE";
            default:
                return "";
        }
    }

    @ConnectivityTypeDef
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

    public static final Creator<ConnectivityType> CREATOR = new Creator<ConnectivityType>() {

        @NonNull
        @Override
        public ConnectivityType createFromParcel(@NonNull Parcel source) {
            return new ConnectivityType(source);
        }

        @NonNull
        @Override
        public ConnectivityType[] newArray(int size) {
            return new ConnectivityType[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UNDEFINED, WIFI, MOBILE, NONE})
    public @interface ConnectivityTypeDef {}
}
