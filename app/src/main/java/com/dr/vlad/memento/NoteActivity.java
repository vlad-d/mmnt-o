package com.dr.vlad.memento;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dr.vlad.memento.fragments.MyMapFragment;
import com.dr.vlad.memento.fragments.ReminderDialogFragment;
import com.dr.vlad.memento.notes.Note;
import com.dr.vlad.memento.notes.NoteItem;
import com.google.android.gms.maps.MapFragment;

import java.util.Calendar;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    //Note auth
    private static final String DIALOG_FRAGMENT_TAG = "reminderFragment";
    private static final String DIALOG_FRAGMENT_LOCATION_TAG = "locationFragment";
    private static final int MY_PERMISSIONS_REQUEST_USE_LCOATION = 0;
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
    private Calendar calendar;

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

        tvActionSetReminder = (TextView) findViewById(R.id.tv_bs_action_set_reminder);
        tvActionSetReminder.setOnClickListener(this);

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

            case R.id.tv_bs_action_set_reminder:
                switchBottomSheetState();
                ReminderDialogFragment fragment = new ReminderDialogFragment();
                fragment.show(this.getFragmentManager(), DIALOG_FRAGMENT_TAG);
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


    public void showDatePicker() {
        getCalendar();
        DatePickerDialog datePicker = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    public void showTimePicker() {
        getCalendar();
        TimePickerDialog timePicker = new TimePickerDialog(this, this, calendar.get(Calendar.HOUR_OF_DAY) + 2, calendar.get(Calendar.MINUTE), false);
        timePicker.show();
    }

    private void getCalendar() {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        calendar.set(i, i1, i2);
        showTimePicker();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        calendar.set(Calendar.HOUR_OF_DAY, i);
        calendar.set(Calendar.MINUTE, i1);
    }

    public void showLocationPicker() {
        if (checkLocationPermission()) {
            MyMapFragment mapFragment = new MyMapFragment();
            mapFragment.show(this.getFragmentManager(), DIALOG_FRAGMENT_TAG);
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionInfoMessage("You need to allow access to Location", Manifest.permission.ACCESS_FINE_LOCATION, MY_PERMISSIONS_REQUEST_USE_LCOATION);
                return false;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_USE_LCOATION);
                return false;
            }
        }

        return true;
    }

    private void showPermissionInfoMessage(String message, final String permission, final int requestCode) {
        new AlertDialog.Builder(this).setMessage(message)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(NoteActivity.this, "Location reminder cannot work without access to location", Toast.LENGTH_SHORT).show();
//                        finish();
                    }
                })
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(NoteActivity.this, new String[]{permission}, requestCode);
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_USE_LCOATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NoteActivity.this, "Location reminder cannot work without access to location", Toast.LENGTH_SHORT).show();
                }

                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }
}

