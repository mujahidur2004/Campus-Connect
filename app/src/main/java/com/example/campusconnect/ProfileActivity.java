package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvBack;
    private TextView tvProfileName;
    private TextView tvDepartment;
    private TextView tvStudentId;
    private TextView tvSession;
    private TextView tvPhone;
    private TextView tvEmail;

    private TextView tvPostCount;
    private TextView tvLikeCount;
    private TextView tvCommentCount;

    private Button btnLogout;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        // =====================================================
        // FIREBASE
        // =====================================================

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // =====================================================
        // FIND VIEWS
        // =====================================================

        tvBack = findViewById(R.id.tvBack);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvStudentId = findViewById(R.id.tvStudentId);
        tvSession = findViewById(R.id.tvSession);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);

        tvPostCount = findViewById(R.id.tvPostCount);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvCommentCount = findViewById(R.id.tvCommentCount);

        btnLogout = findViewById(R.id.btnLogout);

        // =====================================================
        // CHECK LOGIN
        // =====================================================

        if (firebaseAuth.getCurrentUser() == null) {
            goToLogin();
            return;
        }

        // =====================================================
        // LOAD PROFILE
        // =====================================================

        loadUserProfile();

        // =====================================================
        // LOAD STATISTICS
        // =====================================================

        loadUserStatistics();

        // =====================================================
        // BACK BUTTON
        // =====================================================

        tvBack.setOnClickListener(v -> finish());

        // =====================================================
        // LOGOUT
        // =====================================================

        btnLogout.setOnClickListener(v -> {

            firebaseAuth.signOut();

            Toast.makeText(
                    ProfileActivity.this,
                    "Logged out successfully",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    ProfileActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
        });
    }

    // =========================================================
    // LOAD USER PROFILE
    // =========================================================

    private void loadUserProfile() {

        if (firebaseAuth.getCurrentUser() == null) {
            goToLogin();
            return;
        }

        String userId =
                firebaseAuth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        // -----------------------------
                        // Name
                        // -----------------------------

                        String name =
                                documentSnapshot.getString("name");

                        if (name != null && !name.isEmpty()) {
                            tvProfileName.setText(name);
                        }

                        // -----------------------------
                        // Department
                        // -----------------------------

                        String department =
                                documentSnapshot.getString("department");

                        if (department != null &&
                                !department.isEmpty()) {

                            tvDepartment.setText(department);
                        }

                        // -----------------------------
                        // Student ID
                        // -----------------------------

                        String studentId =
                                documentSnapshot.getString("studentId");

                        if (studentId != null &&
                                !studentId.isEmpty()) {

                            tvStudentId.setText(studentId);
                        }

                        // -----------------------------
                        // Session
                        // -----------------------------

                        String session =
                                documentSnapshot.getString("session");

                        if (session != null &&
                                !session.isEmpty()) {

                            tvSession.setText(session);
                        }

                        // -----------------------------
                        // Phone
                        // -----------------------------

                        String phone =
                                documentSnapshot.getString("phone");

                        if (phone != null &&
                                !phone.isEmpty()) {

                            tvPhone.setText(phone);
                        }

                        // -----------------------------
                        // Email
                        // -----------------------------

                        String email =
                                documentSnapshot.getString("email");

                        if (email != null &&
                                !email.isEmpty()) {

                            tvEmail.setText(email);
                        }

                    } else {

                        Toast.makeText(
                                ProfileActivity.this,
                                "Profile data not found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            ProfileActivity.this,
                            "Failed to load profile: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // LOAD REAL USER STATISTICS
    // =========================================================

    private void loadUserStatistics() {

        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        String userId =
                firebaseAuth.getCurrentUser().getUid();

        firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    // -----------------------------
                    // Total Posts
                    // -----------------------------

                    int totalPosts =
                            querySnapshot.size();

                    // -----------------------------
                    // Total Likes
                    // -----------------------------

                    int totalLikes = 0;

                    // -----------------------------
                    // Total Comments
                    // -----------------------------

                    int totalComments = 0;

                    // -----------------------------
                    // Read every post
                    // -----------------------------

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        Long likes =
                                document.getLong("likeCount");

                        Long comments =
                                document.getLong("commentCount");

                        if (likes != null) {
                            totalLikes += likes.intValue();
                        }

                        if (comments != null) {
                            totalComments += comments.intValue();
                        }
                    }

                    // -----------------------------
                    // Show Statistics
                    // -----------------------------

                    tvPostCount.setText(
                            String.valueOf(totalPosts)
                    );

                    tvLikeCount.setText(
                            String.valueOf(totalLikes)
                    );

                    tvCommentCount.setText(
                            String.valueOf(totalComments)
                    );

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            ProfileActivity.this,
                            "Failed to load statistics: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // GO TO LOGIN
    // =========================================================

    private void goToLogin() {

        Intent intent = new Intent(
                ProfileActivity.this,
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }

    // =========================================================
    // RELOAD PROFILE WHEN RETURNING
    // =========================================================

    @Override
    protected void onResume() {
        super.onResume();

        if (firebaseAuth != null &&
                firebaseAuth.getCurrentUser() != null) {

            loadUserProfile();
            loadUserStatistics();
        }
    }
}