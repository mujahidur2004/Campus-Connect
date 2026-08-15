package com.example.campusconnect;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EventActivity extends AppCompatActivity {

    private Button btnInterestedSports;
    private Button btnInterestedContest;
    private Button btnInterestedFest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        btnInterestedSports =
                findViewById(R.id.btnInterestedSports);

        btnInterestedContest =
                findViewById(R.id.btnInterestedContest);

        btnInterestedFest =
                findViewById(R.id.btnInterestedFest);

        btnInterestedSports.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "You are interested in Annual Sports",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnInterestedContest.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "You are interested in Programming Contest",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnInterestedFest.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "You are interested in CSE Fest",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }
}