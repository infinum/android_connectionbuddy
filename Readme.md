[![Build Status](https://travis-ci.org/zplesac/android_connectionbuddy.svg?branch=development)](https://travis-ci.org/zplesac/android_connectionbuddy)
[![JCenter](https://img.shields.io/badge/JCenter-1.0.6-red.svg?style=flat)](https://bintray.com/zplesac/maven/android-connectionbuddy/view)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android%20ConnectionBuddy-green.svg?style=true)](https://android-arsenal.com/details/1/2788)

# Android ConnectionBuddy

Provides a simple way for handling connectivity change events.

# Usage

1) Add the library as a dependency to your ```build.gradle```

```groovy
compile 'com.zplesac:connectionbuddy:version@aar'
```

Versions prior to 1.0.5 were hosted on older jCenter repository, and are not available anymore due to trademark issues.

2) Initialize [ConnectionBuddy](https://github.com/zplesac/android_connectionbuddy/blob/master/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/ConnectionBuddy.java) instance in your Application class. You'll also need to provide a global configuration by defining [ConnectionBuddyConfiguration](https://github.com/zplesac/android_connectionbuddy/blob/master/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/ConnectionBuddyConfiguration.java) object.

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

See all default values for config options [here](https://github.com/zplesac/android_connectionbuddy/blob/master/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/ConnectionBuddyConfiguration.java).

3) Register to connectivity change events in onStart() method of your activity:

```java

 @Override
 protected void onStart() {
     super.onStart();
     ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this);
}

  ```

4) Unregister from connectivity change events in onStop() method of your activity:

```java

  @Override
  protected void onStop() {
      super.onStop();
      ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);
  }

  ```

5) React to connectivity change events on onConnectionChange(ConnectivityEvent event) callback method:

```java
  @Override
  public void onConnectionChange(ConnectivityEvent event) {
      if(event.getConnectionState() == ConnectionsState.CONNECTED){
          // device has active internet connection
      }
      else{
         // there is no active internet connection on this device
      }
  }
  ```

ConnectivityEvent also holds [ConnectivityType](https://github.com/zplesac/android_connectionbuddy/blob/master/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/models/ConnectivityType.java) enum, which defines network connection type currently available on user's device.

You'll also need to clear stored connectivity state for your activity/fragment
if it was restored from saved instance state (in order to always have the latest
connectivity state). Add to you onCreate() method the  following line of code:

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
  
Changelog is available [here.](https://github.com/zplesac/android_connectionbuddy/blob/master/CHANGELOG.md)  

## Advanced usage with MVP pattern

ConnectionBuddy also provides [ConnectivityPresenter](https://github.com/zplesac/android_connectionbuddy/blob/master/connectionbuddy/src/main/java/com/zplesac/connectionbuddy/presenters/NetworkInspectorPresenter.java)
which can be used as a base presenter for registering to connectivity change events.
More detailed example can be found [here](https://github.com/zplesac/android_connectionbuddy/blob/master/sampleapp/src/main/java/com/zplesac/connectionbuddy/sampleapp/activities/MVPActivity.java).

## Contributing

Feedback and code contributions are very much welcome. Just make a pull request with a short description of your changes. By making contributions to this project you give permission for your code to be used under the same [license](LICENSE).
