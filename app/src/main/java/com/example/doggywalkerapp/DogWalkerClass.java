package com.example.doggywalkerapp;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class DogWalkerClass implements Serializable {
    private String dogWalkerName;
    private String dogWalkerPhoneNumber;
    private String dogWalkerRating;
    private String dogWalkerLocation;
    private String walkerId;
    private String walkerSumRatedTrips;

    public DogWalkerClass() {

    }

    public DogWalkerClass(String dogWalkerName, String dogWalkerPhoneNumber, String dogWalkerRating, String dogWalkerLocation,
                          String walkerId, String walkerSumRatedTrips) {
        this.dogWalkerName = dogWalkerName;
        this.dogWalkerPhoneNumber = dogWalkerPhoneNumber;
        this.dogWalkerRating = dogWalkerRating;
        this.dogWalkerLocation = dogWalkerLocation;
        this.walkerId = walkerId;
        this.walkerSumRatedTrips = walkerSumRatedTrips;
    }

    public String getDogWalkerName() {
        return dogWalkerName;
    }

    public void setDogWalkerName(String dogWalkerName) {
        this.dogWalkerName = dogWalkerName;
    }

    public String getDogWalkerPhoneNumber() {
        return dogWalkerPhoneNumber;
    }

    public void setDogWalkerPhoneNumber(String dogWalkerPhoneNumber) {
        this.dogWalkerPhoneNumber = dogWalkerPhoneNumber;
    }

    public String getDogWalkerRating() {
        return dogWalkerRating;
    }

    public void setDogWalkerRating(String dogWalkerRating) {
        this.dogWalkerRating = dogWalkerRating;
    }

    public String getDogWalkerLocation() {
        return dogWalkerLocation;
    }

    public void setDogWalkerLocation(String dogWalkerLocation) {
        this.dogWalkerLocation = dogWalkerLocation;
    }

    public String getWalkerId() {
        return walkerId;
    }

    public void setWalkerId(String walkerId) {
        this.walkerId = walkerId;
    }


    public String getWalkerSumRatedTrips() {
        return walkerSumRatedTrips;
    }

    public void setWalkerSumRatedTrips(String walkerSumRatedTrips) {
        this.walkerSumRatedTrips = walkerSumRatedTrips;
    }

    @NonNull
    @Override
    public String toString() {
        return "DogWalkerClass{" +
                "dogWalkerName='" + dogWalkerName + '\'' +
                ", dogWalkerPhoneNumber='" + dogWalkerPhoneNumber + '\'' +
                ", dogWalkerRating='" + dogWalkerRating + '\'' +
                ", dogWalkerLocation='" + dogWalkerLocation + '\'' +
                ", walkerId='" + walkerId + '\'' +
                ", walkerSumRatedTrips='" + walkerSumRatedTrips + '\'' +
                '}';
    }
}
