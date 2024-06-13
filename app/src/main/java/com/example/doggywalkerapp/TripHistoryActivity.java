package com.example.doggywalkerapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doggywalkerapp.databinding.ActivityTripHistoryBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

public class TripHistoryActivity extends DrawerBaseActivity implements RecyclerViewInterface {
    private ActivityTripHistoryBinding activityTripHistoryBinding; //for menu
    private boolean isRated;

    private RecyclerView recyclerView; // recycle view
    private DatabaseReference pastTripsDbRef;//firebase reference
    private TripClassAdapter tripClassAdapter; // trip class adapter for recycle view
    private ArrayList<TripClass> pastTripsList; // list of all past trips
    private Dialog dialog; //dialog for rating
    private TextView yesDialog, noDialog; //text of the rating dialog
    private RatingBar ratingBar;
    private TripClass tripClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTripHistoryBinding = ActivityTripHistoryBinding.inflate(getLayoutInflater());
        setContentView(activityTripHistoryBinding.getRoot());
        allocateActivityTitle("Trip History");


        isRated = false; //The trip has not been rated

        //create dialog
        //set dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_rating_bar_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        yesDialog = (TextView) dialog.findViewById(R.id.yesTxt);
        noDialog = (TextView) dialog.findViewById(R.id.noTxt);
        ratingBar = dialog.findViewById(R.id.ratingBar);


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


        //retrieve the past trips list from firebase
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


        yesDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = ratingBar.getRating();
                //change the rating for the dog walker
                changeRating(rating);

                String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

                //update the  dog walker list at DogWalkersFolder/$weekDay
                for (String weekDay : weekDays) {
                    //In case where the dog walker isn't found on the current day because someone had ordered him, he will no be update with no raising error
                    updateDogWalker(weekDay);
                }
                //update the main dog walker list at DogWalkersFolder/DogWalkers
                updateDogWalker("DogWalkers");
                dialog.dismiss();
                deletePickedWalkerFromDb();
                showToast(tripClass.getDogWalkerName() + " was rated");
                isRated = true;
                startActivity(new Intent(TripHistoryActivity.this, UserPageActivity.class));


            }
        });

        noDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    @Override
    public void onItemClick(int position) {
        tripClass = pastTripsList.get(position);
        if (!isRated) {
            //show the rating dialog only if the user hasn't rated yet
            dialog.show();
        }
    }

    private UserClass getCurrentUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("User", "");
        UserClass user = gson.fromJson(json, UserClass.class);
        return user;

    }

    public void changeRating(float ratingForChange) {

        float newAvgSum = Float.parseFloat(tripClass.getDogWalkerRating()) * Integer.parseInt(tripClass.getWalkerSumRatedTrips()) + ratingForChange;
        tripClass.setWalkerSumRatedTrips(Integer.toString(Integer.parseInt(tripClass.getWalkerSumRatedTrips()) + 1));
        float currentRating = newAvgSum / Float.parseFloat(tripClass.getWalkerSumRatedTrips());
        @SuppressLint("DefaultLocale") String trimmedNumber = String.format("%.1f", currentRating);
        tripClass.setDogWalkerRating(trimmedNumber);
    }

    public void updateDogWalker(String path) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DogWalkersFolder/" + path + tripClass.getWalkerId());
        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("DogWalkersFolder/" + path);

                ///bug here!!!!!!
                //down casting doesn't work....

                //bug here (now there is no bug)
                DogWalkerClass dogWalker = new DogWalkerClass(tripClass.getDogWalkerName(), tripClass.getDogWalkerPhoneNumber(), tripClass.getDogWalkerRating(), tripClass.getDogWalkerLocation(), tripClass.getWalkerId(), tripClass.getWalkerSumRatedTrips());
                Log.d("22222222222", Boolean.toString(tripClass instanceof DogWalkerClass));
                Log.d("ani333", dogWalker.toString());

                databaseReference2.child(dogWalker.getWalkerId()).setValue(dogWalker);
            }
        });

    }

    private void deletePickedWalkerFromDb() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + getCurrentUser().getUid() + "/PastTrips/" + tripClass.getTripId());
        databaseReference.removeValue();

    }

    private void showToast(String text) {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) View my_toast = inflater.inflate(R.layout.my_toast, findViewById(R.id.my_toast));
        TextView tv = my_toast.findViewById(R.id.tv_my_toast);
        tv.setText(text);
        Toast toast = new Toast(TripHistoryActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(my_toast);
        toast.show();
    }


}