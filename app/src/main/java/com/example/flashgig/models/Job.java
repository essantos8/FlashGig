package com.example.flashgig.models;

import java.util.ArrayList;

public class Job {
    public String title, description, jobId;
    public ArrayList<String> categories;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        if(description == null){
            description = "nondescript";
        }
        return description;
    }

    public String getJobId() {
        return jobId;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public Job(){}

    public Job(String title, String description, ArrayList<String> categories, String jobId) {
        this.title = title;
        this.description = description;
        this.categories = categories;
        this.jobId = jobId;
    }
}
