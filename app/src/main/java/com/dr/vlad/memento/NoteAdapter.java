package com.dr.vlad.memento;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dr.vlad.memento.notes.Note;
import com.dr.vlad.memento.notes.NoteItem;

import java.util.List;

/**
 * Created by vlad.drinceanu on 11.01.2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private Context context;
    private List<Note> notes;

    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder holder, int position) {
        final Note note = notes.get(position);
        holder.bindNote(note);

    }


    @Override
    public int getItemCount() {
        return notes.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        CardView cardView;
        TextView tvTitle;
        TextView tvBody;
        long noteId;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.ll_note_item);
            cardView = (CardView) itemView.findViewById(R.id.cv_note_item);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_note_title);
            tvBody = (TextView) itemView.findViewById(R.id.tv_note_body);
        }

        public void bindNote(Note note) {
            noteId = note.getId();
            String title = note.getTitle();
            if (title.isEmpty()) {
                tvTitle.setVisibility(View.GONE);
            } else {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(title);
                tvTitle.setTypeface(null, Typeface.BOLD);
            }
            List<NoteItem> items = note.getItems();
            int itemsSize = items.size();
            if (!items.isEmpty()) {
                for (int i = 0; i < itemsSize; i++) {
                    String text = items.get(i).getText();
                    if (!text.isEmpty()) {
                        tvBody.setVisibility(View.VISIBLE);
                        tvBody.append(text);
                        if (items.get(i).getDone() != 0) {
                            tvBody.setPaintFlags(tvBody.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }

                        if (i < itemsSize - 1) {
                            tvBody.append("\n");
                        }
                    }

                }

            }

            if (tvBody.getText().toString().isEmpty()) {
                tvBody.setVisibility(View.GONE);
            }

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), NoteActivity.class);
                    intent.putExtra(itemView.getContext().getResources().getString(R.string.key_note_id), noteId);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
