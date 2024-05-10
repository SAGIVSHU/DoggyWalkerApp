package com.example.doggywalkerapp;

import java.io.Serializable;

public class TripClass extends DogWalkerClass implements Serializable {
    private String tripId;
    private String orderDate;
    private String orderDay;
    private String personWhoOrderedUid;
    private String tripOccurDay;

    public TripClass() {
        super();
    }

    public TripClass(DogWalkerClass dogWalker, String orderDay, String orderDate,String tripOccurDay, String personWhoOrderedUid) {
        super(dogWalker.getDogWalkerName(), dogWalker.getDogWalkerPhoneNumber(), dogWalker.getDogWalkerRating(), dogWalker.getDogWalkerLocation(), dogWalker.getWalkerId(), dogWalker.getWalkerSumRatedTrips());
        this.orderDate = orderDate;
        this.orderDay = orderDay;
        this.personWhoOrderedUid = personWhoOrderedUid;
        this.tripOccurDay = tripOccurDay;

    }

    public String getTripOccurDay() {
        return tripOccurDay;
    }

    public void setTripOccurDay(String tripOccurDay) {
        this.tripOccurDay = tripOccurDay;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPersonWhoOrderedUid() {
        return personWhoOrderedUid;
    }

    public void setPersonWhoOrderedUid(String personWhoOrderedUid) {
        this.personWhoOrderedUid = personWhoOrderedUid;
    }

    public String getOrderDay() {
        return orderDay;
    }

    public void setOrderDay(String orderDay) {
        this.orderDay = orderDay;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    @Override
    public String toString() {
        return super.toString()+ "\nTripClass{" +
                "tripId='" + tripId + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", orderDay='" + orderDay + '\'' +
                ", personWhoOrderedUid='" + personWhoOrderedUid + '\'' +
                ", tripOccurDay='" + tripOccurDay + '\'' +
                '}';
    }
}
