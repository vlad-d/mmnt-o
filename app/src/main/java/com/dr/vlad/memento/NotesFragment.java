package com.dr.vlad.memento;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dr.vlad.memento.model.Note;

import java.util.List;

public class NotesFragment extends Fragment {

    public static final String TAG = NotesFragment.class.getSimpleName();
    List<Note> notes;
    RecyclerView rvMainRecyclerView;
    RecyclerView.Adapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notes = getNotesWithItems();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        TextView tvEmptyList = (TextView) view.findViewById(R.id.tv_empty_list);
        if (notes.isEmpty()) {
            if (tvEmptyList.getVisibility() != View.VISIBLE) {
                tvEmptyList.setVisibility(View.VISIBLE);
            }
        } else {
            tvEmptyList.setVisibility(View.GONE);
            rvMainRecyclerView = (RecyclerView) view.findViewById(R.id.rv_main);
            rvMainRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            adapter = new NoteAdapter(this.getContext(), notes);
            rvMainRecyclerView.setAdapter(adapter);
        }

        return view;
    }
}
