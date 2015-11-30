[![Build Status](https://travis-ci.org/zplesac/android_connectify.svg?branch=development)](https://travis-ci.org/zplesac/android_connectify)
[![JCenter](https://img.shields.io/badge/JCenter-1.0.4-red.svg?style=flat)](https://bintray.com/zplesac/maven/android-connectify/view)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android%20Connectify-green.svg?style=true)](https://android-arsenal.com/details/1/2788)

# Android Connectify

Provides a simple way for handling connectivity change events.

# Usage

1) Add the library as a dependency to your ```build.gradle```

```groovy
compile 'com.zplesac:connectify:version@aar'
```

2) Initialize [Connectify](https://github.com/zplesac/android_connectify/blob/development/connectify%2Fsrc%2Fmain%2Fjava%2Fcom%2Fzplesac%2Fconnectifty%2FConnectify.java) instance in your Application class. You'll also need to provide a global configuration by defining [ConnectifyConfiguration](https://github.com/zplesac/android_connectify/blob/development/connectify%2Fsrc%2Fmain%2Fjava%2Fcom%2Fzplesac%2Fconnectifty%2FConnectifyConfiguration.java) object.

```java
public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ConnectifyConfiguration connectifyConfiguration = new ConnectifyConfiguration.Builder(this).build();
        Connectify.getInstance().init(connectifyConfiguration);
    }
}
 ```
 
All options in [ConnectifyConfiguration.Builder](https://github.com/zplesac/android_connectify/blob/development/connectify%2Fsrc%2Fmain%2Fjava%2Fcom%2Fzplesac%2Fconnectifty%2FConnectifyConfiguration.java) are optional. Use only those you really want to customize.

Following values can be overriden:
```java
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
```
 


3) Register to connectivity change events in onStart() method of your activity:

```java

 @Override
 protected void onStart() {
     super.onStart();
     Connectify.getInstance().registerForConnectifyEvents(this, this);
 }

  ```

4) Unregister from connectivity change events in onStop() method of your activity:

```java

  @Override
  protected void onStop() {
      super.onStop();
      Connectify.getInstance().unregisterFromConnectifyEvents(this);
  }

  ```

5) React to connectivity change events on onConnectionChange(ConnectifyEvent event) callback method:

```java
  @Override
  public void onConnectionChange(ConnectifyEvent event) {
      if(event.getConnectionState() == ConnectionsState.CONNECTED){
          // device has active internet connection
      }
      else{
         // there is no active internet connection on this device
      }
  }
  ```

ConnectifyEvent also holds [ConnectifyType](https://github.com/zplesac/android_connectify/blob/development/connectify%2Fsrc%2Fmain%2Fjava%2Fcom%2Fzplesac%2Fconnectifty%2Fmodels%2FConnectifyType.java) enum, which defines network connection type currently available on user's device.

You'll also need to clear stored connectivity state for your activity/fragment
if it was restored from saved instance state (in order to always have the latest
connectivity state). Add to you onCreate() method the  following line of code:

```java
  @Override
  protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       ...

       if(savedInstanceState != null){
           ConnectifyPreferences.clearInternetConnection(this);
       }
   }
  ```
  
Changelog is available [here.](https://github.com/zplesac/android_connectify/blob/development/CHANGELOG.md)  

## Advanced usage with MVP pattern

Connectify also provides [ConnectifyPresenter](https://github.com/zplesac/android_connectify/blob/master/connectify%2Fsrc%2Fmain%2Fjava%2Fcom%2Fzplesac%2Fconnectifty%2Fpresenters%2FConnectifyPresenter.java)
which can be used as a base presenter for registering to connectivity change events.
More detailed example can be found [here](https://github.com/zplesac/android_connectify/blob/master/sampleapp/src/main/java/com/zplesac/connectify/sampleapp/activities/MVPActivity.java).

## Contributing

Feedback and code contributions are very much welcome. Just make a pull request with a short description of your changes. By making contributions to this project you give permission for your code to be used under the same [license](LICENSE).
