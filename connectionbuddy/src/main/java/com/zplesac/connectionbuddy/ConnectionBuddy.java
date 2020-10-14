package com.zplesac.connectionbuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;

import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.interfaces.NetworkRequestCheckListener;
import com.zplesac.connectionbuddy.interfaces.WifiConnectivityListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;
import com.zplesac.connectionbuddy.models.ConnectivityStrength;
import com.zplesac.connectionbuddy.models.ConnectivityType;

import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by Željko Plesac on 06/10/14.
 */
public class ConnectionBuddy {

    private static final String NOT_INITIALIZED_ERROR = "ConnectionBuddy not initialized.";
    private static final String NETWORK_CHECK_URL_GOOGLE_DNS = "8.8.8.8";

    private static final int DNS_PORT = 53;
    private static final int CONNECTION_TIMEOUT = 1500;
    private static final int WIFI_CONNECTION_TIMEOUT_MS = 15_000;

    private static volatile ConnectionBuddy instance;

    @NonNull
    private Map<String, NetworkChangeReceiver> networkReceiversHashMap = new HashMap<>();

    @Nullable
    private WifiScanResultReceiver wifiScanResultReceiver;

    @Nullable
    private WifiConnectionStateChangedReceiver wifiConnectionStateChangedReceiver;

    @Nullable
    private ConnectionBuddyConfiguration configuration;

    @Nullable
    private ExecutorService executor;

    /**
     * Get current library instance.
     *
     * @return Current library instance.
     */
    public static ConnectionBuddy getInstance() {
        if (instance == null) {
            synchronized (ConnectionBuddy.class) {
                if (instance == null) {
                    instance = new ConnectionBuddy();
                }
            }
        }
        return instance;
    }

    @Contract("null -> fail")
    private static void assertNotNull(ConnectionBuddyConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalStateException(NOT_INITIALIZED_ERROR);
        }
    }

    protected ConnectionBuddy() {
        // Empty constructor.
    }

    /**
     * Initialize this instance with provided configuration.
     *
     * @param configuration ConnectionBuddy configuration which is used in instance.
     */
    public synchronized void init(@NonNull ConnectionBuddyConfiguration configuration) {
        if (this.configuration == null) {
            this.configuration = configuration;
        }
    }

    @NonNull
    public ConnectionBuddyConfiguration getConfiguration() {
        assertNotNull(configuration);
        return configuration;
    }

    /**
     * Register for network connectivity events. Must be called separately for each activity/context, and will use
     * global configuration to determine if we should notify the callback immediately about current network connection
     * state.
     *
     * @param object   Object which is registered to network change receiver.
     * @param listener Callback listener.
     */
    public void registerForConnectivityEvents(@NonNull Object object, @NonNull ConnectivityChangeListener listener) {
        assertNotNull(configuration);
        registerForConnectivityEvents(object, configuration.isNotifyImmediately(), listener);
    }

    /**
     * Register for network connectivity events. Must be called separately for each activity/context.
     *
     * @param object            Object which is registered to network change receiver.
     * @param notifyImmediately Indicates should we immediately notify the callback about current network connection state.
     * @param listener          Callback listener.
     */
    public void registerForConnectivityEvents(
        @NonNull Object object,
        boolean notifyImmediately,
        @NonNull ConnectivityChangeListener listener
    ) {
        assertNotNull(configuration);
        if (!isAlreadyRegistered(object)) {
            boolean hasConnection = hasNetworkConnection();
            ConnectionBuddyCache cache = configuration.getNetworkEventsCache();

            if (cache.isLastNetworkStateStored(object) && cache.getLastNetworkState(object) != hasConnection) {
                cache.setLastNetworkState(object, hasConnection);
                if (notifyImmediately) {
                    notifyConnectionChange(hasConnection, listener);
                }
            } else if (!cache.isLastNetworkStateStored(object)) {
                cache.setLastNetworkState(object, hasConnection);
                if (notifyImmediately) {
                    notifyConnectionChange(hasConnection, listener);
                }
            }

            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

            NetworkChangeReceiver receiver = new NetworkChangeReceiver(object, listener);

            networkReceiversHashMap.put(object.toString(), receiver);
            configuration.getContext().registerReceiver(receiver, filter);
        }
    }

    /**
     * @param object Activity or fragment, which is registered for connectivity state changes.
     * @return true if the object is already registered to a network change receiver
     */
    private boolean isAlreadyRegistered(@NonNull Object object) {
        return networkReceiversHashMap.containsKey(object.toString());
    }

    /**
     * Clears network events cache. Has to be called in onCreate() lifecycle methods for activities and fragments, so that we always reset
     * the previous connection state.
     *
     * @param object Activity or fragment, which is registered for connectivity state changes.
     */
    public void clearNetworkCache(@NonNull Object object) {
        assertNotNull(configuration);
        configuration.getNetworkEventsCache().clearLastNetworkState(object);
    }

    /**
     * Clears network events cache. Has to be called in onCreate() lifecycle methods for activities and fragments, so that we always reset
     * the previous connection state.
     *
     * @param object             Activity or fragment, which is registered for connectivity state changes.
     * @param savedInstanceState Activity or fragments bundle.
     */
    public void clearNetworkCache(@NonNull Object object, @Nullable Bundle savedInstanceState) {
        assertNotNull(configuration);
        if (savedInstanceState != null) {
            configuration.getNetworkEventsCache().clearLastNetworkState(object);
        }
    }

    /**
     * Notify the current state of connection to provided interface listener.
     *
     * @param hasConnection Current state of internet connection.
     * @param listener      Interface listener which has to be notified about current internet connection state.
     */
    public void notifyConnectionChange(boolean hasConnection, @NonNull final ConnectivityChangeListener listener) {
        assertNotNull(configuration);
        if (hasConnection) {
            final ConnectivityEvent event = new ConnectivityEvent(
                new ConnectivityState(ConnectivityState.CONNECTED),
                getNetworkType(),
                getSignalStrength()
            );

            if (configuration.isNotifyOnlyReliableEvents()) {
                testNetworkRequest(new NetworkRequestCheckListener() {
                    @Override
                    public void onResponseObtained() {
                        handleActiveInternetConnection(event, listener);
                    }

                    @Override
                    public void onNoResponse() {
                        // No response was obtained from test network request, which means that we have active internet connection,
                        // but user can't perform network requests (I.E. he uses mobile network and doesn't have enough credit.
                        // Don't notify about this connection event.
                    }
                });
            } else {
                handleActiveInternetConnection(event, listener);
            }
        } else {
            listener.onConnectionChange(new ConnectivityEvent(
                new ConnectivityState(ConnectivityState.DISCONNECTED),
                new ConnectivityType(ConnectivityType.NONE),
                new ConnectivityStrength(ConnectivityStrength.UNDEFINED)
            ));
        }
    }

    /**
     * Determine if we should notify the listener about active internet connection, based on configuration values.
     *
     * @param event    ConnectivityEvent which will be posted to listener.
     * @param listener ConnectivityChangeListener which will receive ConnectivityEvent.
     */
    private void handleActiveInternetConnection(@NonNull ConnectivityEvent event, @NonNull ConnectivityChangeListener listener) {
        assertNotNull(configuration);
        // handle only if signal strength is above or equal minimum defined strength
        if (event.getStrength().getValue() >= configuration.getMinimumSignalStrength().getValue()) {
            if (event.getType().getValue() == ConnectivityType.MOBILE && configuration.isRegisteredForMobileNetworkChanges()) {
                listener.onConnectionChange(event);
            } else if (event.getType().getValue() == ConnectivityType.WIFI && configuration.isRegisteredForWiFiChanges()) {
                listener.onConnectionChange(event);
            }
        }
    }

    /**
     * Unregister from network connectivity events.
     *
     * @param object Object which we want to unregister from connectivity changes.
     */
    public void unregisterFromConnectivityEvents(@NonNull Object object) {
        assertNotNull(configuration);

        NetworkChangeReceiver networkChangeReceiver = networkReceiversHashMap.get(object.toString());
        configuration.getContext().unregisterReceiver(networkChangeReceiver);
        networkReceiversHashMap.remove(object.toString());

        if (wifiScanResultReceiver != null) {
            configuration.getContext().unregisterReceiver(wifiScanResultReceiver);
            wifiScanResultReceiver = null;
        }

        if (wifiConnectionStateChangedReceiver != null) {
            configuration.getContext().unregisterReceiver(wifiConnectionStateChangedReceiver);
            wifiConnectionStateChangedReceiver = null;
        }
    }

    /**
     * Utility method which checks current network connection state.
     *
     * @return True if we have active network connection, false otherwise.
     */
    public boolean hasNetworkConnection() {
        assertNotNull(configuration);
        NetworkInfo networkInfo = configuration.getConnectivityManager().getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Utility method which checks current network connection state, but will also try to perform test network request, in order
     * to determine if user can actually perform any network operation.
     *
     * @param listener Callback listener.
     */
    public void hasNetworkConnection(@NonNull NetworkRequestCheckListener listener) {
        if (hasNetworkConnection()) {
            testNetworkRequest(listener);
        } else {
            listener.onNoResponse();
        }
    }

    /**
     * Try to perform test network request to NETWORK_CHECK_URL_GOOGLE_DNS. The request is executed on a separate non-UI thread.
     * This way, we can determine if use is in fact capable of performing any network operations when he has active internet connection.
     *
     * @param listener Callback listener.
     */
    private void testNetworkRequest(@NonNull final NetworkRequestCheckListener listener) {
        assertNotNull(configuration);

        if (executor == null) {
            executor = Executors.newFixedThreadPool(configuration.getTestNetworkRequestExecutorSize());
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(NETWORK_CHECK_URL_GOOGLE_DNS, DNS_PORT);
                    socket.connect(socketAddress, CONNECTION_TIMEOUT);
                    socket.close();
                    callbackExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            listener.onResponseObtained();
                        }
                    });
                } catch (IOException e) {
                    callbackExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            listener.onNoResponse();
                        }
                    });
                }
            }
        });
    }

    /**
     * Get network connection type from ConnectivityManager.
     *
     * @return ConnectivityType which is available on current device.
     */
    @NonNull
    public ConnectivityType getNetworkType() {
        assertNotNull(configuration);
        NetworkInfo networkInfo = configuration.getConnectivityManager().getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return new ConnectivityType(ConnectivityType.WIFI);
                case ConnectivityManager.TYPE_MOBILE:
                    return new ConnectivityType(ConnectivityType.MOBILE);
                default:
                    return new ConnectivityType(ConnectivityType.UNDEFINED);
            }
        } else {
            return new ConnectivityType(ConnectivityType.NONE);
        }
    }

    /**
     * Get signal strength of current network connection.
     *
     * @return ConnectivityStrength object for current network connection.
     */
    @NonNull
    public ConnectivityStrength getSignalStrength() {
        assertNotNull(configuration);
        NetworkInfo networkInfo = configuration.getConnectivityManager().getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return getWifiStrength();
            } else {
                return getMobileConnectionStrength(networkInfo);
            }
        } else {
            return new ConnectivityStrength(ConnectivityStrength.UNDEFINED);
        }
    }

    /**
     * Get WiFi signal strength.
     */
    @NonNull
    private ConnectivityStrength getWifiStrength() {
        assertNotNull(configuration);
        WifiInfo wifiInfo = configuration.getWifiManager().getConnectionInfo();
        if (wifiInfo != null) {
            int level = WifiManager.calculateSignalLevel(
                wifiInfo.getRssi(),
                ConnectionBuddyConfiguration.SIGNAL_STRENGTH_NUMBER_OF_LEVELS
            );

            switch (level) {
                case 0:
                    return new ConnectivityStrength(ConnectivityStrength.POOR);
                case 1:
                    return new ConnectivityStrength(ConnectivityStrength.GOOD);
                case 2:
                    return new ConnectivityStrength(ConnectivityStrength.EXCELLENT);
                default:
                    return new ConnectivityStrength(ConnectivityStrength.UNDEFINED);
            }
        } else {
            return new ConnectivityStrength(ConnectivityStrength.UNDEFINED);
        }
    }

    /**
     * Get mobile network signal strength.
     */
    @NonNull
    private ConnectivityStrength getMobileConnectionStrength(@Nullable NetworkInfo info) {
        if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            switch (info.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return new ConnectivityStrength(ConnectivityStrength.POOR);
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return new ConnectivityStrength(ConnectivityStrength.GOOD);
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return new ConnectivityStrength(ConnectivityStrength.EXCELLENT);
                default:
                    return new ConnectivityStrength(ConnectivityStrength.UNDEFINED);
            }
        } else {
            return new ConnectivityStrength(ConnectivityStrength.UNDEFINED);
        }
    }

    /**
     * Checks if user is on is in roaming.
     *
     * @return boolean variable, which describes if user is in roaming.
     */
    public boolean isOnRoaming() {
        assertNotNull(configuration);
        NetworkInfo networkInfo = configuration.getConnectivityManager().getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isRoaming();
    }

    /**
     * Connects to the WiFi configuration with given {@param networkSsid} as network configuration's SSID and {@param networkPassword} as
     * network configurations's password.
     * {@link android.Manifest.permission#ACCESS_COARSE_LOCATION} and {@link android.Manifest.permission#ACCESS_FINE_LOCATION} permissions
     * are required in order to initiate new access point scan.
     *
     * @param networkSsid     WifiConfiguration network SSID.
     * @param networkPassword WifiConfiguration network password.
     */
    @RequiresPermission(allOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void connectToWifiConfiguration(
        @NonNull Context context,
        @NonNull String networkSsid,
        @NonNull String networkPassword,
        boolean disconnectIfNotFound
    ) throws SecurityException {
        connectToWifiConfiguration(context, networkSsid, networkPassword, disconnectIfNotFound, null);
    }

    /**
     * Connects to the WiFi configuration with given {@param networkSsid} as network configuration's SSID and {@param networkPassword} as
     * network configurations's password and optionally notifies about the result if {@param listener} has defined value.
     * {@link android.Manifest.permission#ACCESS_COARSE_LOCATION} and {@link android.Manifest.permission#ACCESS_FINE_LOCATION} permissions
     * are required in order to initiate new access point scan.
     *
     * @param networkSsid     WifiConfiguration network SSID.
     * @param networkPassword WifiConfiguration network password.
     * @param listener        Callback listener.
     */
    @RequiresPermission(allOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void connectToWifiConfiguration(
        @NonNull Context context,
        @NonNull String networkSsid,
        @NonNull String networkPassword,
        boolean disconnectIfNotFound,
        @Nullable WifiConnectivityListener listener
    ) throws SecurityException {
        assertNotNull(configuration);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectToWifiUsingNetworkSpecifier(networkSsid, networkPassword, listener);
            return;
        }

        // Check if permissions have been granted
        if (ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION permissions have not been granted by the user.");
        } else {
            if (!configuration.getWifiManager().isWifiEnabled()) {
                configuration.getWifiManager().setWifiEnabled(true);
            }

            wifiScanResultReceiver = new WifiScanResultReceiver(
                configuration.getWifiManager(),
                networkSsid,
                networkPassword,
                disconnectIfNotFound,
                listener
            );

            configuration.getContext().registerReceiver(
                wifiScanResultReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            );

            configuration.getWifiManager().startScan();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void connectToWifiUsingNetworkSpecifier(
        @NonNull String ssid,
        @NonNull String preSharedKey,
        @Nullable WifiConnectivityListener listener
    ) {
        assertNotNull(configuration);

        NetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(preSharedKey)
            .build();

        NetworkRequest request = new NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(specifier)
            .build();

        configuration.getConnectivityManager().requestNetwork(
            request,
            new WifiNetworkCallback(configuration.getConnectivityManager(), listener),
            WIFI_CONNECTION_TIMEOUT_MS
        );
    }

    /**
     * Called from WifiScanResultReceiver, when WiFiManager has finished scanning for active access points. Note that SSIDs of the
     * configured networks are enclosed in double quotes, whilst the SSIDs returned in ScanResults are not.
     */
    private class WifiScanResultReceiver extends BroadcastReceiver {

        @NonNull
        private WifiManager wifiManager;

        @Nullable
        private WifiConnectivityListener listener;

        @NonNull
        private String networkSsid;

        @NonNull
        private String networkPassword;

        private boolean disconnectIfNotFound;

        WifiScanResultReceiver(
            @NonNull WifiManager wifiManager,
            @NonNull String networkSsid,
            @NonNull String networkPassword,
            boolean disconnectIfNotFound,
            @Nullable WifiConnectivityListener listener
        ) {
            this.wifiManager = wifiManager;
            this.listener = listener;
            this.networkSsid = networkSsid;
            this.networkPassword = networkPassword;
            this.disconnectIfNotFound = disconnectIfNotFound;
        }

        @Override
        public void onReceive(@NonNull Context context, Intent intent) {
            assertNotNull(configuration);

            // unregister receiver, so that we are only notified once about the results
            context.unregisterReceiver(this);

            if (wifiManager.getScanResults() != null && wifiManager.getScanResults().size() > 0) {
                for (ScanResult scanResult : wifiManager.getScanResults()) {
                    if (scanResult.SSID != null && scanResult.SSID.equals(networkSsid)) {
                        int networkId;
                        WifiConfiguration wifiConfiguration = checkIfWifiAlreadyConfigured(wifiManager.getConfiguredNetworks());

                        if (wifiConfiguration == null) {
                            networkId = wifiManager.addNetwork(createWifiConfiguration(scanResult, networkPassword));
                        } else {
                            wifiConfiguration.preSharedKey = networkPassword;
                            networkId = wifiManager.updateNetwork(wifiConfiguration);
                            if (networkId == -1) {
                                networkId = wifiConfiguration.networkId;
                            }
                        }

                        wifiConnectionStateChangedReceiver = new WifiConnectionStateChangedReceiver(
                            networkSsid,
                            wifiManager,
                            disconnectIfNotFound,
                            listener
                        );

                        configuration.getContext().registerReceiver(
                            wifiConnectionStateChangedReceiver,
                            new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                        );

                        wifiManager.enableNetwork(networkId, true);

                        return;
                    }
                }
            }

            if (listener != null) {
                listener.onNotFound();
            }
        }

        @Nullable
        private WifiConfiguration checkIfWifiAlreadyConfigured(@Nullable List<WifiConfiguration> wifiConfigurationList) {
            if (wifiConfigurationList != null && !wifiConfigurationList.isEmpty()) {
                for (WifiConfiguration configuration : wifiConfigurationList) {
                    if (configuration.SSID != null && configuration.SSID.equals("\"" + networkSsid + "\"")) {
                        return configuration;
                    }
                }
            }
            return null;
        }

        @NonNull
        private WifiConfiguration createWifiConfiguration(@NonNull ScanResult scanResult, @NonNull String preSharedKey) {
            // See https://stackoverflow.com/a/53749271/1597897.

            String[] capabilities = scanResult.capabilities.substring(1, scanResult.capabilities.indexOf(']') - 1).split("-");

            String auth = "";
            String keyManagement = "";
            String pairwiseCipher = "";

            if (capabilities.length > 0) {
                auth = capabilities[0];
            }

            if (capabilities.length > 1) {
                keyManagement = capabilities[1];
            }

            if (capabilities.length > 2) {
                pairwiseCipher = capabilities[2];
            }

            WifiConfiguration config = new WifiConfiguration();
            config.SSID = quoted(scanResult.SSID);
            config.BSSID = scanResult.BSSID;

            if (auth.contains("WPA") || auth.contains("WPA2")) {
                config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            }

            if (auth.contains("EAP")) {
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.LEAP);
            } else if (auth.contains("WPA") || auth.contains("WPA2")) {
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            } else if (auth.contains("WEP")) {
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            }

            if (keyManagement.contains("IEEE802.1X")) {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
            } else if (auth.contains("WPA") && keyManagement.contains("EAP")) {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            } else if (auth.contains("WPA") && keyManagement.contains("PSK")) {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            } else if (auth.contains("WPA2") && keyManagement.contains("PSK")) {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            }

            if (pairwiseCipher.contains("CCMP") || pairwiseCipher.contains("TKIP")) {
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            }

            if (!preSharedKey.isEmpty()) {
                if (auth.contains("WEP")) {
                    if (preSharedKey.matches("\\p{XDigit}+")) {
                        config.wepKeys[0] = preSharedKey;
                    } else {
                        config.wepKeys[0] = quoted(preSharedKey);
                    }
                    config.wepTxKeyIndex = 0;
                } else {
                    config.preSharedKey = quoted(preSharedKey);
                }
            }

            return config;
        }

        @NonNull
        private String quoted(@NonNull String s) {
            return "\"" + s + "\"";
        }
    }

    private static class WifiConnectionStateChangedReceiver extends BroadcastReceiver {

        @Nullable
        private WifiConnectivityListener listener;

        @NonNull
        private String networkSsid;

        @NonNull
        private WifiManager wifiManager;

        private boolean disconnectIfNotFound;

        private boolean startedConnecting = false;

        WifiConnectionStateChangedReceiver(
            @NonNull String networkSsid,
            @NonNull WifiManager wifiManager,
            boolean disconnectIfNotFound,
            @Nullable WifiConnectivityListener listener
        ) {
            this.listener = listener;
            this.networkSsid = networkSsid;
            this.wifiManager = wifiManager;
            this.disconnectIfNotFound = disconnectIfNotFound;
        }

        @Override
        public void onReceive(@NonNull Context context, Intent intent) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo == null) {
                return;
            }

            String ssid = wifiManager.getConnectionInfo().getSSID().replace("\"", "");

            if (!startedConnecting) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTING) {
                    if (ssid.equals(networkSsid)) {
                        startedConnecting = true;
                    } else {
                        onFailure(context);
                    }
                }
                return;
            }

            if (networkInfo.getState() == NetworkInfo.State.CONNECTING) {
                return;
            }

            if (networkInfo.getState() == NetworkInfo.State.CONNECTED && ssid.equals(networkSsid)) {
                onSuccess(context);
            } else {
                onFailure(context);
            }
        }

        private void onSuccess(Context context) {
            context.unregisterReceiver(this);
            if (listener != null) {
                listener.onConnected();
            }
        }

        private void onFailure(Context context) {
            context.unregisterReceiver(this);
            if (disconnectIfNotFound) {
                wifiManager.disconnect();
            }
            if (listener != null) {
                listener.onNotFound();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static class WifiNetworkCallback extends ConnectivityManager.NetworkCallback {

        @NonNull
        private final ConnectivityManager connectivityManager;

        @Nullable
        private final WifiConnectivityListener listener;

        WifiNetworkCallback(@NonNull ConnectivityManager connectivityManager, @Nullable WifiConnectivityListener listener) {
            this.connectivityManager = connectivityManager;
            this.listener = listener;
        }

        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            connectivityManager.bindProcessToNetwork(network);
            if (listener != null) {
                listener.onConnected();
            }
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            connectivityManager.unregisterNetworkCallback(this);
            connectivityManager.bindProcessToNetwork(null);
        }

        @Override
        public void onUnavailable() {
            super.onUnavailable();
            if (listener != null) {
                listener.onNotFound();
            }
        }
    }

    /**
     * Callback executor,  which will post the runnable on main thread.
     */
    @NonNull
    private Executor callbackExecutor = new Executor() {

        @NonNull
        Handler mainHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainHandler.post(command);
        }
    };
}
