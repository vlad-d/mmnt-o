package com.dr.vlad.memento;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.dr.vlad.memento.model.Note;
import com.dr.vlad.memento.model.NoteItem;
import com.dr.vlad.memento.model.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by drinc on 4/5/2017.
 */

public class Notificator {

    public static final int LIGHT_COLOR = Color.GREEN;
    public static final long[] VIBRATION_PATTERN = {500, 1000};

    private Context context;
    private Type type;
    private DatabaseHelper db;

    public Notificator(Context context, Type type) {
        this.context = context;
        this.type = type;
        this.db = new DatabaseHelper(context);
    }

    public void sendNotification(final long reminderId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Note note = db.getNoteByReminderId(reminderId);
                List<NoteItem> noteItems = db.getNoteItems(note.getId());
                NotificationManagerCompat notifManager = NotificationManagerCompat.from(context);
                notifManager.notify(((int) reminderId), buildNotification(note, noteItems.get(0)));
            }
        }).start();

    }

    private android.app.Notification buildNotification(Note note, NoteItem noteItem) {
        Intent intent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        switch (type) {
            case LOCATION:
                builder.setSmallIcon(R.drawable.ic_stat_location_reminder);
                break;
            case DATE_TIME:
                builder.setSmallIcon(R.drawable.ic_stat_date_time);
                break;
        }

        builder.setContentTitle(note.getTitle())
                .setContentText(noteItem.getText())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(VIBRATION_PATTERN)
                .setLights(LIGHT_COLOR, 1000, 1000);

        return builder.build();
    }

    public enum Type {
        LOCATION,
        DATE_TIME
    }

}
