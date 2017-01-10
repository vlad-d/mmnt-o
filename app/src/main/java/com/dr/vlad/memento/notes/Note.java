package com.dr.vlad.memento.notes;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by drinc on 1/5/2017.
 */

public class Note {
    private long id;
    private long labelId;
    private String title;
    private long createdAt;
    private long deletedAt;
    private List<NoteItem> items;

    public Note(long id, long labelId, String title, long createdAt, long deletedAt) {
        this.id = id;
        this.labelId = labelId;
        this.title = title;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public Note(String title, long createdAt, long deletedAt, List<NoteItem> items) {
        this.title = title;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.items = items;
        this.labelId = 0;
    }

    public Note(String title, long createdAt,  long deletedAt) {
        this.title = title;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.labelId = 0;
    }

    public Note(long id, String title, long createdAt, long deletedAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.labelId = 0;
    }

    public List<NoteItem> getItems() {
        return items;
    }

    public void setItems(List<NoteItem> items) {
        this.items = items;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLabelId() {
        return labelId;
    }

    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(long deletedAt) {
        this.deletedAt = deletedAt;
    }
}
