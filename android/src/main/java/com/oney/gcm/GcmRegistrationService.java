package com.oney.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class GcmRegistrationService extends IntentService {

    private static final String TAG = "GcmRegistrationService";

    public GcmRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Resources resources = getApplication().getResources();
        String packageName = getApplication().getPackageName();

        int resourceId = resources.getIdentifier("gcm_defaultSenderId", "string", packageName);
        String projectNumber = getString(resourceId);

        if (projectNumber == null) {
            return;
        }

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(projectNumber,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);

            registeredToken(token);

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }
    private void registeredToken(String token) {
        Intent i = new Intent("RNGCMRegisteredToken");
        i.putExtra("token", token);
        sendBroadcast(i);
    }

}
