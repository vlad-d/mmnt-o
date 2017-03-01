package com.dr.vlad.memento.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dr.vlad.memento.R;

/**
 * Created by vlad.drinceanu on 01.03.2017.
 */

public class TimePickerPreference extends DialogPreference {

    public static final String DEFAULT_VALUE = "8:30";
    private TimePicker picker = null;
    private int hour;
    private int minute;

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPositiveButtonText(R.string.reminder_positive_button);
        setNegativeButtonText(R.string.reminder_negative_button);
        setDialogTitle("");
    }

    @SuppressLint("NewApi")
    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            picker.setHour(hour);
            picker.setMinute(minute);
        } else {
            picker.setCurrentHour(hour);
            picker.setCurrentMinute(minute);
        }
        return picker;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                hour = picker.getHour();
                minute = picker.getMinute();
            } else {
                hour = picker.getCurrentHour();
                minute = picker.getCurrentMinute();
            }

            if (hour <= 12) {
                persistString(hour + ":" + minute);
                setSummary(hour + ":" + minute + " AM");
            } else {
                Toast.makeText(getContext(), "Invalid time selected for Morning", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String time;
        if (restorePersistedValue) {
            time = this.getPersistedString(DEFAULT_VALUE);
        } else {
            time = (String) defaultValue;
        }

        hour = getHour(time);
        minute = getMinute(time);

        setSummary(hour + ":" + minute + " AM");
    }


    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    private static int getHour(String time) {
        String[] timePieces = time.split(":");
        return Integer.parseInt(timePieces[0]);
    }

    private static int getMinute(String time) {
        String[] timePieces = time.split(":");
        return Integer.parseInt(timePieces[1]);
    }


}
