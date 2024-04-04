package com.example.doggywalkerapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doggywalkerapp.databinding.ActivityDogWalkersPerDayBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class DogWalkersPerDayActivity extends DrawerBaseActivity implements RecyclerViewInterface {
    private ActivityDogWalkersPerDayBinding activityDogWalkersPerDayBinding;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private DogWalkerAdapter adapter;
    private ArrayList<DogWalkerClass> list;
    private Dialog dialog;
    private TextView yesDialog, noDialog, mainTextDialog;
    private DogWalkerClass pickedWalker;
    private String pickedDay;
    private UserClass personWhoOrdered;
    private DatabaseReference futureTripDbRef;
    private TripClass currentTrip;
    private boolean isOrdered; // a flag for knowing if the current user already ordered a trip in this activity;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDogWalkersPerDayBinding = ActivityDogWalkersPerDayBinding.inflate(getLayoutInflater());
        setContentView(activityDogWalkersPerDayBinding.getRoot());
        allocateActivityTitle("Order a Trip");

        isOrdered = false; // the user didn't order yet

        personWhoOrdered = getCurrentUser(); // get current user by shared presences
        futureTripDbRef = FirebaseDatabase.getInstance().getReference("Users/" + personWhoOrdered.getUid() + "/FutureTrips"); //data base


        Intent intent = getIntent();
        pickedDay = intent.getStringExtra("DAY_KEY"); //get the day from the previous intent

        //RecycleView Part
        recyclerView = findViewById(R.id.dogWalkersList);
        databaseReference = FirebaseDatabase.getInstance().getReference("DogWalkersFolder/" + pickedDay); // get the data from the day
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new DogWalkerAdapter(this, list, this);
        recyclerView.setAdapter(adapter);


        //Get data from database to list
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DogWalkerClass dogWalker = dataSnapshot.getValue(DogWalkerClass.class);
                    list.add(dogWalker);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_trip_picker_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        yesDialog = (TextView) dialog.findViewById(R.id.yesTxt);
        noDialog = (TextView) dialog.findViewById(R.id.noTxt);
        mainTextDialog = (TextView) dialog.findViewById(R.id.mainTxt);

        yesDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickedDay != null) {
                    if (!isOrdered) {
                        //get and set current date and time
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date();
                        String currentDate = formatter.format(date);

                        //create a trip class
                        String currentDay = new SimpleDateFormat("EEEE").format(new Date()); //day at the moment
                        currentTrip = new TripClass(pickedWalker, currentDay, currentDate, pickedDay, personWhoOrdered.getUid());
                        saveTripToDB();
                        Log.d("tripTripgig", currentTrip.toString());
                        showToast(pickedWalker.getDogWalkerName() + " was ordered for " + pickedDay);
                        startActivity(new Intent(DogWalkersPerDayActivity.this,UserPageActivity.class));
                    } else {
                        showToast("You already ordered a trip for " + pickedDay);
                    }
                }
                dialog.dismiss();

            }
        });
        noDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(int position) {
        mainTextDialog.setText("Do you want to order a trip for " + pickedDay);
        pickedWalker = list.get(position);
        dialog.show();
    }

    private void saveTripToDB() {
        String tripId = futureTripDbRef.push().getKey();
        if (currentTrip != null && tripId != null) {

            currentTrip.setTripId(tripId);
            futureTripDbRef.child(tripId).setValue(currentTrip)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            isOrdered = true;// set the flag for true and now the user cant order again
                            // Data successfully saved
                            Log.d(TAG, "Trip saved successfully!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle error
                            Log.e(TAG, "Error saving trip: " + e.getMessage());
                        }
                    });
        }
    }


    private UserClass getCurrentUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("User", "");
        UserClass user = gson.fromJson(json, UserClass.class);
        return user;

    }

    private void showToast(String text) {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        View my_toast = inflater.inflate(R.layout.my_toast, findViewById(R.id.my_toast));
        TextView tv = my_toast.findViewById(R.id.tv_my_toast);
        tv.setText(text);
        Toast toast = new Toast(DogWalkersPerDayActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(my_toast);
        toast.show();
    }
}