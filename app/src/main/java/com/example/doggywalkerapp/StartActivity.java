package com.example.doggywalkerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class StartActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        UserClass currentUser = getCurrentUser();


        videoView = (VideoView) findViewById(R.id.video);
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.startanimation);
        videoView.start();


        CountDownTimer countDownTimer = new CountDownTimer(3500, 100) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent go;
                if (currentUser == null) {
                    go = new Intent(StartActivity.this, WelcomeActivity.class);

                } else {
                    go = new Intent(StartActivity.this, UserPageActivity.class);
                }
                startActivity(go);
                finish();

            }
        };
        countDownTimer.start();
    }

    private UserClass getCurrentUser() {
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("User", "");
        if (json.equals("")) {
            return null;
        }
        UserClass user = gson.fromJson(json, UserClass.class);
        return user;

    }


}