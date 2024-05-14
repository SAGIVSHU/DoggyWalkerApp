package com.example.doggywalkerapp;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HandleOrderTripClass {

    private TripClass orderedTrip;
    private DogWalkerClass pickedWalker;
    private ArrayList<DogWalkerClass> dogWalkersList;


    //first build a trip
    public HandleOrderTripClass() {


    }

    //if the function returns false the page will be needed to be refreshed and in this way everything will start from the start
    public void bookTrip(String pathOfPickedDayForValidation, DogWalkerClass pickedWalker, String tripOccurDay, String personWhoOrderedUid, BookingCallback callback) {

        this.pickedWalker = pickedWalker;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String currentDate = formatter.format(date);

        //create a trip class
        @SuppressLint("SimpleDateFormat") String currentDay = new SimpleDateFormat("EEEE").format(new Date()); //day at the moment
        orderedTrip = new TripClass(pickedWalker, currentDay, currentDate, tripOccurDay, personWhoOrderedUid);

        //reference for checking if the list of the dog walkers has changed
        DatabaseReference pathOfPickedDay = FirebaseDatabase.getInstance().getReference(pathOfPickedDayForValidation);


        //array list for the most updated list
        ArrayList<DogWalkerClass> dogWalkersList = new ArrayList<>();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DogWalkerClass dogWalker = dataSnapshot.getValue(DogWalkerClass.class);
                    dogWalkersList.add(dogWalker);
                }
                if (!dogWalkersList.isEmpty() && !IsDogWalkerExist(pickedWalker, dogWalkersList)) {
                    callback.onBookingFailure();
                } else {
                    saveTripToDB();
                    deleteDogWalkerFromThePickedDay();
                    callback.onBookingSuccess();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

//        pathOfPickedDay.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    DogWalkerClass dogWalker = dataSnapshot.getValue(DogWalkerClass.class);
//                    dogWalkersList.add(dogWalker);
//                }
//                onDataChangeFinished = true;
//                IsDogWalkerExist(pickedWalker, dogWalkersList);
//                Log.d("sagivgugu", dogWalkersList.toString());
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                onDataChangeFinished = true;
//            }
//        });

        pathOfPickedDay.addListenerForSingleValueEvent(eventListener);
//        while (!onDataChangeFinished.get()) {
//            Log.d("in the while","while");
//        }

//        if (!dogWalkersList.isEmpty() && !IsDogWalkerExist(pickedWalker, dogWalkersList)) {
//            Log.d("Here in the if", "yes");
//            //The dog walker is not exist and the page is needed to be refreshed
//            return false;
//        } else {
//            Log.d("in the else", Boolean.toString(dogWalkersList.isEmpty()) + Boolean.toString(IsDogWalkerExist(pickedWalker, dogWalkersList)));
//
//        }
//
//        saveTripToDB();
//        deleteDogWalkerFromThePickedDay();
//        return true;

    }

    //function for checking if the picked dog walker exists in the updated list
    private boolean IsDogWalkerExist(DogWalkerClass pickedDogWalker, ArrayList<DogWalkerClass> dogWalkersList) {

        Log.e("list1", dogWalkersList.toString());
        Log.d("uidafbdfrw3", Integer.toString(dogWalkersList.size()));

        for (int position = 0; position < dogWalkersList.size(); position++) {

            Log.e("in the loop", "innn");
            String pickedWalkerId = dogWalkersList.get(position).getWalkerId();
            String idOfpickedWalker = pickedDogWalker.getWalkerId();
            Log.e("id of both", pickedWalkerId + "  " + idOfpickedWalker);
            if (pickedWalkerId.equals(idOfpickedWalker)) {
                Log.e("Dog walker is Exists1", "Exists");
                return true;
            }
        }
        Log.e("Dog walker not Exists1", "not Exists");

        return false;
    }


    private void saveTripToDB() {

        //reference to firebase
        DatabaseReference futureTripDbRef = FirebaseDatabase.getInstance().getReference("Users/" + this.orderedTrip.getPersonWhoOrderedUid() + "/FutureTrips");
        String tripId = futureTripDbRef.push().getKey();
        this.orderedTrip.setTripId(tripId);

        if (tripId != null) {
            futureTripDbRef.child(tripId).setValue(this.orderedTrip).addOnSuccessListener(new OnSuccessListener<Void>() {

                @Override
                public void onSuccess(Void unused) {
                    Log.e("SuccessSaving", "save trip");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("failed on saving", "failed saving");
                    throw new RuntimeException(e.toString());

                }
            });
        }
    }

    private void deleteDogWalkerFromThePickedDay() {

        String dogWalkerId = this.orderedTrip.getWalkerId();
        DatabaseReference deleteDogWalkerReference = FirebaseDatabase.getInstance().getReference("DogWalkersFolder/" + this.orderedTrip.getTripOccurDay() + "/" + dogWalkerId);
        Log.d("pathDB", "DogWalkersFolder/" + this.orderedTrip.getTripOccurDay() + "/" + dogWalkerId);
        deleteDogWalkerReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.e("SuccessDelete", "Deleteddogwalker");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("failed on delete", "failed");
                throw new RuntimeException(e.toString());
            }
        });

    }


}
