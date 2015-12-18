# react-native-gcm-android

GCM for React Native Android

## Demo

https://github.com/oney/TestGcm

## Installation

- Run `npm install react-native-gcm-android --save`

- In `android/build.gradle`
```gradle
dependencies {
    classpath 'com.android.tools.build:gradle:1.3.1'
    classpath 'com.google.gms:google-services:1.4.0-beta3' // <- Add this line
```

- In `android/settings.gradle`, add
```gradle
include ':RNGcmAndroid', ':app'
project(':RNGcmAndroid').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-gcm-android/android')
```

- In `android/app/build.gradle`
```gradle
apply plugin: "com.android.application"
apply plugin: 'com.google.gms.google-services'           // <- Add this line
...
dependencies {
    compile fileTree(dir: "libs", include: ["*.jar"])
    compile "com.android.support:appcompat-v7:23.0.1"
    compile "com.facebook.react:react-native:0.14.+"
    compile 'com.google.android.gms:play-services-gcm:8.1.0' // <- Add this line
    compile project(':RNGcmAndroid')                     // <- Add this line
}
```

- In `android/app/src/main/AndroidManifest.xml`, add these lines, be sure to change `com.xxx.yyy` to your package
```xml
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="com.google.android.c2dm.permission.SEND" />
<uses-permission android:name="android.permission.GET_ACCOUNTS" />

<permission
  android:name="com.xxx.yyy.permission.C2D_MESSAGE"
  android:protectionLevel="signature" />
<uses-permission android:name="com.xxx.yyy.permission.C2D_MESSAGE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.VIBRATE"></uses-permission>

...

<application
  android:theme="@style/AppTheme">

  ...
  <meta-data
    android:name="com.google.android.gms.version"
    android:value="@integer/google_play_services_version" />

  <receiver
    android:name="com.google.android.gms.gcm.GcmReceiver"
    android:exported="true"
    android:permission="com.google.android.c2dm.permission.SEND" >
    <intent-filter>
      <action android:name="com.google.android.c2dm.intent.RECEIVE" />
      <category android:name="com.xxx.yyy" />
    </intent-filter>
  </receiver>
  <service android:name="com.oney.gcm.GcmRegistrationService"/>
  <service
    android:name="com.oney.gcm.RNGcmListenerService"
    android:exported="false" >
    <intent-filter>
      <action android:name="com.google.android.c2dm.intent.RECEIVE" />
    </intent-filter>
  </service>
  ...
```
- In `android/app/src/main/java/com/testoe/MainActivity.java`
```java
import com.oney.gcm.GcmPackage;                // <- Add this line
    ...
        .addPackage(new MainReactPackage())
        .addPackage(new GcmPackage())          // <- Add this line
```

### GCM API KEY
By following [Cloud messaging](https://developers.google.com/cloud-messaging/android/client), you can get `google-services.json` file and place it in `android/app` directory

### Usage

```javascript
var GcmAndroid = require('react-native-gcm-android');
GcmAndroid.addEventListener('register', function(token){
  console.log('send gcm token to server', token);
});
GcmAndroid.addEventListener('notification', function(notification){
  console.log('receive gcm notification', notification);
});
GcmAndroid.requestPermissions();
```

- By default, if the activity is not running, it will show a notification on phone. Otherwise, you can get notification in `GcmAndroid.addEventListener('notification'` listenter.

- You should send GCM notification that has "data" key with following infos
```json
{
  "largeIcon": "ic_launcher",
  "contentTitle": "Awesome app",
  "message": "Hi, how are you?",
  "ticker": "hi...",
}
```

- You can remove `<service android:name="com.oney.gcm.RNGcmListenerService"/>` and change to your custom `GcmListenerService` in `AndroidManifest.xml` to handle notifications in java codes

## Troubleshoot

- Do not add `multiDexEnabled true` in `android/app/build.gradle` even encounter `com.android.dex.DexException: Multiple dex files...` failure.
