package com.zplesac.connectifty;

import com.zplesac.connectifty.models.ConnectifyStrenght;

import android.content.Context;

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

    private ConnectifyConfiguration(Builder builder) {
        this.context = builder.context;
        this.registeredForMobileNetworkChanges = builder.registerForMobileNetworkChanges;
        this.registeredForWiFiChanges = builder.registerForWiFiChanges;
        this.minimumSignalStrength = builder.minimumlSignalStrength;
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

    public static class Builder {

        private Context context;

        /**
         * Bool value which defines should we register for WiFi network changes.
         * Default value is set to true.
         */
        private boolean registerForWiFiChanges = true;

        /**
         * Bool value which defines should we register for mobile network changes.
         * Default value is set to true.
         */
        private boolean registerForMobileNetworkChanges = true;

        /**
         * Define minimum signal strength for which we should call callback listener.
         * Default is set to ConnectifyStrenght.POOR.
         */
        private ConnectifyStrenght minimumlSignalStrength = ConnectifyStrenght.POOR;

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

        public ConnectifyConfiguration build() {
            return new ConnectifyConfiguration(this);
        }
    }
}
