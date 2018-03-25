# mayti

A mobile application responsible for alerting users of swing and day trading activity. The application's alpha is an android application.

## Getting Started 

### Prerequisites

A Java IDE is highly recommended, and the Android SDK is a requirement. As one can tell from the _gitignore_, our favorite flavor of IDE for this particlar development is Jetbrains. To get up and running with Android development within IntelliJ, refer to [this documentation](https://www.jetbrains.com/help/idea/prerequisites-for-android-development.html).

Alternatively, Android Studio is another Jetbrains product that is almost at par with IntelliJ functionality and comes bundled with an SDK Manager and emulator support.

### Installation
```
$ git clone https://github.com/collinpreston/Mayti.git
```

### Deployment

Running the application on a local machine occurs through an emulator. The latest version of Android OS is preferred, as is current hardware. As of 03/17/2018 the best configuration is _Pixel 2 API P_.

On macOS, the only caveat with initial attempts of emulation through Android Studios is that the install sets the ANDROID_HOME variable to a local path within **/usr/** while the IDE defaults to searching for an sdk under the macOS User library. The following PATH additions solves this problem. 

```
$ export ANDROID_HOME=/Users/<Username>/Library/Android/sdk
$ export ANDROID_SDK_ROOT=$ANDROID_HOME
```

## Technologies Used
* [Jetbrains IDEA](https://www.jetbrains.com/idea/)
* [Android Studio](https://developer.android.com/studio/index.html)
* [Android Room](https://developer.android.com/topic/libraries/architecture/room.html)
* [IEX API](https://iextrading.com/)

## Contribution

Please refer to CONTRIBUTORS.md to view contributing developers.
