package com.example.doggywalkerapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doggywalkerapp.databinding.ActivityUserPageBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;


public class UserPageActivity extends DrawerBaseActivity {

    private RecyclerView recyclerView; // recycle view
    private DatabaseReference futureTripDbRef;//firebase reference
    private TripClassAdapter tripClassAdapter; // trip class adapter for recycle view
    private ArrayList<TripClass> futureTripsList; // list of all future trips
    private ActivityUserPageBinding activityUserPageBinding;
    private StorageReference storageReference;
    private SharedPreferences sharedPreferences;
    private TextView messageTxt;


    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUserPageBinding = ActivityUserPageBinding.inflate(getLayoutInflater());
        setContentView(activityUserPageBinding.getRoot());
        allocateActivityTitle("User Page");


        UserClass currentUser = getCurrentUser();
        Log.d("eserUSer", currentUser.toString());

        //message of the future trips
        messageTxt = findViewById(R.id.tripsMessage);

        final ImageView userImg = (ImageView) findViewById(R.id.profileImage);
        userImg.setImageResource(R.drawable.loading_image);


        //retrieve image of the user from firebase storage
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("Uploads/" + currentUser.getUid());
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(UserPageActivity.this)
                        .load(uri)
                        .error(R.drawable.loading_image)
                        .into(userImg);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userImg.setImageResource(R.drawable.empty_profile);

            }
        });

        final TextView personalNameTv = (TextView) findViewById(R.id.personalNameTv);
        final TextView emailTv = (TextView) findViewById(R.id.emailTv);

        personalNameTv.setText(currentUser.getUserName());
        emailTv.setText(currentUser.getEmail());


        // handle all the recycle view things

        //database reference for future trips
        futureTripDbRef = FirebaseDatabase.getInstance().getReference("Users/" + currentUser.getUid() + "/FutureTrips");

        //recycle view
        recyclerView = findViewById(R.id.futureTripList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //create list and connect to the adapter
        futureTripsList = new ArrayList<>(); // create an empty list of dog walkers
        tripClassAdapter = new TripClassAdapter(this, futureTripsList, null);
        recyclerView.setAdapter(tripClassAdapter);


        //check and update the future list of trips
        updateFutureTrips();

        //after updating the future list of trips i show the updated future trips.
        //Note: if the future trips hasn't checked today, it is being checked and then refresh the activity for showing the newest future trips.
    }



    private UserClass getCurrentUser() {
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("User", "");
        UserClass user = gson.fromJson(json, UserClass.class);
        return user;

    }






    private void updateFutureTrips() {
        UserClass currentUser = getCurrentUser();
        String userUid = currentUser.getUid();
        Log.d("uidcurrent",userUid);
        //reference for futureTrips
        DatabaseReference futureList = FirebaseDatabase.getInstance().getReference("Users/" + userUid + "/FutureTrips");
        ArrayList<TripClass> pastTrips = new ArrayList<>();

        futureList.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("heygugu","herererererer");

                Log.d("heyg456ugu","herererererer");

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TripClass tripClass = dataSnapshot.getValue(TripClass.class);
                    Log.d("tripTrip345",tripClass.toString());


                    if (hasDatePassed(tripClass.getTripOccurDate())) {
                        pastTrips.add(tripClass);
                        dataSnapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Log.d("RemovePassedTrip", "Complete");
                            }
                        });
                    } else {
                        futureTripsList.add(tripClass);
                    }
                }
                tripClassAdapter.notifyDataSetChanged();
                Log.d("FutureTrips",futureTripsList.toString());
                Log.d("PastTrips",pastTrips.toString());

                //if there is no future trips
                if (futureTripsList.size() == 0) {
                    messageTxt.setText("No Trips For This Week");
                } else {
                    messageTxt.setText("Trips Of This Week");
                }
                Log.d("pastTrips", pastTrips.toString());
                writePastListToDB(pastTrips);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    //function that returns if the date has passed
    public static boolean hasDatePassed(String dateStr) {
        // Split the date string into day, month, and year
        try {
            String[] parts = dateStr.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            // Create a LocalDate object from the extracted components
            LocalDate date = LocalDate.of(year, month, day);

            // Get today's date
            LocalDate today = LocalDate.now();

            // Check if the date has passed
            return date.isBefore(today);
        } catch (NumberFormatException | DateTimeParseException e) {
            // Invalid date format or invalid date, return false
            return false;
        }
    }

    private void writePastListToDB(ArrayList<TripClass> pastTrips) {
        UserClass currentUser = getCurrentUser();
        String userUid = currentUser.getUid();
        DatabaseReference pastTripsRf = FirebaseDatabase.getInstance().getReference("Users/" + userUid + "/PastTrips");

        for (TripClass trip : pastTrips) {
            String key = trip.getTripId();
            pastTripsRf.child(key).setValue(trip);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        finishAffinity();

    }

}