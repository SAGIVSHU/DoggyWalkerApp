package com.example.doggywalkerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.doggywalkerapp.databinding.ActivityOrderTripBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class OrderTripActivity extends DrawerBaseActivity {

    private ActivityOrderTripBinding activityOrderTripBinding;
    private String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"}; // array of week days
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItems;
    private String pickedDay = ""; // picked day
    private Button orderBt;
    private String currentDay;
    private DatabaseReference futureTripDbRef;
    private UserClass currentUser;
    private ArrayList<TripClass> userFutureTripsList;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOrderTripBinding = ActivityOrderTripBinding.inflate(getLayoutInflater());
        setContentView(activityOrderTripBinding.getRoot());
        allocateActivityTitle("Order a Trip");

        currentUser = getCurrentUser(); // get user that is using know
        userFutureTripsList = new ArrayList<>(); //set the arrayList

        futureTripDbRef = FirebaseDatabase.getInstance().getReference("Users/" + currentUser.getUid() + "/FutureTrips"); // set reference for data base

        currentDay = new SimpleDateFormat("EEEE").format(new Date()); //day at the moment

        orderBt = (Button) findViewById(R.id.orderBt); //order button

        //drop list of the choices
        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, days);
        autoCompleteTextView.setAdapter(adapterItems);


        //get all the data from future trip and put it in the list of trips for the current user
        futureTripDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TripClass trip = dataSnapshot.getValue(TripClass.class);
                    userFutureTripsList.add(trip);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //get the picked day of the user
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                pickedDay = adapterView.getItemAtPosition(i).toString();
            }
        });

        //handle order button click
        orderBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickedDay.equals("")) {
                    showToast("Day wasn't picked");
                } else {
                    if (!isValidDay(currentDay, pickedDay, days)) {
                        showToast(pickedDay + " has already passed");
                    } else {
                        if (!IsTripExist()) {
                            Intent intent = new Intent(OrderTripActivity.this, DogWalkersPerDayActivity.class);
                            intent.putExtra("DAY_KEY", pickedDay);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("You already have a trip for " + pickedDay);
                        }
                    }

                }
            }
        });
    }


    //function that show my custom toast
    private void showToast(String text) {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) View my_toast = inflater.inflate(R.layout.my_toast, findViewById(R.id.my_toast));
        TextView tv = my_toast.findViewById(R.id.tv_my_toast);
        tv.setText(text);
        Toast toast = new Toast(OrderTripActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(my_toast);
        toast.show();
    }


    //function that return if the day hasn't passed yet
    private boolean isValidDay(String currentDay, String pickedDay, String[] days) {

        List<String> list = Arrays.asList(days);

        int pickedDayIndex = list.indexOf(pickedDay); // index of picked day in the array
        int currentDayIndex = list.indexOf(currentDay); // index of current day in the array

        return pickedDayIndex >= currentDayIndex;
    }


    //function that returns the current user from the shared preferences
    private UserClass getCurrentUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("User", "");
        UserClass user = gson.fromJson(json, UserClass.class);
        return user;

    }


    //function for knowing if there is already trip that occurs at the picked day
    private boolean IsTripExist() {
        for (TripClass trip : userFutureTripsList) {
            if (userFutureTripsList != null && trip.getTripOccurDay().equals(pickedDay)) {
                return true;
            }
        }
        return false;
    }
}