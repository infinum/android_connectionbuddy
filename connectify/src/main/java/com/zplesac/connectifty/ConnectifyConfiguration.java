package com.zplesac.connectifty;

import com.zplesac.connectifty.models.ConnectifyStrenght;

import android.content.Context;
import android.util.LruCache;

/**
 * Created by Željko Plesac on 09/10/15.
 * Configuration class for Connectify instance. Use this to customize the library behaviour.
 */
public class ConnectifyConfiguration {

    public static final int SIGNAL_STRENGTH_NUMBER_OF_LEVELS = 3;

    private Context context;

    private boolean registeredForWiFiChanges;

    private boolean registeredForMobileNetworkChanges;

    private ConnectifyStrenght minimumSignalStrength;

    private int cacheSize;

    private LruCache<String, Boolean> inMemoryCache;

    private boolean notifyImmediately;

    private ConnectifyConfiguration(Builder builder) {
        this.context = builder.context;
        this.registeredForMobileNetworkChanges = builder.registerForMobileNetworkChanges;
        this.registeredForWiFiChanges = builder.registerForWiFiChanges;
        this.minimumSignalStrength = builder.minimumlSignalStrength;
        this.cacheSize = builder.cacheSize;
        this.inMemoryCache = new LruCache<>(cacheSize);
        this.notifyImmediately = builder.notifyImmediately;
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

    public ConnectifyStrenght getMinimumSignalStrength() {
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
         * Default is set to ConnectifyStrenght.POOR.
         */
        private ConnectifyStrenght minimumlSignalStrength = ConnectifyStrenght.POOR;

        /**
         * Boolean value which defines do we want to notify the listener about current network connection state
         * immediately after the listener has been registered.
         * Default is set to true.
         */
        private boolean notifyImmediately = true;

        private final int kbSize = 1024;

        private final int memoryPart = 10;

        /**
         * Get max available VM memory, exceeding this amount will throw an
         * OutOfMemory exception. Stored in kilobytes as LruCache takes an
         * int in its constructor.
         */
        private final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / kbSize);

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

        public Builder setMinimumSignalStrength(ConnectifyStrenght connectifyStrenght) {
            this.minimumlSignalStrength = connectifyStrenght;
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

        public ConnectifyConfiguration build() {
            return new ConnectifyConfiguration(this);
        }
    }
}
