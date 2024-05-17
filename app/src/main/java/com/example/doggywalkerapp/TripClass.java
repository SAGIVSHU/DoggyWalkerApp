package com.example.doggywalkerapp;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TripClass extends DogWalkerClass implements Serializable {
    private String tripId;
    private String orderDate;
    private String orderDay;
    private String personWhoOrderedUid;
    private String tripOccurDay;
    private String tripOccurDate;

    public TripClass() {
        super();
    }

    public TripClass(DogWalkerClass dogWalker, String orderDay, String orderDate, String tripOccurDay, String personWhoOrderedUid) {
        super(dogWalker.getDogWalkerName(), dogWalker.getDogWalkerPhoneNumber(), dogWalker.getDogWalkerRating(), dogWalker.getDogWalkerLocation(), dogWalker.getWalkerId(), dogWalker.getWalkerSumRatedTrips());
        this.orderDate = orderDate;
        this.orderDay = orderDay;
        this.personWhoOrderedUid = personWhoOrderedUid;
        this.tripOccurDay = tripOccurDay;
        this.tripOccurDate = getDateForDayOfWeek(tripOccurDay);

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


    public String getTripOccurDate() {
        return tripOccurDate;
    }

    public void setTripOccurDate(String tripOccurDate) {
        this.tripOccurDate = tripOccurDate;
    }

    private String getDateForDayOfWeek(String dayOfWeekStr) {
        DayOfWeek inputDayOfWeek = DayOfWeek.valueOf(dayOfWeekStr.toUpperCase());

        LocalDate today = LocalDate.now();
        DayOfWeek currentDayOfWeek = today.getDayOfWeek();
        int daysToAdd = inputDayOfWeek.getValue() - currentDayOfWeek.getValue();
        if (daysToAdd < 0) {
            daysToAdd += 7; // To handle days earlier in the week
        }
        LocalDate date = today.plusDays(daysToAdd);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);

    }

    @Override
    public String toString() {
        return super.toString() + " TripClass{" +
                "tripId='" + tripId + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", orderDay='" + orderDay + '\'' +
                ", personWhoOrderedUid='" + personWhoOrderedUid + '\'' +
                ", tripOccurDay='" + tripOccurDay + '\'' +
                ", tripOccurDate='" + tripOccurDate + '\'' +
                '}';
    }
}
