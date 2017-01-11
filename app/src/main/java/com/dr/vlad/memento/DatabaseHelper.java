package com.dr.vlad.memento;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dr.vlad.memento.notes.Note;
import com.dr.vlad.memento.notes.NoteItem;

import java.util.ArrayList;
import java.util.Calendar;
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
        for (String item : DatabaseContract.CREATE_TABLE_ARRAY) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + item);
            onCreate(sqLiteDatabase);
        }
    }

    public long insertNote(Note note) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.NoteTable.COLUMN_TITLE, note.getTitle());
        values.put(DatabaseContract.NoteTable.COLUMN_CREATED_AT, note.getCreatedAt());
        values.putNull(DatabaseContract.NoteTable.COLUMN_DELETED_AT);
        if (note.getLabelId() != null) {
            values.put(DatabaseContract.NoteTable.COLUMN_LABEL_ID, note.getLabelId());
        }

        long newRowId = db.insert(DatabaseContract.NoteTable.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }


    public List<Note> getNotes() {
        List<Note> notes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseContract.NoteTable.TABLE_NAME + " WHERE " + DatabaseContract.NoteTable.COLUMN_DELETED_AT + " IS NULL" + " ORDER BY " + DatabaseContract.NoteTable.DEFAULT_SORT_ORDER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getLong(3), cursor.getLong(4));
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
        values.put(DatabaseContract.NoteItemsTable.COLUMN_EDITED_AT, item.getEditedAt());
        long newRowId = db.insert(DatabaseContract.NoteItemsTable.TABLE_NAME, null, values);
        return newRowId;
    }

    public List<NoteItem> getNoteItems(long noteId) {
        List<NoteItem> items = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseContract.NoteItemsTable.TABLE_NAME
                + " WHERE " + DatabaseContract.NoteItemsTable.COLUMN_NOTE_ID + " = " + noteId + " ORDER BY " + DatabaseContract.NoteItemsTable.DEFAULT_SORT_ORDER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                NoteItem item = new NoteItem(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getInt(3), cursor.getLong(4));
                items.add(item);
            } while (cursor.moveToNext());
        }

        return items;
    }




}
