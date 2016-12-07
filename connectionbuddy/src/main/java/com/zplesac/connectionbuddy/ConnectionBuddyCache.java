package com.zplesac.connectionbuddy;

public interface ConnectionBuddyCache {

    /**
     * Fetches last stored network connection state for provided object.
     * @param object Activity or fragment for which we want to fetch last network connection state.
     * @return Boolean property which indicates whether provided object had network connection when
     *         it was stored in cache.
     */
    boolean getLastNetworkState(Object object);

    /**
     * Store last network connection state for provided object.
     * @param object Activity or fragment for which we want to cache network connectivity state.
     * @param isActive Does provided object has active network connection.
     */
    void setLastNetworkState(Object object, boolean isActive);

    /**
     * Clear stored network connectivity state for provided object.
     * @param object Activity or fragment for which we want to delete last stored state.
     */
    void clearLastNetworkState(Object object);

    /**
     * Check whether we have stored network connectivity state for provided object.
     * @param object Activity or fragment for which we want to check if we have stored last network connectivity state.
     * @return Boolean property which indicates do we have stored last network state for provided object.
     */
    boolean isLastNetworkStateStored(Object object);
}
