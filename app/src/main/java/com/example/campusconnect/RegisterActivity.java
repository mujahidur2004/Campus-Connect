package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etStudentId;
    private EditText etDepartment;
    private EditText etSession;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etPassword;

    private Button btnRegister;
    private TextView tvLogin;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Find Views
        etName = findViewById(R.id.etName);
        etStudentId = findViewById(R.id.etStudentId);
        etDepartment = findViewById(R.id.etDepartment);
        etSession = findViewById(R.id.etSession);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Register Button
        btnRegister.setOnClickListener(v -> registerUser());

        // Login Button
        tvLogin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);

            finish();
        });
    }

    // =========================================================
    // REGISTER USER
    // =========================================================

    private void registerUser() {

        String name =
                etName.getText().toString().trim();

        String studentId =
                etStudentId.getText().toString().trim();

        String department =
                etDepartment.getText().toString().trim();

        String session =
                etSession.getText().toString().trim();

        String phone =
                etPhone.getText().toString().trim();

        String email =
                etEmail.getText().toString().trim();

        String password =
                etPassword.getText().toString().trim();

        // =====================================================
        // VALIDATION
        // =====================================================

        if (name.isEmpty()) {

            etName.setError("Enter your name");
            etName.requestFocus();
            return;
        }

        if (studentId.isEmpty()) {

            etStudentId.setError("Enter your student ID");
            etStudentId.requestFocus();
            return;
        }

        if (department.isEmpty()) {

            etDepartment.setError("Enter your department");
            etDepartment.requestFocus();
            return;
        }

        if (session.isEmpty()) {

            etSession.setError("Enter your session");
            etSession.requestFocus();
            return;
        }

        if (phone.isEmpty()) {

            etPhone.setError("Enter your phone number");
            etPhone.requestFocus();
            return;
        }

        if (phone.length() < 10) {

            etPhone.setError("Enter a valid phone number");
            etPhone.requestFocus();
            return;
        }

        if (email.isEmpty()) {

            etEmail.setError("Enter your email");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {

            etPassword.setError("Enter your password");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {

            etPassword.setError(
                    "Password must be at least 6 characters"
            );

            etPassword.requestFocus();
            return;
        }

        // Disable button
        btnRegister.setEnabled(false);
        btnRegister.setText("Creating Account...");

        // =====================================================
        // FIREBASE AUTHENTICATION
        // =====================================================

        firebaseAuth
                .createUserWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        // Get Firebase User ID
                        String userId =
                                firebaseAuth
                                        .getCurrentUser()
                                        .getUid();

                        // =================================================
                        // USER DATA
                        // =================================================

                        Map<String, Object> user =
                                new HashMap<>();

                        user.put(
                                "userId",
                                userId
                        );

                        user.put(
                                "name",
                                name
                        );

                        user.put(
                                "studentId",
                                studentId
                        );

                        user.put(
                                "department",
                                department
                        );

                        user.put(
                                "session",
                                session
                        );

                        user.put(
                                "phone",
                                phone
                        );

                        user.put(
                                "email",
                                email
                        );

                        // =================================================
                        // SAVE TO FIRESTORE
                        // =================================================

                        firestore
                                .collection("users")
                                .document(userId)
                                .set(user)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(
                                            RegisterActivity.this,
                                            "Registration Successful!",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    // Go to Home
                                    Intent intent =
                                            new Intent(
                                                    RegisterActivity.this,
                                                    HomeFeedActivity.class
                                            );

                                    intent.setFlags(
                                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    );

                                    startActivity(intent);

                                    finish();
                                })
                                .addOnFailureListener(e -> {

                                    btnRegister.setEnabled(true);
                                    btnRegister.setText("Register");

                                    Toast.makeText(
                                            RegisterActivity.this,
                                            "Failed to save profile: "
                                                    + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                });

                    } else {

                        btnRegister.setEnabled(true);
                        btnRegister.setText("Register");

                        Toast.makeText(
                                RegisterActivity.this,
                                "Registration Failed: "
                                        + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}