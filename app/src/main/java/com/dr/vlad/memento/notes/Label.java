package com.dr.vlad.memento.notes;

/**
 * Created by drinc on 1/5/2017.
 */

public class Label {
    private long id;
    private String title;
    private String color;
    private long createdAt;

    public Label(String title, String color, long createdAt) {
        this.title = title;
        this.color = color;
        this.createdAt = createdAt;
    }

    public Label(long id, String title, String color, long createdAt) {
        this.id = id;
        this.title = title;
        this.color = color;
        this.createdAt = createdAt;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
