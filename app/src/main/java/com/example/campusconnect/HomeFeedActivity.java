package com.example.campusconnect;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFeedActivity extends AppCompatActivity {

    private LinearLayout btnCreatePost;
    private LinearLayout postsContainer;

    private TextView btnChat;
    private TextView btnCreate;
    private TextView btnNotices;
    private TextView btnProfile;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feed);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Find Views
        btnCreatePost = findViewById(R.id.btnCreatePost);
        postsContainer = findViewById(R.id.postsContainer);

        btnChat = findViewById(R.id.btnChat);
        btnCreate = findViewById(R.id.btnCreate);
        btnNotices = findViewById(R.id.btnNotices);
        btnProfile = findViewById(R.id.btnProfile);

        // Create Post
        btnCreatePost.setOnClickListener(v -> openCreatePost());

        // Bottom Create
        btnCreate.setOnClickListener(v -> openCreatePost());

        // Chat
        btnChat.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    ChatListActivity.class
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

        // Load posts
        loadPosts();
    }

    // =====================================================
    // Open Create Post
    // =====================================================

    private void openCreatePost() {

        Intent intent = new Intent(
                HomeFeedActivity.this,
                CreatePostActivity.class
        );

        startActivity(intent);
    }

    // =====================================================
    // Load Posts From Firestore
    // =====================================================

    private void loadPosts() {

        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        firestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    // Remove previously loaded Firebase posts
                    postsContainer.removeAllViews();

                    if (queryDocumentSnapshots.isEmpty()) {

                        TextView emptyText = new TextView(
                                HomeFeedActivity.this
                        );

                        emptyText.setText(
                                "No posts yet.\nBe the first to create a post!"
                        );

                        emptyText.setTextSize(16);
                        emptyText.setTextColor(Color.GRAY);
                        emptyText.setGravity(Gravity.CENTER);
                        emptyText.setPadding(20, 50, 20, 50);

                        postsContainer.addView(emptyText);

                        return;
                    }

                    // Add Firebase posts
                    for (DocumentSnapshot document :
                            queryDocumentSnapshots.getDocuments()) {

                        String postText =
                                document.getString("postText");

                        String email =
                                document.getString("email");

                        Long timestamp =
                                document.getLong("timestamp");

                        addPostToFeed(
                                postText,
                                email,
                                timestamp
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            HomeFeedActivity.this,
                            "Failed to load posts: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =====================================================
    // Create Post UI
    // =====================================================

    private void addPostToFeed(
            String postText,
            String email,
            Long timestamp
    ) {

        // Main Post Layout
        LinearLayout postLayout =
                new LinearLayout(this);

        postLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        postLayout.setPadding(
                15,
                15,
                15,
                15
        );

        postLayout.setBackgroundColor(
                Color.WHITE
        );

        LinearLayout.LayoutParams postParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        postParams.setMargins(
                0,
                0,
                0,
                10
        );

        postLayout.setLayoutParams(postParams);

        // =================================================
        // User Section
        // =================================================

        LinearLayout userLayout =
                new LinearLayout(this);

        userLayout.setOrientation(
                LinearLayout.HORIZONTAL
        );

        userLayout.setGravity(
                Gravity.CENTER_VERTICAL
        );

        // User icon
        TextView userIcon =
                new TextView(this);

        userIcon.setText("👤");
        userIcon.setTextSize(25);
        userIcon.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        45,
                        45
                );

        userIcon.setLayoutParams(iconParams);

        userLayout.addView(userIcon);

        // User info
        LinearLayout userInfo =
                new LinearLayout(this);

        userInfo.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        infoParams.setMargins(
                10,
                0,
                0,
                0
        );

        userInfo.setLayoutParams(infoParams);

        // Email
        TextView userName =
                new TextView(this);

        if (email != null && !email.isEmpty()) {
            userName.setText(email);
        } else {
            userName.setText("CampusConnect User");
        }

        userName.setTextSize(17);
        userName.setTextColor(Color.BLACK);
        userName.setTypeface(
                null,
                Typeface.BOLD
        );

        userInfo.addView(userName);

        // Time
        TextView postTime =
                new TextView(this);

        postTime.setText(
                getTimeText(timestamp)
        );

        postTime.setTextSize(13);
        postTime.setTextColor(Color.GRAY);

        userInfo.addView(postTime);

        userLayout.addView(userInfo);

        postLayout.addView(userLayout);

        // =================================================
        // Post Text
        // =================================================

        TextView postTextView =
                new TextView(this);

        postTextView.setText(
                postText != null ? postText : ""
        );

        postTextView.setTextSize(16);
        postTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        textParams.setMargins(
                0,
                12,
                0,
                12
        );

        postTextView.setLayoutParams(textParams);

        postLayout.addView(postTextView);

        // =================================================
        // Divider
        // =================================================

        TextView divider =
                new TextView(this);

        divider.setBackgroundColor(
                Color.parseColor("#EEEEEE")
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );

        divider.setLayoutParams(dividerParams);

        postLayout.addView(divider);

        // =================================================
        // Action Buttons
        // =================================================

        LinearLayout actionLayout =
                new LinearLayout(this);

        actionLayout.setOrientation(
                LinearLayout.HORIZONTAL
        );

        actionLayout.setGravity(
                Gravity.CENTER
        );

        // Like
        TextView likeButton =
                new TextView(this);

        likeButton.setText("♡ Like");
        likeButton.setTextSize(14);
        likeButton.setTextColor(
                getResources().getColor(
                        R.color.app_name
                )
        );
        likeButton.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams likeParams =
                new LinearLayout.LayoutParams(
                        0,
                        50,
                        1
                );

        likeButton.setLayoutParams(
                likeParams
        );

        // Comment
        TextView commentButton =
                new TextView(this);

        commentButton.setText("💬 Comment");
        commentButton.setTextSize(14);
        commentButton.setTextColor(
                getResources().getColor(
                        R.color.app_name
                )
        );
        commentButton.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams commentParams =
                new LinearLayout.LayoutParams(
                        0,
                        50,
                        1
                );

        commentButton.setLayoutParams(
                commentParams
        );

        // Share
        TextView shareButton =
                new TextView(this);

        shareButton.setText("↗ Share");
        shareButton.setTextSize(14);
        shareButton.setTextColor(
                getResources().getColor(
                        R.color.app_name
                )
        );
        shareButton.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams shareParams =
                new LinearLayout.LayoutParams(
                        0,
                        50,
                        1
                );

        shareButton.setLayoutParams(
                shareParams
        );

        actionLayout.addView(
                likeButton
        );

        actionLayout.addView(
                commentButton
        );

        actionLayout.addView(
                shareButton
        );

        postLayout.addView(
                actionLayout
        );

        // Add post to container
        postsContainer.addView(
                postLayout
        );
    }

    // =====================================================
    // Format Time
    // =====================================================

    private String getTimeText(Long timestamp) {

        if (timestamp == null) {
            return "Just now";
        }

        Date date =
                new Date(timestamp);

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a",
                        Locale.getDefault()
                );

        return format.format(date);
    }

    // =====================================================
    // Reload When Returning To Home
    // =====================================================

    @Override
    protected void onResume() {
        super.onResume();

        if (firebaseAuth != null &&
                firebaseAuth.getCurrentUser() != null) {

            loadPosts();
        }
    }
}