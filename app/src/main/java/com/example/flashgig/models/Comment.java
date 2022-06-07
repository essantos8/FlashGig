package com.example.flashgig.models;

public class Comment {
    public String rator, comment;
    public Float rating;
    public Comment() {

    }
    public Comment(String rator,float rating, String comment){
        this.rator = rator;
        this.comment = comment;
        this.rating = rating;
    }


    public String getRator(){return rator;}
    public String getText() {return comment;}
    public Float getRating(){return rating;}
}


