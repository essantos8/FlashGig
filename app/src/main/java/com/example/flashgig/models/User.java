package com.example.flashgig.models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    // Required info
    public String fullName;
    public String email;
    public String phone;
    public String userId;
    // Additional info
    public String about = "";
    public ArrayList<String> skills = new ArrayList<>();

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
    public User(String fullName, String email, String phone, String userId, String about, ArrayList<String> skills) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
        this.about = about;
        this.skills = skills;
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
}
