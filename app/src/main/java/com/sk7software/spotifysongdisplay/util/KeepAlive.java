package com.sk7software.spotifysongdisplay.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.sk7software.spotifysongdisplay.TrackBroadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;

public class KeepAlive extends BroadcastReceiver {
    private static final String TAG = KeepAlive.class.getSimpleName();
    private static int KEEP_ALIVE_INTERVAL_S = 90;
    private static boolean alarmSet = false;
    private static final String TIME_DISPLAY_FORMAT = "dd/MM/yyyy HH:mm:ss";

    @Override
    public void onReceive(Context context, Intent intent) {
        checkService(context);
    }

    public void initialise(Context context) {
        if (!alarmSet) {
            Log.d(TAG, "Initialising keep alive");
            alarmSet = true;
            setAlarm(context);
        }
    }

    private void setAlarm(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_DISPLAY_FORMAT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, KeepAlive.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        long wakeupTime = System.currentTimeMillis() + (1000 * KEEP_ALIVE_INTERVAL_S);
        am.setRepeating(AlarmManager.RTC_WAKEUP, wakeupTime, 1000 * KEEP_ALIVE_INTERVAL_S, pi);
        Log.d(TAG, "Keep alive alarm set for: " + sdf.format(new Date(wakeupTime)));
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, KeepAlive.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        Log.d(TAG, "Keep alive cancelled");
        alarmSet = false;
    }

    private void checkService(Context context) {
        Log.d(TAG, "Song display heartbeat");
        Intent i = new Intent(context, TrackBroadcastReceiver.class);
        context.startService(i);
    }
}
