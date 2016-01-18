package com.oney.gcm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;

import java.lang.reflect.Field;

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
                .addPackage(new NotificationPackage(null))
                .setUseDeveloperSupport(getBuildConfigDEBUG())
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

    private Class getBuildConfigClass() {
        try {
            String packageName = getPackageName();

            return Class.forName(packageName + ".BuildConfig");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    private boolean getBuildConfigDEBUG() {
        Class klass = getBuildConfigClass();
        for (Field f : klass.getDeclaredFields()) {
            if (f.getName().equals("DEBUG")) {
                try {
                    return f.getBoolean(this);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
