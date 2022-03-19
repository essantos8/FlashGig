package com.example.flashgig.models;

import java.util.ArrayList;

public class Job {

    public String title, description, client, date, jobId, location, budget;

    public Integer numWorkers;

    public ArrayList<String> categories, workers, bidders;

    public Job() {
    }
    // Constructor for new jobs
    public Job(String title, String description, String client, String date, ArrayList<String> categories, Integer numWorkers, String location, String budget, String jobId) {
        this.title = title;
        this.description = description;
        this.client = client;
        this.date = date;
        this.categories = categories;
        this.numWorkers = numWorkers;
        this.location = location;
        this.budget = budget;
        this.bidders = new ArrayList<>();
        this.workers = new ArrayList<>();
        this.jobId = jobId;
    }
    //
    public Job(String title, String description, String client, String date, ArrayList<String> categories, Integer numWorkers, String location, String budget, ArrayList<String> workers, ArrayList<String> bidders, String jobId) {
        this.title = title;
        this.description = description;
        this.client = client;
        this.date = date;
        this.categories = categories;
        this.numWorkers = numWorkers;
        this.location = location;
        this.budget = budget;
        this.bidders = bidders;
        this.workers = workers;
        this.jobId = jobId;
    }

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

    public void setBidders(ArrayList<String> bidders) {
        this.bidders = bidders;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        if (description == null) {
            description = "No description given.";
        }
        return description;
    }

    public String getJobId() {
        return jobId;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public ArrayList<String> getBidders() {
        return bidders;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getWorkers() {
        return workers;
    }

    public void setWorkers(ArrayList<String> workers) {
        this.workers = workers;
    }

    public Integer getNumWorkers() {
        return numWorkers;
    }

    public void setNumWorkers(Integer numWorkers) {
        this.numWorkers = numWorkers;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }
}
