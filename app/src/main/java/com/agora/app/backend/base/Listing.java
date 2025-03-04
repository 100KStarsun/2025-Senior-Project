package com.agora.app.backend.base;


// basic class placeholder to allow for listings to ba added on user info page
// takes into account listing title and description
public class Listing {

    private String title;
    private String description;
    private String tag1;
    private String tag2;
    private String tag3;

    public Listing(String title, String description, String tag1, String tag2, String tag3) {
        this.title = title;
        this.description = description;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }

    public String getTag1(){
        return tag1;
    }
    public String getTag2(){
        return tag2;
    }
    public String getTag3(){
        return tag3;
    }
 

}
