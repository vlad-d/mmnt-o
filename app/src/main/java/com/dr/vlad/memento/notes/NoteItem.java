package com.dr.vlad.memento.notes;

/**
 * Created by drinc on 1/5/2017.
 */

public class NoteItem {
    private Long id;
    private Long noteId;
    private String text;
    private Integer order;
    private Long editedAt;

    /**
     * @param noteId
     * @param text
     * @param order
     * @param editedAt
     */
    public NoteItem(Long noteId, String text, Integer order, Long editedAt) {

        this.noteId = noteId;
        this.text = text;
        this.order = order;
        this.editedAt = editedAt;
    }

    public NoteItem(Long id, Long noteId, String text, Integer order, Long editedAt) {

        this.id = id;
        this.noteId = noteId;
        this.text = text;
        this.order = order;
        this.editedAt = editedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Long getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(Long editedAt) {
        this.editedAt = editedAt;
    }
}
