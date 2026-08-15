package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeFeedActivity extends AppCompatActivity {

    private LinearLayout btnCreatePost;

    private TextView btnEvents;
    private TextView btnNotices;
    private TextView btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);

        btnCreatePost = findViewById(R.id.btnCreatePost);

        btnEvents = findViewById(R.id.btnEvents);
        btnNotices = findViewById(R.id.btnNotices);
        btnProfile = findViewById(R.id.btnProfile);

        // Create Post
        btnCreatePost.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    CreatePostActivity.class
            );

            startActivity(intent);
        });

        // Events
        btnEvents.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    EventActivity.class
            );

            startActivity(intent);
        });

        // Notices
        btnNotices.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    NoticeActivity.class
            );

            startActivity(intent);
        });

        // Profile
        btnProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    ProfileActivity.class
            );

            startActivity(intent);
        });
    }
}