package com.dr.vlad.memento;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dr.vlad.memento.model.Note;
import com.dr.vlad.memento.model.NoteItem;
import com.dr.vlad.memento.model.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by drinc on 1/4/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = DatabaseHelper.class.getSimpleName();
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MementoDatabase.db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for (String item : DatabaseContract.CREATE_TABLE_ARRAY) {
            sqLiteDatabase.execSQL(item);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        for (String item : DatabaseContract.TABLES_ARRAY) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + item);
        }
        onCreate(sqLiteDatabase);
    }

    public long insertNote(Note note) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.NoteTable.COLUMN_TITLE, note.getTitle());
        values.put(DatabaseContract.NoteTable.COLUMN_CREATED_AT, note.getCreatedAt());
        values.put(DatabaseContract.NoteTable.COLUMN_PROTECTED, note.getProtect());
        values.putNull(DatabaseContract.NoteTable.COLUMN_DELETED_AT);
        if (note.getLabelId() != null) {
            values.put(DatabaseContract.NoteTable.COLUMN_LABEL_ID, note.getLabelId());
        }

        long newRowId = db.insert(DatabaseContract.NoteTable.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    public Note getNote(long noteId) {
        Note note = new Note();
        String selectQuery = "SELECT * FROM " + DatabaseContract.NoteTable.TABLE_NAME + " WHERE " + DatabaseContract.NoteTable._ID + " = " + noteId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            note.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.NoteTable._ID)));
            note.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_TITLE)));
            note.setProtect(cursor.getInt(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_PROTECTED)));
            note.setCreatedAt(cursor.getLong(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_CREATED_AT)));
            note.setDeletedAt(cursor.getLong(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_DELETED_AT)));
            note.setLabelId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_LABEL_ID)));
        }


        return note;
    }

    public void updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.NoteTable.COLUMN_TITLE, note.getTitle());
        values.put(DatabaseContract.NoteTable.COLUMN_PROTECTED, note.getProtect());
        if (note.getLabelId() != null) {
            values.put(DatabaseContract.NoteTable.COLUMN_LABEL_ID, note.getLabelId());
        }
        db.update(DatabaseContract.NoteTable.TABLE_NAME, values, DatabaseContract.NoteTable._ID + " =? ", new String[]{String.valueOf(note.getId())});
    }


    public List<Note> getNotes() {
        List<Note> notes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseContract.NoteTable.TABLE_NAME + " WHERE " + DatabaseContract.NoteTable.COLUMN_DELETED_AT + " IS NULL" + " ORDER BY " + DatabaseContract.NoteTable.DEFAULT_SORT_ORDER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(
                        cursor.getLong(cursor.getColumnIndex(DatabaseContract.NoteTable._ID)),
                        cursor.getLong(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_LABEL_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_TITLE)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_PROTECTED)),
                        cursor.getLong(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_CREATED_AT)),
                        cursor.getLong(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_DELETED_AT))
                );
                notes.add(note);
            } while (cursor.moveToNext());
        }

        return notes;
    }

    public long insertItem(NoteItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.NoteItemsTable.COLUMN_NOTE_ID, item.getNoteId());
        values.put(DatabaseContract.NoteItemsTable.COLUMN_TEXT, item.getText());
        values.put(DatabaseContract.NoteItemsTable.COLUMN_ORDER, item.getOrder());
        values.put(DatabaseContract.NoteItemsTable.COLUMN_DONE, item.getDone());
        values.put(DatabaseContract.NoteItemsTable.COLUMN_EDITED_AT, item.getEditedAt());
        long newRowId = db.insert(DatabaseContract.NoteItemsTable.TABLE_NAME, null, values);
        return newRowId;
    }

    public void updateItem(NoteItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.NoteItemsTable.COLUMN_TEXT, item.getText());
        values.put(DatabaseContract.NoteItemsTable.COLUMN_DONE, item.getDone());
        values.put(DatabaseContract.NoteItemsTable.COLUMN_ORDER, item.getOrder());
        values.put(DatabaseContract.NoteItemsTable.COLUMN_EDITED_AT, item.getEditedAt());
        db.update(DatabaseContract.NoteItemsTable.TABLE_NAME, values, DatabaseContract.NoteItemsTable._ID + " =?", new String[]{String.valueOf(item.getId())});
    }

    public List<NoteItem> getNoteItems(long noteId) {
        List<NoteItem> items = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseContract.NoteItemsTable.TABLE_NAME
                + " WHERE " + DatabaseContract.NoteItemsTable.COLUMN_NOTE_ID + " = " + noteId + " ORDER BY " + DatabaseContract.NoteItemsTable.DEFAULT_SORT_ORDER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                NoteItem item = new NoteItem(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(5), cursor.getLong(4));
                items.add(item);
            } while (cursor.moveToNext());
        }

        return items;
    }

    public long insertReminder(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.RemindersTable.COLUMN_NOTE_ID, reminder.getNoteId());
        values.put(DatabaseContract.RemindersTable.COLUMN_TYPE, reminder.getType());
        values.put(DatabaseContract.RemindersTable.COLUMN_DONE, reminder.getDone() ? 1 : 0);
        values.put(DatabaseContract.RemindersTable.COLUMN_CREATED_AT, reminder.getCreatedAt());
        if (reminder.getType() == Reminder.TYPE_DATE_TIME) {
            values.put(DatabaseContract.RemindersTable.COLUMN_DATE_TIME, reminder.getDateTime());
        } else {
            values.put(DatabaseContract.RemindersTable.COLUMN_LATITUDE, reminder.getLatitude());
            values.put(DatabaseContract.RemindersTable.COLUMN_LONGITUDE, reminder.getLongitude());

        }

        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(DatabaseContract.RemindersTable.TABLE_NAME, null, values);
        Log.d(TAG, "reminder id" + id);
        return id;

    }

    public Reminder getNoteReminder(long noteId) {
        Reminder reminder = new Reminder();
        String selectQuery = "SELECT * FROM " + DatabaseContract.RemindersTable.TABLE_NAME + " WHERE "
                + DatabaseContract.RemindersTable.COLUMN_NOTE_ID + " = " + noteId
                + " AND " + DatabaseContract.RemindersTable.COLUMN_DELETED_AT + " IS NULL"
                + " ORDER BY " + DatabaseContract.RemindersTable.COLUMN_CREATED_AT + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            reminder.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.RemindersTable._ID)));
            reminder.setLongitude(cursor.getDouble(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_LONGITUDE)));
            reminder.setLatitude(cursor.getDouble(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_LATITUDE)));
            reminder.setDateTime(cursor.getLong(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_DATE_TIME)));
            reminder.setDone(cursor.getInt(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_DONE)) != 0);
            reminder.setType(cursor.getInt(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_TYPE)));
            reminder.setCreatedAt(cursor.getLong(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_CREATED_AT)));
            reminder.setDeletedAt(cursor.getLong(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_DELETED_AT)));
        }

        return reminder;
    }

    public Reminder getReminder(long reminderId) {
        Reminder reminder = new Reminder();
        String selectQuery = "SELECT * FROM " + DatabaseContract.RemindersTable.TABLE_NAME + " WHERE "
                + DatabaseContract.RemindersTable._ID + " = " + reminderId
                + " AND " + DatabaseContract.RemindersTable.COLUMN_DELETED_AT + " IS NULL"
                + " ORDER BY " + DatabaseContract.RemindersTable.COLUMN_CREATED_AT + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            reminder.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.RemindersTable._ID)));
            reminder.setLongitude(cursor.getDouble(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_LONGITUDE)));
            reminder.setLatitude(cursor.getDouble(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_LATITUDE)));
            reminder.setDateTime(cursor.getLong(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_DATE_TIME)));
            reminder.setDone(cursor.getInt(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_DONE)) != 0);
            reminder.setCreatedAt(cursor.getLong(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_CREATED_AT)));
            reminder.setDeletedAt(cursor.getLong(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_DELETED_AT)));
        }

        return reminder;
    }


    public ArrayList<Reminder> getLocationReminders() {
        ArrayList<Reminder> reminders = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseContract.RemindersTable.TABLE_NAME + " WHERE "
                + DatabaseContract.RemindersTable.COLUMN_TYPE + " = " + Reminder.TYPE_LOCATION + " AND "
                + DatabaseContract.RemindersTable.COLUMN_DONE + " = 0 AND "
                + DatabaseContract.RemindersTable.COLUMN_DELETED_AT + " IS NULL"
                + " ORDER BY " + DatabaseContract.RemindersTable.COLUMN_CREATED_AT + " ASC ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.RemindersTable._ID)));
                reminder.setNoteId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_NOTE_ID)));
                reminder.setType(cursor.getInt(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_TYPE)));
                reminder.setLatitude(cursor.getDouble(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_LATITUDE)));
                reminder.setLongitude(cursor.getDouble(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_LONGITUDE)));
                reminder.setDone(false);
                reminder.setCreatedAt(cursor.getLong(cursor.getColumnIndex(DatabaseContract.RemindersTable.COLUMN_CREATED_AT)));
                reminders.add(reminder);
            } while (cursor.moveToNext());
        }
        db.close();
        return reminders;
    }

    public Note getNoteByReminderId(long reminderId) {
        Note note = new Note();
        String selectQuery = "SELECT * FROM " + DatabaseContract.NoteTable.TABLE_NAME + " AS N "
                + "LEFT OUTER JOIN " + DatabaseContract.RemindersTable.TABLE_NAME + " AS R "
                + " ON " + "N." + DatabaseContract.NoteTable._ID + " = R." + DatabaseContract.RemindersTable.COLUMN_NOTE_ID
                + " WHERE " + " R." + DatabaseContract.RemindersTable._ID + " = " + reminderId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            note.setId(cursor.getLong(0));
            note.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_TITLE)));
            note.setProtect(cursor.getInt(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_PROTECTED)));
            note.setCreatedAt(cursor.getLong(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_CREATED_AT)));
            note.setDeletedAt(cursor.getLong(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_DELETED_AT)));
            note.setLabelId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.NoteTable.COLUMN_LABEL_ID)));
        }
        return note;
    }


}
