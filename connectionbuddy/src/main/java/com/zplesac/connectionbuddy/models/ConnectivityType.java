package com.zplesac.connectionbuddy.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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

    @ConnectivityTypeDef
    public int getValue() {
        return value;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            UNDEFINED,
            WIFI,
            MOBILE,
            NONE
    })
    public @interface ConnectivityTypeDef {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.value);
    }

    protected ConnectivityType(Parcel in) {
        this.value = in.readInt();
    }

    public static final Creator<ConnectivityType> CREATOR = new Creator<ConnectivityType>() {
        @Override
        public ConnectivityType createFromParcel(Parcel source) {
            return new ConnectivityType(source);
        }

        @Override
        public ConnectivityType[] newArray(int size) {
            return new ConnectivityType[size];
        }
    };
}
