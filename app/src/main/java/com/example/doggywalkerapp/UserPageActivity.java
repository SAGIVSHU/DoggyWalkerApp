package com.example.doggywalkerapp;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.doggywalkerapp.databinding.ActivityUserPageBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;


public class UserPageActivity extends DrawerBaseActivity {

    ActivityUserPageBinding activityUserPageBinding;
    private StorageReference storageReference;
    private SharedPreferences sharedPreferences;

    private UserClass getCurrentUser() {
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("User", "");
        UserClass user = gson.fromJson(json, UserClass.class);
        return user;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUserPageBinding = ActivityUserPageBinding.inflate(getLayoutInflater());
        setContentView(activityUserPageBinding.getRoot());
        allocateActivityTitle("User Page");


        UserClass currentUser = getCurrentUser();
        String extension = currentUser.getExtension();
        Log.d("eserUSer", currentUser.toString());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                UserPageActivity.this.finish();
                System.exit(0);
            }
        });

        final ImageView userImg = (ImageView) findViewById(R.id.profileImage);
        userImg.setImageResource(R.drawable.loading_image);

        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imegRef = storageReference.child("Uploads/" + currentUser.getUid() + "." + extension);
        imegRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                Log.d("erhhjhjror2", e.toString());
                userImg.setImageResource(R.drawable.empty_profile);

            }
        });

        final TextView personalNameTv = (TextView) findViewById(R.id.personalNameTv);
        final TextView emailTv = (TextView) findViewById(R.id.emailTv);

        personalNameTv.setText(currentUser.getUserName());
        emailTv.setText(currentUser.getEmail());


    }
}