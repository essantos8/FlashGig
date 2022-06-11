package com.example.flashgig.models;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    // Required info
    public String fullName;
    public String email;
    public String phone;
    public String userId;
    // Additional info
    public String about = "";
    public ArrayList<String> skills = new ArrayList<>();
    public HashMap<String, Comment> ratings = new HashMap<>();

    public User() {
    }

    // Constructor for new Users
    public User(String fullName, String email, String phone, String userId) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public ArrayList<String> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<String> skills) {
        this.skills = skills;
    }

    // Constructor for existing/editing user profiles
    public User(String fullName, String email, String phone, String userId, String about, ArrayList<String> skills, HashMap<String, Comment> ratings) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
        this.about = about;
        this.skills = skills;
        this.ratings = ratings;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Comment getComment(String jobId) {return this.ratings.get(jobId);}

    public float getAverageRating() {
        float sum = 0;
        for (Comment i: this.ratings.values()){
            sum += i.getRating();
        }
        float avg = sum / this.ratings.values().size();
        return avg;
    }
}
