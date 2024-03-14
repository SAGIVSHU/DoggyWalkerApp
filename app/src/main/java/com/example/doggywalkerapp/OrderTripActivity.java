package com.example.doggywalkerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.doggywalkerapp.databinding.ActivityOrderTripBinding;
import com.example.doggywalkerapp.databinding.ActivityUserPageBinding;

public class OrderTripActivity extends DrawerBaseActivity {

    ActivityOrderTripBinding activityOrderTripBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOrderTripBinding = ActivityOrderTripBinding.inflate(getLayoutInflater());
        setContentView(activityOrderTripBinding.getRoot());
        allocateActivityTitle("Order Trip");
    }
}