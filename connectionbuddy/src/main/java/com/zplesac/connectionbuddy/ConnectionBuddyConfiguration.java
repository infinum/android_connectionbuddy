package com.zplesac.connectionbuddy;

import com.zplesac.connectionbuddy.models.ConnectivityStrength;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.LruCache;

/**
 * Created by Željko Plesac on 09/10/15.
 * Configuration class for ConnectionBuddy instance. Use this to customize the library behaviour.
 */
public class ConnectionBuddyConfiguration {

    public static final int SIGNAL_STRENGTH_NUMBER_OF_LEVELS = 3;

    private Context context;

    private boolean registeredForWiFiChanges;

    private boolean registeredForMobileNetworkChanges;

    private ConnectivityStrength minimumSignalStrength;

    private int cacheSize;

    private LruCache<String, Boolean> inMemoryCache;

    private boolean notifyImmediately;

    private ConnectivityManager connectivityManager;

    private boolean notifyOnlyReliableEvents;

    private ConnectionBuddyConfiguration(Builder builder) {
        this.context = builder.context;
        this.registeredForMobileNetworkChanges = builder.registerForMobileNetworkChanges;
        this.registeredForWiFiChanges = builder.registerForWiFiChanges;
        this.minimumSignalStrength = builder.minimumSignalStrength;
        this.cacheSize = builder.cacheSize;
        this.inMemoryCache = new LruCache<>(cacheSize);
        this.notifyImmediately = builder.notifyImmediately;
        this.notifyOnlyReliableEvents = builder.notifyOnlyReliableEvents;
        this.connectivityManager =  (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public Context getContext() {
        return context;
    }

    public boolean isRegisteredForWiFiChanges() {
        return registeredForWiFiChanges;
    }

    public boolean isRegisteredForMobileNetworkChanges() {
        return registeredForMobileNetworkChanges;
    }

    public ConnectivityStrength getMinimumSignalStrength() {
        return minimumSignalStrength;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public LruCache<String, Boolean> getInMemoryCache() {
        return inMemoryCache;
    }

    public boolean isNotifyImmediately() {
        return notifyImmediately;
    }

    public ConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }

    public boolean isNotifyOnlyReliableEvents() {
        return notifyOnlyReliableEvents;
    }

    public static class Builder {

        private Context context;

        private final int kbSize = 1024;

        private final int memoryPart = 10;

        /**
         * Get max available VM memory, exceeding this amount will throw an
         * OutOfMemory exception. Stored in kilobytes as LruCache takes an
         * int in its constructor.
         */
        private final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / kbSize);

        /**
         * Boolean value which defines should we register for WiFi network changes.
         * Default value is set to true.
         */
        private boolean registerForWiFiChanges = true;

        /**
         * Boolean value which defines should we register for mobile network changes.
         * Default value is set to true.
         */
        private boolean registerForMobileNetworkChanges = true;

        /**
         * Define minimum signal strength for which we should call callback listener.
         * Default is set to ConnectivityStrength.POOR.
         */
        private ConnectivityStrength minimumSignalStrength = ConnectivityStrength.POOR;

        /**
         * Boolean value which defines do we want to notify the listener about current network connection state
         * immediately after the listener has been registered.
         * Default is set to true.
         */
        private boolean notifyImmediately = true;

        /**
         * Boolean value which defines do we want to use reliable network events. This means that if we have active internet connection,
         * it will try to execute test network request to determine if user is capable of any network operation.
         * Default is set to false.
         */
        private boolean notifyOnlyReliableEvents = false;

        /**
         * Use 1/10th of the available memory for this memory cache.
         */
        private int cacheSize = maxMemory / memoryPart;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder registerForWiFiChanges(boolean shouldRegister) {
            this.registerForWiFiChanges = shouldRegister;
            return this;
        }

        public Builder registerForMobileNetworkChanges(boolean shouldRegister) {
            this.registerForMobileNetworkChanges = shouldRegister;
            return this;
        }

        public Builder setMinimumSignalStrength(ConnectivityStrength minimumSignalStrength) {
            this.minimumSignalStrength = minimumSignalStrength;
            return this;
        }

        public Builder setCacheSize(int size) {
            this.cacheSize = size;
            return this;
        }

        public Builder setNotifyImmediately(boolean shouldNotify) {
            this.notifyImmediately = shouldNotify;
            return this;
        }

        public Builder notifyOnlyReliableEvents(boolean shouldNotify) {
            this.notifyOnlyReliableEvents = shouldNotify;
            return this;
        }


        public ConnectionBuddyConfiguration build() {
            return new ConnectionBuddyConfiguration(this);
        }
    }
}
