package com.zplesac.connectifty;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Željko Plesac on 06/10/14.
 */
public class ConnectifyPreferences {

    private ConnectifyPreferences() {
        // empty constructor
    }

    public static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(Connectify.getInstance().getConfiguration().getContext());
    }

    public static boolean getInternetConnection(Object object) {
        boolean internetConnection = getSharedPreferences().getBoolean(object.toString(), true);
        return internetConnection;
    }

    public static void setInternetConnection(Object object, boolean wasActive) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(object.toString(), wasActive);
        editor.commit();
    }

    public static void clearInternetConnection(Object object) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(object.toString());
        editor.commit();
    }

    public static boolean containsInternetConnection(Object object) {
        boolean contains = getSharedPreferences().contains(object.toString());
        return contains;
    }

}
