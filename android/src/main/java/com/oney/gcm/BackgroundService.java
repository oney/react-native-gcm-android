package com.oney.gcm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;

import io.neson.react.notification.NotificationPackage;

public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";
    private ReactInstanceManager mReactInstanceManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("index.android")
                .addPackage(new MainReactPackage())
                .addPackage(new GcmPackage(intent))
                .setUseDeveloperSupport(false)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        mReactInstanceManager.createReactContextInBackground();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mReactInstanceManager.onPause();
        mReactInstanceManager.onDestroy();
        mReactInstanceManager = null;
    }
}
