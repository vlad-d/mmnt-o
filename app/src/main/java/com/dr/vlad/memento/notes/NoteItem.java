package com.dr.vlad.memento.notes;

/**
 * Created by drinc on 1/5/2017.
 */

public class NoteItem {
    private long id;
    private long noteId;
    private String text;
    private int order;
    private long editedAt;

    public NoteItem(long noteId, String text, int order, long editedAt) {

        this.noteId = noteId;
        this.text = text;
        this.order = order;
        this.editedAt = editedAt;
    }

    public NoteItem(long id, long noteId, String text, int order, long editedAt) {

        this.id = id;
        this.noteId = noteId;
        this.text = text;
        this.order = order;
        this.editedAt = editedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public long getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(long editedAt) {
        this.editedAt = editedAt;
    }
}
