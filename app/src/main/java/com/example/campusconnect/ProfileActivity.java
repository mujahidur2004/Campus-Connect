package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName;
    private TextView tvDepartment;
    private TextView tvStudentId;
    private TextView tvEmail;

    private Button btnLogout;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Find Views
        tvProfileName = findViewById(R.id.tvProfileName);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvStudentId = findViewById(R.id.tvStudentId);
        tvEmail = findViewById(R.id.tvEmail);

        btnLogout = findViewById(R.id.btnLogout);

        // Check if user is logged in
        if (firebaseAuth.getCurrentUser() == null) {
            goToLogin();
            return;
        }

        // Load profile
        loadUserProfile();

        // Logout
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

    private void loadUserProfile() {

        if (firebaseAuth.getCurrentUser() == null) {
            goToLogin();
            return;
        }

        // Current user's Firebase UID
        String userId = firebaseAuth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        // Get data from Firestore
                        String name =
                                documentSnapshot.getString("name");

                        String studentId =
                                documentSnapshot.getString("studentId");

                        String email =
                                documentSnapshot.getString("email");

                        // Set Name
                        if (name != null && !name.isEmpty()) {
                            tvProfileName.setText(name);
                        }

                        // Set Student ID
                        if (studentId != null && !studentId.isEmpty()) {
                            tvStudentId.setText(studentId);
                        }

                        // Set Email
                        if (email != null && !email.isEmpty()) {
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
}