package com.example.flashgig.models;

import android.util.Log;

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
    public HashMap<String, Integer> ratings = new HashMap<>();

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
    public User(String fullName, String email, String phone, String userId, String about, ArrayList<String> skills, HashMap<String, Integer> ratings) {
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

    public Integer getRating(String jobId) {return this.ratings.get(jobId);}

    public float getAverageRating() {
        float sum = 0;
        for (int i: this.ratings.values()){
            Log.d("VALS", ": "+i);
            sum += i;
        }
        float avg = sum / this.ratings.values().size();
        Log.d("AVG", "getAverageRating: " + sum + "size: " + this.ratings.values().size() + "avg" + avg);
        return avg;
    }
}
