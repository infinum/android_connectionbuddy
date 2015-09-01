package co.infinum.connectionutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zeljkoplesac on 06/10/14.
 */
public class ConnectionPreferences {

    private ConnectionPreferences() {
        // empty constructor
    }

    public static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static boolean getInternetConnection(Context ctx, Object object) {
        boolean internetConnection = getSharedPreferences(ctx).getBoolean(object.toString(), true);
        return internetConnection;
    }

    public static void setInternetConnection(Context ctx, Object object, boolean wasActive) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(object.toString(), wasActive);
        editor.commit();
    }

    public static void clearInternetConnection(Context ctx, Object object) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(object.toString());
        editor.commit();
    }

    public static boolean containsInternetConnection(Context ctx, Object object) {
        boolean contains = getSharedPreferences(ctx).contains(object.toString());
        return contains;
    }

}
