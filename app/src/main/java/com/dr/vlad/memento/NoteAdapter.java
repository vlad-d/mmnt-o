package com.dr.vlad.memento;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dr.vlad.memento.fragments.FingerprintAuthDialogFragment;
import com.dr.vlad.memento.notes.Note;
import com.dr.vlad.memento.notes.NoteItem;

import java.util.List;

/**
 * Created by vlad.drinceanu on 11.01.2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private Context context;
    private List<Note> notes;
    private MainActivity mActivity;

    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
        this.mActivity = (MainActivity)context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Note note = notes.get(position);
        List<NoteItem> items = note.getItems();
        if (note.getProtect() == 0) {
            holder.noteProtected.setVisibility(View.GONE);
        } else {
            holder.noteProtected.setVisibility(View.VISIBLE);
        }
        holder.tvTitle.setText(note.getTitle());
        int itemsSize = items.size();
        if (!items.isEmpty()) {
            holder.tvBody.setText(items.get(0).getText());
//            for (int i = 0; i < itemsSize; i++) {
//                String text = items.get(i).getText();
//                if (!text.isEmpty()) {
//                    holder.tvBody.append(text);
//                    if (items.get(i).getDone() != 0) {
//                        holder.tvBody.setPaintFlags(holder.tvBody.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                    }
//
//                    if (i < itemsSize - 1) {
//                        holder.tvBody.append("\n");
//                    }
//                }
//            }
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (note.getProtect() != 0) {
                    FingerprintAuthDialogFragment fragment = new FingerprintAuthDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong(context.getResources().getString(R.string.key_note_id), note.getId());
                    fragment.setArguments(bundle);
                    fragment.show(((Activity) context).getFragmentManager(), DIALOG_FRAGMENT_TAG);
                } else {
                    mActivity.openNote(note.getId());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }




    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvBody;
        private CardView cardView;
        private RelativeLayout noteProtected;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_note_title);
            tvBody = (TextView) itemView.findViewById(R.id.tv_note_body);
            cardView = (CardView) itemView.findViewById(R.id.cv_note_item);
            noteProtected = (RelativeLayout) itemView.findViewById(R.id.rl_note_protected);
        }
    }

}
