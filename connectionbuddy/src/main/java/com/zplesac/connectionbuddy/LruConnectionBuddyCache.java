package com.zplesac.connectionbuddy;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

class LruConnectionBuddyCache implements ConnectionBuddyCache {

    @NonNull
    private LruCache<String, Boolean> cache;

    LruConnectionBuddyCache() {
        int kbSize = 1024;
        int memoryPart = 10;

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / kbSize);

        // Use 1/10th of the available memory for this memory cache.
        this.cache = new LruCache<>(maxMemory / memoryPart);
    }

    @Override
    public boolean getLastNetworkState(@NonNull Object object) {
        if (isLastNetworkStateStored(object)) {
            Boolean state = cache.get(object.toString());
            if (state != null) {
                return state;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public void setLastNetworkState(@NonNull Object object, boolean isActive) {
        cache.put(object.toString(), isActive);
    }

    @Override
    public void clearLastNetworkState(@NonNull Object object) {
        cache.remove(object.toString());
    }

    @Override
    public boolean isLastNetworkStateStored(@NonNull Object object) {
        return cache.snapshot().containsKey(object.toString());
    }
}
