package com.example.doggywalkerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button rgButton = (Button) findViewById(R.id.registerBt);
        Button lgButton = (Button) findViewById(R.id.loginBt);

        rgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(go);
            }
        });

        lgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(go);
            }
        });

    }
}