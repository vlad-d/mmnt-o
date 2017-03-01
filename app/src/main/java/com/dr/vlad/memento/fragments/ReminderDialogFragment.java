package com.dr.vlad.memento.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dr.vlad.memento.NoteActivity;
import com.dr.vlad.memento.R;
import com.dr.vlad.memento.SettingsActivity;

/**
 * Created by drinc on 2/26/2017.
 */

public class ReminderDialogFragment extends DialogFragment implements View.OnClickListener {

    private LinearLayout llMorning;
    private LinearLayout llPickDateTime;
    private LinearLayout llWork;
    private LinearLayout llPickLocation;
    private TextView tvMorningTime;
    private TextView tvWorkLocation;
    private NoteActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminder_dialog, container, false);

        llMorning = (LinearLayout) view.findViewById(R.id.ll_reminder_tomorrow_morning);
        llMorning.setOnClickListener(this);
        llPickDateTime = (LinearLayout) view.findViewById(R.id.ll_reminder_pick_date_time);
        llPickDateTime.setOnClickListener(this);
        llWork = (LinearLayout) view.findViewById(R.id.ll_reminder_at_work);
        llWork.setOnClickListener(this);
        llPickLocation = (LinearLayout) view.findViewById(R.id.ll_reminder_pick_place);
        llPickLocation.setOnClickListener(this);
        tvMorningTime = (TextView) view.findViewById(R.id.tv_morning_time);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String morningTimePref = preferences.getString("pref_key_morning_time", "");

        tvMorningTime.setText(morningTimePref + " AM");

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ll_reminder_tomorrow_morning:

                dismiss();
                break;

            case R.id.ll_reminder_at_work:

                dismiss();
                break;

            case R.id.ll_reminder_pick_date_time:
                mActivity.showDatePicker();
                dismiss();
                break;

            case R.id.ll_reminder_pick_place:
                mActivity.showLocationPicker();
                dismiss();
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NoteActivity) {
            mActivity = (NoteActivity) context;
        }
    }


}
