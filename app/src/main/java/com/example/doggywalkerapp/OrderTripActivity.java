package com.example.doggywalkerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doggywalkerapp.databinding.ActivityOrderTripBinding;

import java.text.SimpleDateFormat;
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


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOrderTripBinding = ActivityOrderTripBinding.inflate(getLayoutInflater());
        setContentView(activityOrderTripBinding.getRoot());
        allocateActivityTitle("Order a Trip");

        currentDay = new SimpleDateFormat("EEEE").format(new Date());

        orderBt = (Button) findViewById(R.id.orderBt); //order button

        //drop list
        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, days);
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                pickedDay = adapterView.getItemAtPosition(i).toString();
            }
        });

        orderBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickedDay.equals("")) {
                    showToast("Day wasn't picked");
                } else {
                    if (!isValidDay(currentDay, pickedDay, days)) {
                        showToast(pickedDay + " has already passed");
                    } else {
                        Intent intent = new Intent(OrderTripActivity.this, DogWalkersPerDayActivity.class);
                        intent.putExtra("DAY_KEY", pickedDay);
                        startActivity(intent);
                        finish();
                    }

                }
            }
        });
    }

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

    private boolean isValidDay(String currentDay, String pickedDay, String[] days) {

        List<String> list = Arrays.asList(days);

        int pickedDayIndex = list.indexOf(pickedDay); // index of picked day in the array
        int currentDayIndex = list.indexOf(currentDay); // index of current day in the array

        return pickedDayIndex >= currentDayIndex;
    }
}