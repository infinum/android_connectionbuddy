[![JCenter](https://img.shields.io/badge/JCenter-1.0.0-red.svg?style=flat)](https://bintray.com/zplesac/maven/android-connectify/view)

# Android Connectify

Provides a simple way for handling connectivity change events.

# Usage

1) Register to connectivity change events in onStart() method of your activity:

```java

 @Override
 protected void onStart() {
     super.onStart();
     ConnectifyUtils.registerForConnectivityEvents(this, this, this);
 }

  ```

2) Unregister from connectivity change events in onStop() method of your activity:

```java

  @Override
  protected void onStop() {
      super.onStop();
      ConnectifyUtils.unregisterFromConnectivityEvents(this, this);
  }

  ```

3) React to connectivity change events on onConnectionChange(NetworkChangeReceiver.ConnectivityEvent event) callback method:

```java
  @Override
  public void onConnectionChange(NetworkChangeReceiver.ConnectivityEvent event) {
      if(event == NetworkChangeReceiver.ConnectivityEvent.CONNECTED){
          // device has active internet connection
      }
      else{
         // there is no active internet connection on this device
      }
  }
  ```

You'll also need to clear stored connectivity state for your activity/fragment
if it was restored from saved instance state (in order to always have the latest
connectivity state). Add to you onCreate() method the  following line of code:

```java
  @Override
  protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       ...

       if(savedInstanceState != null){
           ConnectifyPreferences.clearInternetConnection(this, this);
       }
   }
  ```

## Advanced usage with MVP pattern

Connectify also provides [ConnectifyPresenter](https://github.com/zplesac/android_connectify/blob/master/connectify%2Fsrc%2Fmain%2Fjava%2Fcom%2Fzplesac%2Fconnectifty%2Fpresenters%2FConnectifyPresenter.java)
which can be used as a base presenter for registering to connectivity change events.
More detailed example can be found [here](https://github.com/zplesac/android_connectify/blob/master/sampleapp/src/main/java/com/zplesac/connectify/sampleapp/activities/MVPActivity.java).

## Contributing

Feedback and code contributions are very much welcome. Just make a pull request with a short description of your changes. By making contributions to this project you give permission for your code to be used under the same [license](LICENSE).
