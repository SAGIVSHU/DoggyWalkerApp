package com.example.doggywalkerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.doggywalkerapp.databinding.ActivityOrderTripBinding;
import com.example.doggywalkerapp.databinding.ActivityTripHistoryBinding;

public class TripHistoryActivity extends DrawerBaseActivity {
    private ActivityTripHistoryBinding activityTripHistoryBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTripHistoryBinding = ActivityTripHistoryBinding.inflate(getLayoutInflater());
        setContentView(activityTripHistoryBinding.getRoot());
        allocateActivityTitle("Trip History");
    }
}