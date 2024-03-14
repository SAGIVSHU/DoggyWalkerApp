package com.example.doggywalkerapp;

import java.io.Serializable;

public class User implements Serializable {
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private String dogRace;
    private String location;
    private String uid;

    private String extension;

    public User() {
    }

    public User(String email, String userName, String password, String phoneNumber, String dogRace, String location, String uid, String extension) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.dogRace = dogRace;
        this.location = location;
        this.uid = uid;
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        return "User{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dogRace='" + dogRace + '\'' +
                ", location='" + location + '\'' +
                ", uid='" + uid + '\'' +
                ", extension='" + extension + '\'' +
                '}';
    }
}
