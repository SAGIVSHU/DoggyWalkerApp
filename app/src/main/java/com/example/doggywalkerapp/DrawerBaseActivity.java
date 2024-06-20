package com.example.doggywalkerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private FrameLayout container;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;


    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base, null);
        container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        toolbar = drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        firebaseAuth = FirebaseAuth.getInstance();


        navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        if (item.getItemId() == R.id.nav_userPage) {
            startActivity(new Intent(this, UserPageActivity.class));
            overridePendingTransition(0, 0);
        }
        else if (item.getItemId() == R.id.nav_tripOrder) {
            startActivity(new Intent(this, OrderTripActivity.class));
            overridePendingTransition(0, 0);
        }
        else if(item.getItemId() == R.id.nav_tripHistory){
            startActivity(new Intent(this, TripHistoryActivity.class));
            overridePendingTransition(0, 0);
        }
        else{
            clearDataFromSharedPreferences("User");
            firebaseAuth.signOut();
            startActivity(new Intent(this, WelcomeActivity.class));
            overridePendingTransition(0, 0);

        }

        return false;
    }

    protected void allocateActivityTitle(String titleString) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleString);

        }
    }

    private void clearDataFromSharedPreferences(String key){

        SharedPreferences preferences = getSharedPreferences(key, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

    }
}