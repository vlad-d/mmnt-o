package com.dr.vlad.memento;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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

//                simulare
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

        }).start();

        for (Reminder reminder : reminders) {
            Log.i("LOCATION SERVICE:", reminder.getLatitude() + " : " + reminder.getLongitude());
        }




        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
