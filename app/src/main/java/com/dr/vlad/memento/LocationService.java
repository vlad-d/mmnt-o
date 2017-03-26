package com.dr.vlad.memento;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dr.vlad.memento.model.Reminder;

import java.util.ArrayList;

/**
 * Created by drinc on 3/11/2017.
 */

public class LocationService extends Service {
    public static final String TAG = "LOCATION SERVICE";
    private DatabaseHelper db;
    private ArrayList<Reminder> reminders;


    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        db = new DatabaseHelper(LocationService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LOCATION SERVICE", "onStartCommand");
        new Thread(new Runnable() {
            @Override
            public void run() {
                reminders = db.getLocationReminders();


                stopSelf();
            }

        }).start();

        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
