package com.example.campusconnect;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeFeedActivity extends AppCompatActivity {

    // =====================================================
    // FIREBASE
    // =====================================================

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    // =====================================================
    // UI
    // =====================================================

    private LinearLayout postsContainer;
    private LinearLayout btnCreatePost;

    private TextView btnNotification;

    private Button btnHome;
    private Button btnChat;
    private Button btnNotices;
    private Button btnEvents;
    private Button btnProfile;

    // =====================================================
    // CURRENT USER
    // =====================================================

    private String currentUserId;

    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_feed);

        // Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Check login
        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        currentUserId = auth.getCurrentUser().getUid();

        // =================================================
        // FIND VIEWS
        // =================================================

        postsContainer =
                findViewById(R.id.postsContainer);

        btnNotification =
                findViewById(R.id.btnNotification);

        btnCreatePost =
                findViewById(R.id.btnCreatePost);

        btnHome =
                findViewById(R.id.btnHome);

        btnChat =
                findViewById(R.id.btnChat);

        btnNotices =
                findViewById(R.id.btnNotices);

        btnEvents =
                findViewById(R.id.btnEvents);

        btnProfile =
                findViewById(R.id.btnProfile);

        // =================================================
        // NOTIFICATION
        // =================================================

        btnNotification.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    NotificationActivity.class
            );

            startActivity(intent);
        });

        // =================================================
        // CREATE POST
        // =================================================

        btnCreatePost.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    CreatePostActivity.class
            );

            startActivity(intent);
        });

        // =================================================
        // HOME
        // =================================================

        btnHome.setOnClickListener(v -> {

            loadPosts();
        });

        // =================================================
        // CHAT
        // =================================================

        btnChat.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    ChatListActivity.class
            );

            startActivity(intent);
        });

        // =================================================
        // NOTICES
        // =================================================

        btnNotices.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    NoticeActivity.class
            );

            startActivity(intent);
        });

        // =================================================
        // EVENTS
        // =================================================

        btnEvents.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    EventActivity.class
            );

            startActivity(intent);
        });

        // =================================================
        // PROFILE
        // =================================================

        btnProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeFeedActivity.this,
                    ProfileActivity.class
            );

            startActivity(intent);
        });

        // =================================================
        // LOAD POSTS
        // =================================================

        loadPosts();
    }

    // =====================================================
    // ON RESUME
    // =====================================================

    @Override
    protected void onResume() {
        super.onResume();

        if (auth != null &&
                auth.getCurrentUser() != null) {

            loadPosts();
        }
    }

    // =====================================================
    // LOAD POSTS
    // =====================================================

    private void loadPosts() {

        if (postsContainer == null) {
            return;
        }

        postsContainer.removeAllViews();

        firestore.collection("posts")
                .orderBy(
                        "timestamp",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {

                        showNoPosts();
                        return;
                    }

                    queryDocumentSnapshots.forEach(
                            documentSnapshot -> {

                                String postId =
                                        documentSnapshot.getId();

                                String userId =
                                        documentSnapshot.getString(
                                                "userId"
                                        );

                                String postText =
                                        documentSnapshot.getString(
                                                "postText"
                                        );

                                Long likeCount =
                                        documentSnapshot.getLong(
                                                "likeCount"
                                        );

                                Long commentCount =
                                        documentSnapshot.getLong(
                                                "commentCount"
                                        );

                                if (postText == null) {
                                    postText = "";
                                }

                                if (likeCount == null) {
                                    likeCount = 0L;
                                }

                                if (commentCount == null) {
                                    commentCount = 0L;
                                }

                                loadPostUser(
                                        postId,
                                        userId,
                                        postText,
                                        likeCount,
                                        commentCount
                                );
                            }
                    );
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
    // LOAD USER INFORMATION
    // =====================================================

    private void loadPostUser(
            String postId,
            String userId,
            String postText,
            Long likeCount,
            Long commentCount
    ) {

        if (userId == null || userId.isEmpty()) {

            addPostCard(
                    postId,
                    "CampusConnect Student",
                    "Student",
                    "",
                    postText,
                    likeCount,
                    commentCount
            );

            return;
        }

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    String name =
                            documentSnapshot.getString("name");

                    String department =
                            documentSnapshot.getString(
                                    "department"
                            );

                    String session =
                            documentSnapshot.getString(
                                    "session"
                            );

                    if (name == null ||
                            name.isEmpty()) {

                        name =
                                "CampusConnect Student";
                    }

                    if (department == null ||
                            department.isEmpty()) {

                        department = "Student";
                    }

                    if (session == null) {
                        session = "";
                    }

                    addPostCard(
                            postId,
                            name,
                            department,
                            session,
                            postText,
                            likeCount,
                            commentCount
                    );
                })
                .addOnFailureListener(e -> {

                    addPostCard(
                            postId,
                            "CampusConnect Student",
                            "Student",
                            "",
                            postText,
                            likeCount,
                            commentCount
                    );
                });
    }

    // =====================================================
    // NO POSTS
    // =====================================================

    private void showNoPosts() {

        TextView textView =
                new TextView(this);

        textView.setText(
                "No posts available yet."
        );

        textView.setTextSize(16);

        textView.setTextColor(
                getResources().getColor(
                        android.R.color.darker_gray
                )
        );

        textView.setGravity(
                Gravity.CENTER
        );

        textView.setPadding(
                20,
                50,
                20,
                50
        );

        postsContainer.addView(textView);
    }

    // =====================================================
    // ADD POST CARD
    // =====================================================

    private void addPostCard(
            String postId,
            String userName,
            String department,
            String session,
            String postText,
            Long likeCount,
            Long commentCount
    ) {

        // =================================================
        // CARD
        // =================================================

        CardView cardView =
                new CardView(this);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                12
        );

        cardView.setLayoutParams(cardParams);

        cardView.setCardElevation(2);

        cardView.setRadius(4);

        // =================================================
        // MAIN LAYOUT
        // =================================================

        LinearLayout mainLayout =
                new LinearLayout(this);

        mainLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        mainLayout.setPadding(
                15,
                15,
                15,
                10
        );

        // =================================================
        // USER HEADER
        // =================================================

        LinearLayout userLayout =
                new LinearLayout(this);

        userLayout.setOrientation(
                LinearLayout.HORIZONTAL
        );

        userLayout.setGravity(
                Gravity.CENTER_VERTICAL
        );

        // Avatar

        TextView avatar =
                new TextView(this);

        avatar.setText("👤");

        avatar.setTextSize(25);

        avatar.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams avatarParams =
                new LinearLayout.LayoutParams(
                        45,
                        45
                );

        avatar.setLayoutParams(
                avatarParams
        );

        // User info

        LinearLayout userInfo =
                new LinearLayout(this);

        userInfo.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        infoParams.setMargins(
                10,
                0,
                0,
                0
        );

        userInfo.setLayoutParams(
                infoParams
        );

        // Name

        TextView tvName =
                new TextView(this);

        tvName.setText(userName);

        tvName.setTextSize(17);

        tvName.setTextColor(
                getResources().getColor(
                        android.R.color.black
                )
        );

        tvName.setTypeface(
                null,
                Typeface.BOLD
        );

        // Department + Session

        TextView tvDepartment =
                new TextView(this);

        String departmentText;

        if (session != null &&
                !session.isEmpty()) {

            departmentText =
                    department + " • " + session;

        } else {

            departmentText =
                    department;
        }

        tvDepartment.setText(
                departmentText
        );

        tvDepartment.setTextSize(13);

        tvDepartment.setTextColor(
                getResources().getColor(
                        android.R.color.darker_gray
                )
        );

        userInfo.addView(tvName);

        userInfo.addView(tvDepartment);

        userLayout.addView(avatar);

        userLayout.addView(userInfo);

        // =================================================
        // POST TEXT
        // =================================================

        TextView tvPost =
                new TextView(this);

        tvPost.setText(postText);

        tvPost.setTextSize(16);

        tvPost.setTextColor(
                getResources().getColor(
                        android.R.color.black
                )
        );

        tvPost.setPadding(
                0,
                15,
                0,
                10
        );

        // =================================================
        // DIVIDER
        // =================================================

        View divider =
                new View(this);

        divider.setBackgroundColor(
                0xFFE0E0E0
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );

        divider.setLayoutParams(
                dividerParams
        );

        // =================================================
        // COUNT LAYOUT
        // =================================================

        LinearLayout countLayout =
                new LinearLayout(this);

        countLayout.setOrientation(
                LinearLayout.HORIZONTAL
        );

        countLayout.setGravity(
                Gravity.CENTER_VERTICAL
        );

        countLayout.setPadding(
                0,
                8,
                0,
                8
        );

        // Like count

        TextView tvLikeCount =
                new TextView(this);

        tvLikeCount.setText(
                "❤️ " + likeCount + " Likes"
        );

        tvLikeCount.setTextSize(14);

        tvLikeCount.setTextColor(
                getResources().getColor(
                        android.R.color.darker_gray
                )
        );

        LinearLayout.LayoutParams likeParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        tvLikeCount.setLayoutParams(
                likeParams
        );

        // Comment count

        TextView tvCommentCount =
                new TextView(this);

        tvCommentCount.setText(
                "💬 " + commentCount + " Comments"
        );

        tvCommentCount.setTextSize(14);

        tvCommentCount.setTextColor(
                getResources().getColor(
                        android.R.color.darker_gray
                )
        );

        countLayout.addView(
                tvLikeCount
        );

        countLayout.addView(
                tvCommentCount
        );

        // =================================================
        // ACTION LAYOUT
        // =================================================

        LinearLayout actionLayout =
                new LinearLayout(this);

        actionLayout.setOrientation(
                LinearLayout.HORIZONTAL
        );

        actionLayout.setGravity(
                Gravity.CENTER
        );

        // =================================================
        // LIKE BUTTON
        // =================================================

        Button btnLike =
                new Button(this);

        btnLike.setText("♡ Like");

        btnLike.setAllCaps(false);

        btnLike.setTextSize(13);

        btnLike.setTextColor(
                getResources().getColor(
                        R.color.app_name
                )
        );

        btnLike.setBackgroundColor(
                android.graphics.Color.TRANSPARENT
        );

        LinearLayout.LayoutParams likeButtonParams =
                new LinearLayout.LayoutParams(
                        0,
                        50,
                        1
                );

        btnLike.setLayoutParams(
                likeButtonParams
        );

        // =================================================
        // COMMENT BUTTON
        // =================================================

        Button btnComment =
                new Button(this);

        btnComment.setText(
                "💬 Comment"
        );

        btnComment.setAllCaps(false);

        btnComment.setTextSize(13);

        btnComment.setTextColor(
                getResources().getColor(
                        R.color.app_name
                )
        );

        btnComment.setBackgroundColor(
                android.graphics.Color.TRANSPARENT
        );

        LinearLayout.LayoutParams commentButtonParams =
                new LinearLayout.LayoutParams(
                        0,
                        50,
                        1
                );

        btnComment.setLayoutParams(
                commentButtonParams
        );

        // =================================================
        // SHARE BUTTON
        // =================================================

        Button btnShare =
                new Button(this);

        btnShare.setText(
                "↗ Share"
        );

        btnShare.setAllCaps(false);

        btnShare.setTextSize(13);

        btnShare.setTextColor(
                getResources().getColor(
                        R.color.app_name
                )
        );

        btnShare.setBackgroundColor(
                android.graphics.Color.TRANSPARENT
        );

        LinearLayout.LayoutParams shareButtonParams =
                new LinearLayout.LayoutParams(
                        0,
                        50,
                        1
                );

        btnShare.setLayoutParams(
                shareButtonParams
        );

        // =================================================
        // CLICK LISTENERS
        // =================================================

        btnLike.setOnClickListener(v -> {

            openPostDetails(postId);
        });

        btnComment.setOnClickListener(v -> {

            openPostDetails(postId);
        });

        tvPost.setOnClickListener(v -> {

            openPostDetails(postId);
        });

        tvName.setOnClickListener(v -> {

            openPostDetails(postId);
        });

        btnShare.setOnClickListener(v -> {

            sharePost(postText);
        });

        // =================================================
        // ADD BUTTONS
        // =================================================

        actionLayout.addView(
                btnLike
        );

        actionLayout.addView(
                btnComment
        );

        actionLayout.addView(
                btnShare
        );

        // =================================================
        // ADD ALL TO MAIN LAYOUT
        // =================================================

        mainLayout.addView(
                userLayout
        );

        mainLayout.addView(
                tvPost
        );

        mainLayout.addView(
                divider
        );

        mainLayout.addView(
                countLayout
        );

        mainLayout.addView(
                actionLayout
        );

        cardView.addView(
                mainLayout
        );

        postsContainer.addView(
                cardView
        );
    }

    // =====================================================
    // OPEN POST DETAILS
    // =====================================================

    private void openPostDetails(
            String postId
    ) {

        Intent intent =
                new Intent(
                        HomeFeedActivity.this,
                        PostDetailsActivity.class
                );

        intent.putExtra(
                "postId",
                postId
        );

        startActivity(intent);
    }

    // =====================================================
    // SHARE POST
    // =====================================================

    private void sharePost(
            String postText
    ) {

        Intent shareIntent =
                new Intent(
                        Intent.ACTION_SEND
                );

        shareIntent.setType(
                "text/plain"
        );

        shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                postText
        );

        startActivity(
                Intent.createChooser(
                        shareIntent,
                        "Share post"
                )
        );
    }
}