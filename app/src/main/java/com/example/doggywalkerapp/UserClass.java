package com.example.doggywalkerapp;

import java.io.Serializable;

public class UserClass implements Serializable {
    private String userName;
    private String email;
    private String phoneNumber;
    private String dogRace;
    private String location;
    private String uid;


    public UserClass() {
    }

    public UserClass(String email, String userName, String phoneNumber, String dogRace, String location, String uid) {
        this.email = email;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.dogRace = dogRace;
        this.location = location;
        this.uid = uid;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDogRace() {
        return dogRace;
    }

    public void setDogRace(String dogRace) {
        this.dogRace = dogRace;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "UserClass{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dogRace='" + dogRace + '\'' +
                ", location='" + location + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
