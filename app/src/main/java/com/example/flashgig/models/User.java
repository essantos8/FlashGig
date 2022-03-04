package com.example.flashgig.models;

public class User {
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

    public String fullName, email, phone, userId;

    public User(){
    }

    public User(String fullName, String email, String phone, String userId){
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
    }
}
