[![Build Status](https://travis-ci.org/zplesac/android_connectionbuddy.svg?branch=development)](https://travis-ci.org/zplesac/android_connectionbuddy)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![JCenter](https://img.shields.io/badge/JCenter-2.0.0-beta1-red.svg?style=flat)](https://bintray.com/zplesac/maven/android-connectionbuddy/view)
[![Method count](https://img.shields.io/badge/Methods count-188-e91e63.svg)](http://www.methodscount.com/?lib=com.zplesac%3Aconnectionbuddy%3A2.0.0-beta1)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android%20ConnectionBuddy-green.svg?style=true)](https://android-arsenal.com/details/1/2788)


# Android ConnectionBuddy

Provides a simple way of handling connectivity change events.

# Usage

1) Add the library as a dependency to your ```build.gradle```

```groovy
compile 'com.zplesac:connectionbuddy:version@aar'
```

Check the latest version [here](https://bintray.com/search?query=connectionbuddy).

Versions prior to 1.0.5 were hosted on an older jCenter repository and aren't available anymore due to trademark issues.

2) Initialize a [ConnectionBuddy](https://github.com/zplesac/android_connectionbuddy/blob/master/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/ConnectionBuddy.java) instance in your Application class. You'll also need to provide a global configuration by defining [ConnectionBuddyConfiguration](https://github.com/zplesac/android_connectionbuddy/blob/master/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/ConnectionBuddyConfiguration.java) object.

```java
public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
         ConnectionBuddyConfiguration networkInspectorConfiguration = new ConnectionBuddyConfiguration.Builder(this).build();
         ConnectionBuddy.getInstance().init(networkInspectorConfiguration);
    }
}
```
 
All options in [ConnectionBuddyConfiguration.Builder](https://github.com/zplesac/android_connectionbuddy/blob/master/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/ConnectionBuddyConfiguration.java) are optional. Use only those you really want to customize.

3) Make your activites (or BaseActivity) extend [ConnectionBuddyActivity](https://github.com/zplesac/android_connectionbuddy/blob/development/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/activities/ConnectionBuddyActivity.java), and react to connectivity change events in onConnectionChange(ConnectivityEvent event) callback method:

```java
  @Override
  public void onConnectionChange(ConnectivityEvent event) {
      if(event.getState() == ConnectivityState.CONNECTED){
          // device has active internet connection
      }
      else{
         // there is no active internet connection on this device
      }
  }
```

If you don't want to extend [ConnectionBuddyActivity](https://github.com/zplesac/android_connectionbuddy/blob/development/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/activities/ConnectionBuddyActivity.java), you can use manual configuration:

* Register to connectivity change events in the onStart() method of your activity:

```java

 @Override
 protected void onStart() {
     super.onStart();
     ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this);
}

```

* Unregister from connectivity change events in the onStop() method of your activity:

```java

  @Override
  protected void onStop() {
      super.onStop();
      ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);
  }

```

*  Clear the stored connectivity state for your activity/fragment if it was restored from a saved instance state (in order to always have the latest connectivity state). Add to your onCreate() method the following line of code:

```java
  @Override
  protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       ...

       if(savedInstanceState != null){
           ConnectionBuddyCache.clearInternetConnection(this);
       }
   }
```

* Implement a [ConnectivityChangeListener](https://github.com/zplesac/android_connectionbuddy/blob/master/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/interfaces/ConnectivityChangeListener.java) interface and react to connectivity change events.

ConnectivityEvent also holds some additional information:
* [ConnectivityType](https://github.com/zplesac/android_connectionbuddy/blob/master/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/models/ConnectivityType.java) enum, which defines the network connection type currently available on the user's device
* [ConnectivityStrength](https://github.com/zplesac/android_connectionbuddy/blob/master/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/models/ConnectivityStrength.java) enum, which describes signal strength of the network connection.


## ConnectionBuddy configuration

You can customize the default ConnectionBuddy configuration by providing your own configuration. Following values can be changed:

#### 1. registerForWiFiChanges(boolean shouldRegister)

A Boolean value which defines whether we should register for WiFi network changes. The default value is set to true.

#### 2. registerForMobileNetworkChanges(boolean shouldRegister)

A Boolean value which defines whether we should register for mobile network changes. The default value is set to true.

#### 3. setMinimumSignalStrength(ConnectivityStrength minimumSignalStrength)

Defines the minimum signal strength for which the callback listener should be notified. The default value is set to ConnectivityStrength.POOR.

#### 4. setNotifyImmediately(boolean shouldNotify)

A Boolean value which defines whether we want to notify the listener about the current network connection state immediately after the listener has been registered. The default value is set to true.

#### 5. notifyOnlyReliableEvents(boolean shouldNotify)

A Boolean value which defines whether we want to use reliable network events. If we have an active internet connection, it will try to execute a test network request to determine whether a user is capable of any network operation. The default value is set to false.
  
## Advanced usage with MVP pattern

ConnectionBuddy also provides [ConnectivityPresenter](https://github.com/zplesac/android_connectionbuddy/blob/master/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/presenters/ConnectivityPresenter.java)
which can be used as a base presenter for registering to connectivity change events.
A more detailed example can be found [here](https://github.com/zplesac/android_connectionbuddy/blob/master/sampleapp/src/main/java/com/zplesac/connectionbuddy/sampleapp/activities/MVPActivity.java).

## Backward compatibility

As of version 1.2.0, ConnectionBuddy can be used with your apps on devices all the way back to Android 2.3 (API 10). It should also work on devices with API 8-9, but that's not tested.

## Changelog

Changelog is available in the [releases tab](https://github.com/zplesac/android_connectionbuddy/releases).

For versions prior to 2.0.0, [here.](https://github.com/zplesac/android_connectionbuddy/blob/master/CHANGELOG.md)

## Contributing

Feedback and code contributions are very much welcome. Just make a pull request with a short description of your changes. By making contributions to this project you give permission for your code to be used under the same [license](LICENSE).