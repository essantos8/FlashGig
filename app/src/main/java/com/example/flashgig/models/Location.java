package com.example.flashgig.models;

public class Location {

    public String region;
    public String city;
    public String baranggay;

    public Location() {
    }

    public Location(String region, String city, String baranggay) {
        this.region = region;
        this.city = city;
        this.baranggay = baranggay;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBaranggay() {
        return baranggay;
    }

    public void setBaranggay(String baranggay) {
        this.baranggay = baranggay;
    }

}
