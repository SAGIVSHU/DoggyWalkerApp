package com.example.doggywalkerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.doggywalkerapp.databinding.ActivityOrderTripBinding;

public class TripHistoryActivity extends DrawerBaseActivity {

    ActivityOrderTripBinding activityTripHistoryBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTripHistoryBinding= ActivityOrderTripBinding.inflate(getLayoutInflater());
        setContentView(activityTripHistoryBinding.getRoot());
        allocateActivityTitle("Trip History");
    }
}