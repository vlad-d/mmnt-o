package com.dr.vlad.memento;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder holder, int position) {
        final Note note = notes.get(position);
        String title = note.getTitle();
        if (!title.isEmpty()) {
            TextView tvTitle = createTextView(false);
            tvTitle.setText(title);
            tvTitle.setTypeface(null, Typeface.BOLD);
            holder.layout.addView(tvTitle);
        }

        List<NoteItem> items = note.getItems();
        if (!items.isEmpty()) {
            for (NoteItem item : items) {
                String text = item.getText();
                if (!text.isEmpty()) {
                    TextView tvItem = createTextView(item.getDone() != 0);
                    tvItem.setText(text);
                    holder.layout.addView(tvItem);
                }

            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NoteActivity.class);
                intent.putExtra(context.getResources().getString(R.string.key_note_id), note.getId());
                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * @param drawLine
     * @return TextView
     */
    private TextView createTextView(boolean drawLine) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(24, 0, 24, 24);
        textView.setLayoutParams(params);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary));

        if (drawLine) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        return textView;
    }

    private TextView createLabelTextView(int color) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 8, 0, 0);
        textView.setLayoutParams(params);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setBackgroundColor(color);

        return textView;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.ll_note_item);
            cardView = (CardView) itemView.findViewById(R.id.cv_note_item);
        }
    }
}
