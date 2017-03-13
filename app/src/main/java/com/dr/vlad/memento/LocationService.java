package com.dr.vlad.memento;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.dr.vlad.memento.notes.Reminder;

import java.util.ArrayList;

/**
 * Created by drinc on 3/11/2017.
 */

public class LocationService extends Service {
    private DatabaseHelper db;
    private ArrayList<Reminder> reminders;


    @Override
    public void onCreate() {
        db = new DatabaseHelper(LocationService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                reminders = db.getLocationReminders();
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
