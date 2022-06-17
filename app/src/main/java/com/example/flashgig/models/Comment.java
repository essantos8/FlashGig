package com.example.flashgig.models;

import java.io.Serializable;

public class Comment implements Serializable {
    public String rator, comment, ratorId;
    public Float rating;
    public Comment() {

    }
    public Comment(String rator,float rating, String comment, String ratorId){
        this.rator = rator;
        this.ratorId = ratorId;
        this.comment = comment;
        this.rating = rating;
    }


    public String getRator(){return rator;}
    public String getText() {return comment;}
    public Float getRating(){return rating;}
    public String getRatorId(){return ratorId;}
}


