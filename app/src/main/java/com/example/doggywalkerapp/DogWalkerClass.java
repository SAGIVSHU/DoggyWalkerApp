package com.example.doggywalkerapp;

import java.io.Serializable;
import java.util.ArrayList;

public class DogWalkerClass implements Serializable {
    private String dogWalkerName;
    private String dogWalkerPhoneNumber;
    private String DogWalkerRating;
    private String DogWalkerLocation;
    private String walkerId;
    private String WalkerSumOfTrips;

    public DogWalkerClass(String dogWalkerName, String dogWalkerPhoneNumber, String dogWalkerRating, String dogWalkerLocation, String walkerId, String walkerSumOfTrips) {
        this.dogWalkerName = dogWalkerName;
        this.dogWalkerPhoneNumber = dogWalkerPhoneNumber;
        this.DogWalkerRating = dogWalkerRating;
        this.DogWalkerLocation = dogWalkerLocation;
        this.walkerId = walkerId;
        this.WalkerSumOfTrips = walkerSumOfTrips;
    }

    public DogWalkerClass() {
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
        return DogWalkerRating;
    }

    public void setDogWalkerRating(String dogWalkerRating) {
        DogWalkerRating = dogWalkerRating;
    }

    public String getDogWalkerLocation() {
        return DogWalkerLocation;
    }

    public void setDogWalkerLocation(String dogWalkerLocation) {
        DogWalkerLocation = dogWalkerLocation;
    }

    public String getWalkerId() {
        return walkerId;
    }

    public void setWalkerId(String walkerId) {
        this.walkerId = walkerId;
    }

    public String getWalkerSumOfTrips() {
        return WalkerSumOfTrips;
    }

    public void setWalkerSumOfTrips(String walkerSumOfTrips) {
        WalkerSumOfTrips = walkerSumOfTrips;
    }

    @Override
    public String toString() {
        return "DogWalkerClass{" +
                "dogWalkerName='" + dogWalkerName + '\'' +
                ", dogWalkerPhoneNumber='" + dogWalkerPhoneNumber + '\'' +
                ", DogWalkerRating='" + DogWalkerRating + '\'' +
                ", DogWalkerLocation='" + DogWalkerLocation + '\'' +
                ", walkerId='" + walkerId + '\'' +
                ", WalkerSumOfTrips='" + WalkerSumOfTrips + '\'' +
                '}';
    }
}
