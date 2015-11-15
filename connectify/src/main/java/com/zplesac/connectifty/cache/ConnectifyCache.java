package com.zplesac.connectifty.cache;

import com.zplesac.connectifty.Connectify;

/**
 * Created by Željko Plesac on 08/11/15.
 *
 * Utility class which manages in memory LruCache, used to store last connection state
 * for activities/fragments.
 */
public class ConnectifyCache {

    private ConnectifyCache() {
        // empty constructor
    }

    /**
     * Fetches last stored network connection state for provided object.
     * @param object Activity or fragment for which we want to fetch last network connection state.
     * @return Boolean property which indicates whether provided object had network connection when
     *         it was stored in cache.
     */
    public static boolean getInternetConnection(Object object) {
        if (containsInternetConnection(object)) {
            return Connectify.getInstance().getConfiguration().getInMemoryCache().get(object.toString());
        }

        return false;
    }

    /**
     * Store last network connection state for provided object.
     * @param object Activity or fragment for which we want to cache network connectivity state.
     * @param isActive Does provided object has active network connection.
     */
    public static void setInternetConnection(Object object, boolean isActive) {
        Connectify.getInstance().getConfiguration().getInMemoryCache().put(object.toString(), isActive);
    }

    /**
     * Clear stored network connectivity state for provided object.
     * @param object Activity or fragment for which we want to delete last stored state.
     */
    public static void clearInternetConnection(Object object) {
        Connectify.getInstance().getConfiguration().getInMemoryCache().remove(object.toString());
    }

    /**
     * Check whether we have stored network connectivity state for provided object.
     * @param object Activity or fragment for which we want to check if we have stored last network connectivity state.
     * @return Boolean property which indicates do we have stored last network state for provided object.
     */
    public static boolean containsInternetConnection(Object object) {
        return Connectify.getInstance().getConfiguration().getInMemoryCache().snapshot().containsKey(object.toString());
    }
}
