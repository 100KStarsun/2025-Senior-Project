package com.agora.app.backend.base;

public class Listing {

    private String title;
    private String description;

    public Listing(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }

}
