package com.example.doggywalkerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.doggywalkerapp.databinding.ActivityOrderTripBinding;
import com.example.doggywalkerapp.databinding.ActivityTripHistoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class TripHistoryActivity extends DrawerBaseActivity implements RecyclerViewInterface {
    private ActivityTripHistoryBinding activityTripHistoryBinding; //for menu

    private RecyclerView recyclerView; // recycle view
    private DatabaseReference pastTripsDbRef;//firebase reference
    private TripClassAdapter tripClassAdapter; // trip class adapter for recycle view
    private ArrayList<TripClass> pastTripsList; // list of all past trips

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTripHistoryBinding = ActivityTripHistoryBinding.inflate(getLayoutInflater());
        setContentView(activityTripHistoryBinding.getRoot());
        allocateActivityTitle("Trip History");


        //database reference for future trips
        pastTripsDbRef = FirebaseDatabase.getInstance().getReference("Users/" + getCurrentUser().getUid() + "/PastTrips");

        //recycle view
        recyclerView = findViewById(R.id.pastTripsRv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //create list and connect to the adapter
        pastTripsList = new ArrayList<>(); // create an empty list of dog walkers
        tripClassAdapter = new TripClassAdapter(this, pastTripsList, this);
        recyclerView.setAdapter(tripClassAdapter);

        pastTripsDbRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TripClass tripClass = dataSnapshot.getValue(TripClass.class);
                    pastTripsList.add(tripClass);
                }
                tripClassAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private UserClass getCurrentUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("User", "");
        UserClass user = gson.fromJson(json, UserClass.class);
        return user;

    }

    @Override
    public void onItemClick(int position) {

    }
}