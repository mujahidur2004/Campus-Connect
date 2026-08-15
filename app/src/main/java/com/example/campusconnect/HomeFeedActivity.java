package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeFeedActivity extends AppCompatActivity {

    // Create Post
    private LinearLayout btnCreatePost;

    // First Post
    private LinearLayout postOne;

    // Bottom Navigation
    private TextView btnChat;
    private TextView btnCreate;
    private TextView btnNotices;
    private TextView btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);

        // =========================
        // Find Views
        // =========================

        btnCreatePost = findViewById(R.id.btnCreatePost);
        postOne = findViewById(R.id.postOne);

        btnChat = findViewById(R.id.btnChat);
        btnCreate = findViewById(R.id.btnCreate);
        btnNotices = findViewById(R.id.btnNotices);
        btnProfile = findViewById(R.id.btnProfile);


        // =========================
        // Create Post
        // "What's on your mind?"
        // =========================

        btnCreatePost.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    CreatePostActivity.class
            );

            startActivity(intent);
        });


        // =========================
        // Create Post
        // Bottom + Button
        // =========================

        btnCreate.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    CreatePostActivity.class
            );

            startActivity(intent);
        });


        // =========================
        // Post Details
        // First Post
        // =========================

        postOne.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    PostDetailsActivity.class
            );

            startActivity(intent);
        });


        // =========================
        // Chat
        // =========================

        btnChat.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    ChatListActivity.class
            );

            startActivity(intent);
        });


        // =========================
        // Notices
        // =========================

        btnNotices.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    NoticeActivity.class
            );

            startActivity(intent);
        });


        // =========================
        // Profile
        // =========================

        btnProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    ProfileActivity.class
            );

            startActivity(intent);
        });

    }
}