package com.zplesac.connectionbuddy;

import com.zplesac.connectionbuddy.models.ConnectivityStrength;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Željko Plesac on 09/10/15.
 * Configuration class for ConnectionBuddy instance. Use this to customize the library behaviour.
 */
public class ConnectionBuddyConfiguration {

    public static final int SIGNAL_STRENGTH_NUMBER_OF_LEVELS = 3;

    public static final int DEFAULT_NETWORK_EXECUTOR_THREAD_SIZE = 4;

    private Context context;

    private boolean registeredForWiFiChanges;

    private boolean registeredForMobileNetworkChanges;

    private ConnectivityStrength minimumSignalStrength;

    private ConnectionBuddyCache networkEventsCache;

    private boolean notifyImmediately;

    private ConnectivityManager connectivityManager;

    private boolean notifyOnlyReliableEvents;

    private int testNetworkRequestExecutorSize;

    private ConnectionBuddyConfiguration(Builder builder) {
        this.context = builder.context;
        this.registeredForMobileNetworkChanges = builder.registerForMobileNetworkChanges;
        this.registeredForWiFiChanges = builder.registerForWiFiChanges;
        this.minimumSignalStrength = builder.minimumSignalStrength;
        this.notifyImmediately = builder.notifyImmediately;
        this.notifyOnlyReliableEvents = builder.notifyOnlyReliableEvents;
        this.connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        this.testNetworkRequestExecutorSize = builder.testNetworkRequestExecutorSize;

        if (builder.cache != null) {
            this.networkEventsCache = builder.cache;
        } else {
            this.networkEventsCache = new LruConnectionBuddyCache();
        }
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

    public ConnectionBuddyCache getNetworkEventsCache() {
        return networkEventsCache;
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

    public int getTestNetworkRequestExecutorSize() {
        return testNetworkRequestExecutorSize;
    }

    public static class Builder {

        private Context context;

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
         * Default is set to ConnectivityStrength.UNDEFINED.
         */
        private ConnectivityStrength minimumSignalStrength = new ConnectivityStrength(ConnectivityStrength.UNDEFINED);

        /**
         * Boolean value which defines do we want to notify the listener about current network connection state
         * immediately after the listener has been registered.
         * Default is set to true.
         */
        private boolean notifyImmediately = true;

        /**
         * Cache which is used for storing network events.
         */
        private ConnectionBuddyCache cache;

        /**
         * Boolean value which defines do we want to use reliable network events. This means that if we have active internet connection,
         * it will try to execute test network request to determine if user is capable of any network operation.
         * Default is set to false.
         */
        private boolean notifyOnlyReliableEvents = false;

        /**
         * Default network request executor service size.
         */
        private int testNetworkRequestExecutorSize = DEFAULT_NETWORK_EXECUTOR_THREAD_SIZE;

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

        public Builder setNotifyImmediately(boolean shouldNotify) {
            this.notifyImmediately = shouldNotify;
            return this;
        }

        public Builder notifyOnlyReliableEvents(boolean shouldNotify) {
            this.notifyOnlyReliableEvents = shouldNotify;
            return this;
        }

        public Builder setNetworkEventsCache(ConnectionBuddyCache cache) {
            this.cache = cache;
            return this;
        }

        public Builder setTestNetworkRequestExecutorSize(int testNetworkRequestExecutorSize) {
            this.testNetworkRequestExecutorSize = testNetworkRequestExecutorSize;
            return this;
        }

        public ConnectionBuddyConfiguration build() {
            return new ConnectionBuddyConfiguration(this);
        }
    }
}
