package com.example.doggywalkerapp;

import java.io.Serializable;
import java.util.ArrayList;

public class DogWalkerClass implements Serializable {
    private String Name;
    private String phoneNumber;
    private String yearsOfExperience;
    private String rating;
    private String location;
    private String walkerId;
    private String sumOfTrips;

    public DogWalkerClass(String name, String phoneNumber, String yearsOfExperience, String rating, String location, String walkerId, String sumOfTrips) {
        Name = name;
        this.phoneNumber = phoneNumber;
        this.yearsOfExperience = yearsOfExperience;
        this.rating = rating;
        this.location = location;
        this.walkerId = walkerId;
        this.sumOfTrips = sumOfTrips;
    }





    public DogWalkerClass() {
    }

    public String getName() {
        return Name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getYearsOfExperience() {
        return yearsOfExperience;
    }

    public String getRating() {
        return rating;
    }

    public String getLocation() {
        return location;
    }

    public String getWalkerId() {
        return walkerId;
    }


    public String getSumOfTrips() {
        return sumOfTrips;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setYearsOfExperience(String yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setWalkerId(String walkerId) {
        this.walkerId = walkerId;
    }


    public void setSumOfTrips(String sumOfTrips) {
        this.sumOfTrips = sumOfTrips;
    }

    @Override
    public String toString() {
        return "DogWalkerClass{" +
                "Name='" + Name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", yearsOfExperience='" + yearsOfExperience + '\'' +
                ", rating='" + rating + '\'' +
                ", location='" + location + '\'' +
                ", walkerId='" + walkerId + '\'' +
                ", sumOfTrips='" + sumOfTrips + '\'' +
                '}';
    }
}
