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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private TextView tvBack;
    private LinearLayout notificationContainer;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);


        // =========================
        // Firebase
        // =========================

        firebaseAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();


        // =========================
        // Find Views
        // =========================

        tvBack = findViewById(R.id.tvBack);

        notificationContainer =
                findViewById(R.id.notificationContainer);


        // =========================
        // Check Login
        // =========================

        if (firebaseAuth.getCurrentUser() == null) {

            finish();

            return;
        }


        // =========================
        // Back Button
        // =========================

        tvBack.setOnClickListener(v -> finish());


        // =========================
        // Load Notifications
        // =========================

        loadNotifications();
    }


    // =========================================================
    // LOAD NOTIFICATIONS
    // =========================================================

    private void loadNotifications() {

        if (firebaseAuth.getCurrentUser() == null) {

            return;
        }


        String currentUserId =
                firebaseAuth.getCurrentUser().getUid();


        // Clear old notifications

        notificationContainer.removeAllViews();


        // =====================================================
        // IMPORTANT:
        // No orderBy() here.
        // This avoids Firestore composite index problem.
        // =====================================================

        firestore.collection("notifications")
                .whereEqualTo(
                        "recipientId",
                        currentUserId
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {


                    // =========================================
                    // No Notifications
                    // =========================================

                    if (querySnapshot.isEmpty()) {

                        showEmptyMessage();

                        return;
                    }


                    // =========================================
                    // Convert Documents To List
                    // =========================================

                    List<DocumentSnapshot> notifications =
                            new ArrayList<>(
                                    querySnapshot.getDocuments()
                            );


                    // =========================================
                    // Sort By Timestamp
                    // Newest First
                    // =========================================

                    Collections.sort(
                            notifications,
                            new Comparator<DocumentSnapshot>() {

                                @Override
                                public int compare(
                                        DocumentSnapshot d1,
                                        DocumentSnapshot d2
                                ) {

                                    Long t1 =
                                            d1.getLong("timestamp");

                                    Long t2 =
                                            d2.getLong("timestamp");


                                    if (t1 == null) {
                                        t1 = 0L;
                                    }


                                    if (t2 == null) {
                                        t2 = 0L;
                                    }


                                    return Long.compare(
                                            t2,
                                            t1
                                    );
                                }
                            }
                    );


                    // =========================================
                    // Display Notifications
                    // =========================================

                    for (DocumentSnapshot document :
                            notifications) {


                        String senderName =
                                document.getString(
                                        "senderName"
                                );


                        String message =
                                document.getString(
                                        "message"
                                );


                        String type =
                                document.getString(
                                        "type"
                                );


                        String postId =
                                document.getString(
                                        "postId"
                                );


                        Boolean read =
                                document.getBoolean(
                                        "read"
                                );


                        // -------------------------------------
                        // Default Values
                        // -------------------------------------

                        if (senderName == null ||
                                senderName.isEmpty()) {

                            senderName = "Student";
                        }


                        if (message == null) {

                            message = "";
                        }


                        if (type == null) {

                            type = "";
                        }


                        if (postId == null) {

                            postId = "";
                        }


                        boolean isRead =
                                read != null && read;


                        // -------------------------------------
                        // Add UI
                        // -------------------------------------

                        addNotificationToUI(
                                document.getId(),
                                senderName,
                                message,
                                type,
                                postId,
                                isRead
                        );
                    }

                })
                .addOnFailureListener(e -> {


                    // =========================================
                    // Error
                    // =========================================

                    Toast.makeText(
                            NotificationActivity.this,
                            "Failed to load notifications",
                            Toast.LENGTH_LONG
                    ).show();


                    e.printStackTrace();
                });
    }


    // =========================================================
    // ADD NOTIFICATION UI
    // =========================================================

    private void addNotificationToUI(
            String notificationId,
            String senderName,
            String message,
            String type,
            String postId,
            boolean read
    ) {


        // =========================================
        // Main Notification Layout
        // =========================================

        LinearLayout notificationLayout =
                new LinearLayout(this);


        notificationLayout.setOrientation(
                LinearLayout.HORIZONTAL
        );


        notificationLayout.setGravity(
                Gravity.CENTER_VERTICAL
        );


        int padding =
                dpToPx(14);


        notificationLayout.setPadding(
                padding,
                padding,
                padding,
                padding
        );


        // =========================================
        // Background
        // =========================================

        if (read) {

            notificationLayout.setBackgroundColor(
                    Color.WHITE
            );

        } else {

            notificationLayout.setBackgroundColor(
                    Color.parseColor("#EEF5FF")
            );
        }


        // =========================================
        // ICON
        // =========================================

        TextView icon =
                new TextView(this);


        icon.setTextSize(25);

        icon.setGravity(
                Gravity.CENTER
        );


        if ("like".equals(type)) {

            icon.setText("❤️");

        } else if ("comment".equals(type)) {

            icon.setText("💬");

        } else {

            icon.setText("🔔");
        }


        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dpToPx(50),
                        dpToPx(50)
                );


        notificationLayout.addView(
                icon,
                iconParams
        );


        // =========================================
        // TEXT AREA
        // =========================================

        LinearLayout textLayout =
                new LinearLayout(this);


        textLayout.setOrientation(
                LinearLayout.VERTICAL
        );


        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );


        textParams.setMargins(
                dpToPx(10),
                0,
                0,
                0
        );


        // =========================================
        // Sender Name
        // =========================================

        TextView senderText =
                new TextView(this);


        senderText.setText(
                senderName
        );


        senderText.setTextSize(16);


        senderText.setTextColor(
                Color.BLACK
        );


        senderText.setTypeface(
                null,
                Typeface.BOLD
        );


        // =========================================
        // Message
        // =========================================

        TextView messageText =
                new TextView(this);


        messageText.setText(
                message
        );


        messageText.setTextSize(14);


        messageText.setTextColor(
                Color.DKGRAY
        );


        messageText.setPadding(
                0,
                dpToPx(4),
                0,
                0
        );


        // =========================================
        // Add Text
        // =========================================

        textLayout.addView(
                senderText
        );


        textLayout.addView(
                messageText
        );


        notificationLayout.addView(
                textLayout,
                textParams
        );


        // =========================================
        // Click Notification
        // =========================================

        notificationLayout.setClickable(true);


        String finalNotificationId =
                notificationId;


        String finalPostId =
                postId;


        notificationLayout.setOnClickListener(v -> {


            // -----------------------------------------
            // Mark As Read
            // -----------------------------------------

            markNotificationAsRead(
                    finalNotificationId
            );


            // -----------------------------------------
            // Open Post
            // -----------------------------------------

            if (finalPostId != null &&
                    !finalPostId.isEmpty()) {


                Intent intent =
                        new Intent(
                                NotificationActivity.this,
                                PostDetailsActivity.class
                        );


                intent.putExtra(
                        "postId",
                        finalPostId
                );


                startActivity(intent);
            }
        });


        // =========================================
        // Layout Parameters
        // =========================================

        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        layoutParams.setMargins(
                0,
                0,
                0,
                dpToPx(6)
        );


        // =========================================
        // Add To Container
        // =========================================

        notificationContainer.addView(
                notificationLayout,
                layoutParams
        );
    }


    // =========================================================
    // MARK NOTIFICATION AS READ
    // =========================================================

    private void markNotificationAsRead(
            String notificationId
    ) {


        firestore.collection("notifications")
                .document(notificationId)
                .update(
                        "read",
                        true
                );
    }


    // =========================================================
    // EMPTY MESSAGE
    // =========================================================

    private void showEmptyMessage() {


        TextView emptyText =
                new TextView(this);


        emptyText.setText(
                "No notifications yet"
        );


        emptyText.setTextSize(16);


        emptyText.setTextColor(
                Color.GRAY
        );


        emptyText.setGravity(
                Gravity.CENTER
        );


        emptyText.setPadding(
                dpToPx(20),
                dpToPx(80),
                dpToPx(20),
                dpToPx(20)
        );


        notificationContainer.addView(
                emptyText
        );
    }


    // =========================================================
    // DP TO PX
    // =========================================================

    private int dpToPx(int dp) {


        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;


        return (int) (
                dp * density + 0.5f
        );
    }


    // =========================================================
    // REFRESH WHEN RETURNING
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();


        if (firebaseAuth != null &&
                firebaseAuth.getCurrentUser() != null) {

            loadNotifications();
        }
    }
}