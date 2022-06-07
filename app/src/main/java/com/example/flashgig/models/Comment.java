package com.example.flashgig.models;

public class Comment {
    public String rator, ratee, jobId, comment;
    public Float rating;
    public Comment() {

    }
    public Comment(String rator, String ratee, String jobId, String comment, Float rating){
        this.rator = rator;
        this.ratee = ratee;
        this.jobId = jobId;
        this.comment = comment;
        this.rating = rating;
    }

    public String getRator(){return rator;}
    public String getRatee(){return ratee;}
    public String getJobId(){return jobId;}
    public String getComment() {return comment;}
    public Float getRating(){return rating;}
}


