# Android NetworkInspector

Provides a simple way for handling connectivity change events.

# Usage

1) Add the library as a dependency to your ```build.gradle```

```groovy
compile 'com.zplesac:networkinspector:version@aar'
```

2) Initialize [NetworkInspector](https://github.com/zplesac/android_networkinspector/blob/master/networkinspector/src/main/java/com/zplesac/networkinspector/NetworkInspector.java) instance in your Application class. You'll also need to provide a global configuration by defining [NetworkInspectorConfiguration](https://github.com/zplesac/android_networkinspector/blob/master/networkinspector/src/main/java/com/zplesac/networkinspector/NetworkInspectorConfiguration.java) object.

```java
public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
         NetworkInspectorConfiguration networkInspectorConfiguration = new NetworkInspectorConfiguration.Builder(this).build();
         NetworkInspector.getInstance().init(networkInspectorConfiguration);
    }
}
```
 
All options in [NetworkInspectorConfiguration.Builder](https://github.com/zplesac/android_networkinspector/blob/master/networkinspector/src/main/java/com/zplesac/networkinspector/NetworkInspectorConfiguration.java) are optional. Use only those you really want to customize.

See all default values for config options [here](https://github.com/zplesac/android_networkinspector/blob/master/networkinspector/src/main/java/com/zplesac/networkinspector/NetworkInspectorConfiguration.java).

3) Register to connectivity change events in onStart() method of your activity:

```java

 @Override
 protected void onStart() {
     super.onStart();
     NetworkInspector.getInstance().registerForConnectivityEvents(this, this);
}

  ```

4) Unregister from connectivity change events in onStop() method of your activity:

```java

  @Override
  protected void onStop() {
      super.onStop();
      NetworkInspector.getInstance().unregisterFromConnectivityEvents(this);
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

ConnectivityEvent also holds [ConnectivityType](https://github.com/zplesac/android_networkinspector/blob/master/networkinspector/src/main/java/com/zplesac/networkinspector/models/ConnectivityType.java) enum, which defines network connection type currently available on user's device.

You'll also need to clear stored connectivity state for your activity/fragment
if it was restored from saved instance state (in order to always have the latest
connectivity state). Add to you onCreate() method the  following line of code:

```java
  @Override
  protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       ...

       if(savedInstanceState != null){
           NetworkInspectorCache.clearInternetConnection(this);
       }
   }
  ```
  
Changelog is available [here.](https://github.com/zplesac/android_networkinspector/blob/master/CHANGELOG.md)  

## Advanced usage with MVP pattern

NetworkInspector also provides [NetworkInspectorPresenter](https://github.com/zplesac/android_networkinspector/blob/master/networkinspector/src/main/java/com/zplesac/networkinspector/presenters/NetworkInspectorPresenter.java)
which can be used as a base presenter for registering to connectivity change events.
More detailed example can be found [here](https://github.com/zplesac/android_networkinspector/blob/master/sampleapp/src/main/java/com/zplesac/networkinspector/sampleapp/activities/MVPActivity.java).

## Contributing

Feedback and code contributions are very much welcome. Just make a pull request with a short description of your changes. By making contributions to this project you give permission for your code to be used under the same [license](LICENSE).
