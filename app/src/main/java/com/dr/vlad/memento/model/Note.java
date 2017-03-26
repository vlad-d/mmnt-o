package com.dr.vlad.memento.model;

import java.util.List;

/**
 * Created by drinc on 1/5/2017.
 */

public class Note {
    private Long id;
    private Long labelId;
    private String title;
    private Integer protect;
    private Long createdAt;
    private Long deletedAt;
    private List<NoteItem> items;

    public Note() {
    }

    public Note(Long id, Long labelId, String title, Integer protect, Long createdAt, Long deletedAt) {
        this.id = id;
        this.labelId = labelId;
        this.title = title;
        this.protect = protect;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public Note(String title, Long createdAt, Long deletedAt, List<NoteItem> items) {
        this.title = title;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.items = items;
        this.labelId = null;
    }

    public Note(String title, Long createdAt, Long deletedAt) {
        this.title = title;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.labelId = null;
    }

    public Note(Long id, String title, Long createdAt, Long deletedAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.labelId = null;
    }

    public List<NoteItem> getItems() {
        return items;
    }

    public void setItems(List<NoteItem> items) {
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getProtect() {
        return protect;
    }

    public void setProtect(Integer protect) {
        this.protect = protect;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Long deletedAt) {
        this.deletedAt = deletedAt;
    }
}
