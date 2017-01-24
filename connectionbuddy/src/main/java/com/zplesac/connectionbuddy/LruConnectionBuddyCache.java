package com.zplesac.connectionbuddy;

import android.support.v4.util.LruCache;

class LruConnectionBuddyCache implements ConnectionBuddyCache {

    private final int kbSize = 1024;

    private final int memoryPart = 10;

    /**
     * Get max available VM memory, exceeding this amount will throw an
     * OutOfMemory exception. Stored in kilobytes as LruCache takes an
     * int in its constructor.
     */
    private final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / kbSize);

    private LruCache<String, Boolean> cache;

    LruConnectionBuddyCache() {
        // Use 1/10th of the available memory for this memory cache.
        this.cache = new LruCache<>(maxMemory / memoryPart);
    }

    @Override
    public boolean getLastNetworkState(Object object) {
        if (isLastNetworkStateStored(object)) {
            return cache.get(object.toString());
        }

        return true;
    }

    @Override
    public void setLastNetworkState(Object object, boolean isActive) {
        cache.put(object.toString(), isActive);
    }

    @Override
    public void clearLastNetworkState(Object object) {
        cache.remove(object.toString());
    }

    @Override
    public boolean isLastNetworkStateStored(Object object) {
        return cache.snapshot().containsKey(object.toString());
    }
}
