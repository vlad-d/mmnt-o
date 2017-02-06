package com.dr.vlad.memento;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dr.vlad.memento.notes.Note;
import com.dr.vlad.memento.notes.NoteItem;

import java.util.Calendar;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    //Note auth
    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    DatabaseHelper db;
    Calendar now;
    private int color;
    private EditText etNoteTitle;
    private EditText etNoteBody;
    //Bottom sheet
    private LinearLayout llBottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageButton ibtnEnhance;
    private TextView tvActionCancel;
    private TextView tvActionSend;
    private TextView tvActionProtect;
    private TextView tvActionSetReminder;
    private TextView tvActionAddLabel;
    private BottomSheetDialogFragment bottomSheetDialogFragment;
    private Note note = new Note();
    private NoteItem noteItem = new NoteItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_note_activity);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViews();

        db = new DatabaseHelper(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            long noteId = bundle.getLong(getResources().getString(R.string.key_note_id));
            Log.d("FINGKEY", noteId + "NoteActivity");
            note = db.getNote(noteId);
            note.setItems(db.getNoteItems(noteId));
        }


        if (note.getId() != null) {
            noteItem = note.getItems().get(0);
            String title = note.getTitle();
            if (!title.isEmpty()) {
                etNoteTitle.setText(note.getTitle());
            }
            String body = noteItem.getText();
            if (!body.isEmpty()) {
                etNoteBody.setText(body);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                //save note here
                saveNote();
                hideKeyboard(this.getCurrentFocus());
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeViews() {
        llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                ibtnEnhance.setRotation(slideOffset * 180);
            }
        });

        TextView tvEnhance = (TextView) findViewById(R.id.tv_action_enhance);
        tvEnhance.setOnClickListener(this);

        ibtnEnhance = (ImageButton) findViewById(R.id.btn_enhance);
        ibtnEnhance.setOnClickListener(this);

        tvActionCancel = (TextView) findViewById(R.id.tv_bs_action_cancel);
        tvActionCancel.setOnClickListener(this);

        tvActionProtect = (TextView) findViewById(R.id.tv_bs_action_protect);
        tvActionProtect.setOnClickListener(this);

        color = ContextCompat.getColor(this, R.color.colorAccent);
        etNoteTitle = (EditText) findViewById(R.id.et_note_title);
        etNoteTitle.setHintTextColor(color);
        etNoteBody = (EditText) findViewById(R.id.et_note_body);
//        etNoteBody.requestFocus();
//        showKeyboard();


    }


    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_action_enhance:
                switchBottomSheetState();
                break;

            case R.id.btn_enhance:
                switchBottomSheetState();
                break;

            case R.id.tv_bs_action_cancel:
                finish();
                break;

            case R.id.tv_bs_action_protect:
                note.setProtect(1);
                switchBottomSheetState();
                break;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect = new Rect();
                llBottomSheet.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            saveNote();
            hideKeyboard(this.getCurrentFocus());
            NavUtils.navigateUpFromSameTask(this);
//            super.onBackPressed();
        }
    }

    private void switchBottomSheetState() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


    private void saveNote() {

        String noteTitle = etNoteTitle.getText().toString();
        noteTitle = noteTitle.isEmpty() ? "" : noteTitle;

        String noteBody = etNoteBody.getText().toString();
        noteBody = noteBody.isEmpty() ? "" : noteBody;

        note.setTitle(noteTitle);
        noteItem.setText(noteBody);


        if (note.getId() == null) {
            storeNote();
        } else {
            editNote();
        }

        if (noteItem.getId() == null) {
            storeItem();
        } else {
            editItem();
        }

    }


    private void storeNote() {
        now = Calendar.getInstance();
        note.setCreatedAt(now.getTimeInMillis());
        if (note.getProtect() == null) {
            note.setProtect(0);
        }
        long noteId = db.insertNote(note);
        note.setId(noteId);

//        NoteItem item = new NoteItem(noteId, noteBody, 0, 0, now.getTimeInMillis());
//        long itemId = db.insertItem(item);


    }

    private void editNote() {
        db.updateNote(note);
    }

    private void storeItem() {

        noteItem.setNoteId(note.getId());
        noteItem.setEditedAt(now.getTimeInMillis());
        noteItem.setOrder(0);
        noteItem.setDone(0);
        long itemId = db.insertItem(noteItem);
        noteItem.setId(itemId);

    }

    private void editItem() {
        Calendar editedAt = Calendar.getInstance();
        noteItem.setEditedAt(editedAt.getTimeInMillis());
        db.updateItem(noteItem);
    }


}

