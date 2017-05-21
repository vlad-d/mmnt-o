package com.dr.vlad.memento.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dr.vlad.memento.MainActivity;
import com.dr.vlad.memento.R;
import com.dr.vlad.memento.model.Note;

import java.util.List;

public class ReminderNotesFragment extends Fragment {

    public static final String TAG = NotesFragment.class.getSimpleName();
    List<Note> notes;
    RecyclerView rvMainRecyclerView;
    RecyclerView.Adapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_notes, container, false);
        TextView tvEmptyList = (TextView) view.findViewById(R.id.tv_empty_list_reminders);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            ((MainActivity) context).setToolbarTitle("Reminders");
        }
    }
}
