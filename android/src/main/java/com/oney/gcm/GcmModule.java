package com.oney.gcm;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.json.*;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.util.Log;

import android.content.Context;

public class GcmModule extends ReactContextBaseJavaModule {
    private final static String TAG = GcmModule.class.getCanonicalName();
    private ReactContext mReactContext;
    private Intent mIntent;

    public GcmModule(ReactApplicationContext reactContext, Intent intent) {
        super(reactContext);
        mReactContext = reactContext;
        mIntent = intent;

        if (mIntent == null) {
            listenGcmRegistration();
            listenGcmReceiveNotification();
        }
    }

    @Override
    public String getName() {
        return "GcmModule";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        if (mIntent != null) {
            Bundle bundle = mIntent.getBundleExtra("bundle");
            String bundleString = convertJSON(bundle);
            constants.put("launchNotification", bundleString);
        }
        return constants;
    }

    private void sendEvent(String eventName, Object params) {
        mReactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }

    private void listenGcmRegistration() {
        IntentFilter intentFilter = new IntentFilter("RNGCMRegisteredToken");

        mReactContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String token = intent.getStringExtra("token");
                WritableMap params = Arguments.createMap();
                params.putString("deviceToken", token);

                sendEvent("remoteNotificationsRegistered", params);
            }
        }, intentFilter);
    }

    private String convertJSON(Bundle bundle) {
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    json.put(key, JSONObject.wrap(bundle.get(key)));
                } else {
                    json.put(key, bundle.get(key));
                }
            } catch(JSONException e) {
                return null;
            }
        }
        return json.toString();
    }

    private void listenGcmReceiveNotification() {
        IntentFilter intentFilter = new IntentFilter("com.oney.gcm.GCMReceiveNotification");

        mReactContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "GCMReceiveNotification BroadcastReceiver");
                Bundle bundle = intent.getBundleExtra("bundle");

                String bundleString = convertJSON(bundle);

                WritableMap params = Arguments.createMap();
                params.putString("dataJSON", bundleString);

                sendEvent("remoteNotificationReceived", params);
                abortBroadcast();
            }
        }, intentFilter);
    }

    @ReactMethod
    public void requestPermissions() {
        mReactContext.startService(new Intent(mReactContext, GcmRegistrationService.class));
    }
    @ReactMethod
    public void stopService() {
        if (mIntent != null) {
            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
                    mReactContext.stopService(mIntent);
                }
            }, 1000);
        }
    }
}
