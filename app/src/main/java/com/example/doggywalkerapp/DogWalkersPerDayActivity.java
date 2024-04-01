package com.example.doggywalkerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doggywalkerapp.databinding.ActivityDogWalkersPerDayBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DogWalkersPerDayActivity extends DrawerBaseActivity  implements RecyclerViewInterface{

    private ActivityDogWalkersPerDayBinding activityDogWalkersPerDayBinding;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private DogWalkerAdapter adapter;
    private ArrayList<DogWalkerClass> list;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDogWalkersPerDayBinding = ActivityDogWalkersPerDayBinding.inflate(getLayoutInflater());
        setContentView(activityDogWalkersPerDayBinding.getRoot());
        allocateActivityTitle("Order a Trip");



        Intent intent = getIntent();
        String day = intent.getStringExtra("DAY_KEY"); //get the day from the previous intent

        //RecycleView Part
        recyclerView = findViewById(R.id.dogWalkersList);
        databaseReference = FirebaseDatabase.getInstance().getReference("DogWalkersFolder/" + day); // get the data from the day
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new DogWalkerAdapter(this,list,this);
        recyclerView.setAdapter(adapter);


        //Get data from database to list
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                   DogWalkerClass dogWalker = dataSnapshot.getValue(DogWalkerClass.class);
                   list.add(dogWalker);
               }
               adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        showToast(position+" was clicked");
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