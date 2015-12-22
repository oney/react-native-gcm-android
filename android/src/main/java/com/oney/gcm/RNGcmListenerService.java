package com.oney.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.res.Resources;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import java.util.List;

import com.google.android.gms.gcm.GcmListenerService;

public class RNGcmListenerService extends GcmListenerService {

    private static final String TAG = "RNGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        sendNotification(bundle);
    }

    private void sendNotification(Bundle bundle) {
        Log.d(TAG, "sendNotification");

        Intent i = new Intent("com.oney.gcm.GCMReceiveNotification");
        i.putExtra("bundle", bundle);
        sendOrderedBroadcast(i, null);
    }
}
