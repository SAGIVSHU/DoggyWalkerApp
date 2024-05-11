package com.example.doggywalkerapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

        // get user that is using now
        currentUser = getCurrentUser();
        
        //set the arrayList of the current user future trips
        userFutureTripsList = new ArrayList<>(); 

        //reference of the user
        futureTripDbRef = FirebaseDatabase.getInstance().getReference("Users/" + currentUser.getUid() + "/FutureTrips"); // set reference for data base

        //day at the moment
        currentDay = new SimpleDateFormat("EEEE").format(new Date());

        //order button
        orderBt = (Button) findViewById(R.id.orderBt); 

        //drop list of the choices
        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(this, R.layout.days_drop_list_item, days);
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

                //build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderTripActivity.this);
                builder.setCancelable(false);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.dismiss();
                });


                //Create the dialog
                AlertDialog alertDialog = builder.create();

                if (pickedDay.equals("")) {
                    alertDialog.setMessage("Day wasn't picked");
                    alertDialog.show();
                } else {
                    if (!isValidDay(currentDay, pickedDay, days)) {
                        alertDialog.setMessage(pickedDay + " has already passed");
                        alertDialog.show();
                    } else {
                        if (!IsTripExistInFutureTrips()) {
                            Intent intent = new Intent(OrderTripActivity.this, DogWalkersPerDayActivity.class);
                            intent.putExtra("DAY_KEY", pickedDay);
                            startActivity(intent);
                            finish();
                        } else {
                            alertDialog.setMessage("You already have a trip for " + pickedDay);
                            alertDialog.show();
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
        return gson.fromJson(json, UserClass.class);

    }


    //function for knowing if there is already trip that occurs at the picked day
    private boolean IsTripExistInFutureTrips() {
        for (TripClass trip : userFutureTripsList) {
            if (userFutureTripsList != null && trip.getTripOccurDay().equals(pickedDay)) {
                return true;
            }
        }
        return false;
    }
}