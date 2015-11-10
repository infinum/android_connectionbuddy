package com.zplesac.connectifty.cache;

import com.zplesac.connectifty.Connectify;

/**
 * Created by Željko Plesac on 08/11/15.
 */
public class ConnectifyCache {

    private ConnectifyCache() {
        // empty constructor
    }

    public static boolean getInternetConnection(Object object) {
        if (containsInternetConnection(object)) {
            return Connectify.getInstance().getConfiguration().getInMemoryCache().get(object.toString());
        }

        return true;
    }

    public static void setInternetConnection(Object object, boolean wasActive) {
        Connectify.getInstance().getConfiguration().getInMemoryCache().put(object.toString(), wasActive);
    }

    public static void clearInternetConnection(Object object) {
        Connectify.getInstance().getConfiguration().getInMemoryCache().remove(object.toString());
    }

    public static boolean containsInternetConnection(Object object) {
        return Connectify.getInstance().getConfiguration().getInMemoryCache().snapshot().containsKey(object.toString());
    }
}
