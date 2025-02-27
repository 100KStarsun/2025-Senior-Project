package com.agora.app.backend.base;


// basic class placeholder to allow for listings to ba added on user info page
// takes into account listing title and description
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
