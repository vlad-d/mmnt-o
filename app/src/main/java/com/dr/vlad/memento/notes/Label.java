package com.dr.vlad.memento.notes;

/**
 * Created by drinc on 1/5/2017.
 */

public class Label {
    private Long id;
    private String title;
    private String color;
    private Long createdAt;

    public Label(String title, String color, Long createdAt) {
        this.title = title;
        this.color = color;
        this.createdAt = createdAt;
    }

    public Label(Long id, String title, String color, Long createdAt) {
        this.id = id;
        this.title = title;
        this.color = color;
        this.createdAt = createdAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
