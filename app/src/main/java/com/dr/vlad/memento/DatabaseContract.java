package com.dr.vlad.memento;

import android.provider.BaseColumns;

/**
 * Created by drinc on 1/4/2017.
 */

public final class DatabaseContract {

    public static final String COMMA_SEP = ", ";

    /* Array of all tables */
    public static final String[] TABLES_ARRAY = {
            NoteTable.TABLE_NAME,
            NoteItemsTable.TABLE_NAME,
            LabelsTable.TABLE_NAME
    };

    /* An array list of all the SQL create statements */
    public static final String[] CREATE_TABLE_ARRAY = {
            LabelsTable.CREATE_TABLE,
            NoteTable.CREATE_TABLE,
            NoteItemsTable.CREATE_TABLE
    };

    private DatabaseContract() {
    }


    public static class NoteTable implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_LABEL_ID = "label_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_DELETED_AT = "deleted_at";

        public static final String DEFAULT_SORT_ORDER = COLUMN_CREATED_AT + " DESC";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INT PRIMARY KEY" + COMMA_SEP
                + COLUMN_LABEL_ID + " INT" + COMMA_SEP
                + COLUMN_TITLE + " TEXT " + COMMA_SEP
                + COLUMN_CREATED_AT + " INT NOT NULL " + COMMA_SEP
                + COLUMN_DELETED_AT + " INT"  + COMMA_SEP
                + "FOREIGN KEY(" + COLUMN_LABEL_ID + ") REFERENCES " + LabelsTable.TABLE_NAME + "(" + LabelsTable._ID + ")"
                + " );";
    }

    public static class NoteItemsTable implements BaseColumns {
        public static final String TABLE_NAME = "note_items";
        public static final String COLUMN_NOTE_ID = "note_id";
        public static final String COLUMN_TEXT = "content";
        public static final String COLUMN_ORDER = "position";
        public static final String COLUMN_EDITED_AT = "edited_at";

        public static final String DEFAULT_SORT_ORDER = COLUMN_ORDER + " ASC";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INT PRIMARY KEY" + COMMA_SEP
                + COLUMN_NOTE_ID + " INT" + COMMA_SEP
                + COLUMN_TEXT + " TEXT" + COMMA_SEP
                + COLUMN_ORDER + " INT NOT NULL" + COMMA_SEP
                + COLUMN_EDITED_AT + " INT" + COMMA_SEP
                + "FOREIGN KEY(" + COLUMN_NOTE_ID + ") REFERENCES " + NoteTable.TABLE_NAME + "(" + NoteTable._ID + ")"
                + " );";

    }

    public static class LabelsTable implements BaseColumns {
        public static final String TABLE_NAME = "labels";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_CREATED_AT = "created_at";

        public static final String DEFAULT_SORT_ORDER = COLUMN_TITLE + " ASC";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INT PRIMARY KEY" + COMMA_SEP
                + COLUMN_TITLE + " TEXT NOT NULL" + COMMA_SEP
                + COLUMN_COLOR + " TEXT NOT NULL" + COMMA_SEP
                + COLUMN_CREATED_AT + " INT" + " );";
    }


}
