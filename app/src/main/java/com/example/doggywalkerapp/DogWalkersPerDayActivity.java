package com.example.doggywalkerapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Objects;

public class DogWalkersPerDayActivity extends DrawerBaseActivity implements RecyclerViewInterface {
    private ActivityDogWalkersPerDayBinding activityDogWalkersPerDayBinding;
    private RecyclerView recyclerView;
    private DogWalkerAdapter adapter;

    private DatabaseReference pickedDayDatabaseReference;
    private ArrayList<DogWalkerClass> dogWalkersList; //arraylist of the dog walkers for the picked day
    private Dialog dialog;
    private TextView yesDialog, noDialog, mainTextDialog;
    private DogWalkerClass pickedWalker;
    private String pickedDay;
    private UserClass personWhoOrdered;
    private DatabaseReference futureTripDbRef;
    private TripClass currentTrip;
    private ProgressDialog progressDialog;

    private boolean isOrdered; // a flag for knowing if the current user already ordered a trip in this activity;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //menu part
        activityDogWalkersPerDayBinding = ActivityDogWalkersPerDayBinding.inflate(getLayoutInflater());
        setContentView(activityDogWalkersPerDayBinding.getRoot());
        allocateActivityTitle("Order a Trip");

        isOrdered = false; // the user didn't order yet 

        personWhoOrdered = getCurrentUser(); // get current user by shared presences
        //data base for write into future trips folder 
        futureTripDbRef = FirebaseDatabase.getInstance().getReference("Users/" + personWhoOrdered.getUid() + "/FutureTrips");


        //Create progressDialog
        progressDialog = new ProgressDialog(this);


        //get the day from the previous intent
        Intent intent = getIntent();
        pickedDay = intent.getStringExtra("DAY_KEY");


        //RecycleView Part

        recyclerView = findViewById(R.id.dogWalkersList);
        // get the data from the picked day
        pickedDayDatabaseReference = FirebaseDatabase.getInstance().getReference("DogWalkersFolder/" + pickedDay);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create an empty list of dog walkers and connect the adapter to the recycle view
        dogWalkersList = new ArrayList<>();
        adapter = new DogWalkerAdapter(this, dogWalkersList, this);
        recyclerView.setAdapter(adapter);


        //Get all dog walkers from the picked day in firebase to list
        pickedDayDatabaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DogWalkerClass dogWalker = dataSnapshot.getValue(DogWalkerClass.class);
                    dogWalkersList.add(dogWalker);
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


        //build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(DogWalkersPerDayActivity.this);
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
            finish();
            startActivity(getIntent());
            dialog.dismiss();
        });


        //Create the dialog
        AlertDialog alertDialog = builder.create();
        yesDialog.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UnsafeIntentLaunch")
            @Override
            public void onClick(View v) {
                if (pickedDay != null && pickedWalker != null) {
                    if (!isOrdered) {

                        progressDialog.setMessage("Checking Data...");
                        progressDialog.show();
                        //create the class that handle couples of orders at the same time
                        HandleOrderTripClass handleOrderTripClass = new HandleOrderTripClass();



                        if (handleOrderTripClass.bookTrip("DogWalkersFolder/" + pickedDay,pickedWalker, pickedDay, personWhoOrdered.getUid())) {
                            showToast(pickedWalker.getDogWalkerName() + " was ordered for " + pickedDay);
                            startActivity(new Intent(DogWalkersPerDayActivity.this, UserPageActivity.class)); // finally new intent

                        } else {
                            alertDialog.setMessage(pickedWalker.getDogWalkerName() + " is taken");
                            alertDialog.show();
                            //refresh the activity
                            Log.d("RefreshSagiv", "Gugu");

                        }
                        dialog.dismiss();

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

    //recycle view on click function (implementation of the interface)
    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(int position) {
        mainTextDialog.setText("Do you want to order a trip for " + pickedDay);
        pickedWalker = dogWalkersList.get(position);
        dialog.show();
    }

    //save a trip function
    private void saveTripToDB() {
        String tripId = futureTripDbRef.push().getKey();
        if (currentTrip != null && tripId != null) {

            currentTrip.setTripId(tripId);
            futureTripDbRef.child(tripId).setValue(currentTrip).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // set the flag for true and now the user cant order again for that activity
                    isOrdered = true;
                    // Data successfully saved
                    Log.d(TAG, "Trip saved successfully!");
                }
            }).addOnFailureListener(new OnFailureListener() {
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

    // delete dog walker after ordering function
    private void deleteDogWalkerById() {
        if (pickedWalker != null) {
            String dogWalkerId = pickedWalker.getWalkerId();
            DatabaseReference deleteDogWalker = FirebaseDatabase.getInstance().getReference("DogWalkersFolder/" + pickedDay + "/" + dogWalkerId);
            deleteDogWalker.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("deleted", "yes the dog walker was deleted");
                }
            });


        }
    }

    //show toast function
    private void showToast(String text) {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) View my_toast = inflater.inflate(R.layout.my_toast, findViewById(R.id.my_toast));
        TextView tv = my_toast.findViewById(R.id.tv_my_toast);
        tv.setText(text);
        Toast toast = new Toast(DogWalkersPerDayActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(my_toast);
        toast.show();
    }
}