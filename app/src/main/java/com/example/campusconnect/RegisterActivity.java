package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity
        implements View.OnClickListener {

    private TextView btnlogin;
    private Button btnRegister;

    private EditText etName;
    private EditText etStudentId;
    private EditText etEmail;
    private EditText etPassword;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Views
        btnlogin = findViewById(R.id.tvLogin);
        btnRegister = findViewById(R.id.btnRegister);

        etName = findViewById(R.id.etName);
        etStudentId = findViewById(R.id.etStudentId);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // Click listeners
        btnlogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // Login button
        if (v.getId() == R.id.tvLogin) {

            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
        }

        // Register button
        else if (v.getId() == R.id.btnRegister) {

            registerUser();
        }
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Name validation
        if (name.isEmpty()) {
            etName.setError("Enter your full name");
            etName.requestFocus();
            return;
        }

        // Student ID validation
        if (studentId.isEmpty()) {
            etStudentId.setError("Enter your student ID");
            etStudentId.requestFocus();
            return;
        }

        // Email validation
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

        // Password validation
        if (password.isEmpty()) {
            etPassword.setError("Enter a password");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        // Disable button while registration is running
        btnRegister.setEnabled(false);
        btnRegister.setText("Creating Account...");

        // Firebase Authentication
        firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        // Get Firebase User ID
                        String userId = firebaseAuth
                                .getCurrentUser()
                                .getUid();

                        // Create user data
                        Map<String, Object> user = new HashMap<>();

                        user.put("name", name);
                        user.put("studentId", studentId);
                        user.put("email", email);
                        user.put("userId", userId);

                        // Save user information in Firestore
                        firestore
                                .collection("users")
                                .document(userId)
                                .set(user)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(
                                            RegisterActivity.this,
                                            "Registration Successful!",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    // Go to Login
                                    Intent intent = new Intent(
                                            RegisterActivity.this,
                                            LoginActivity.class
                                    );

                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {

                                    btnRegister.setEnabled(true);
                                    btnRegister.setText("Register");

                                    Toast.makeText(
                                            RegisterActivity.this,
                                            "Profile save failed: "
                                                    + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                });

                    } else {

                        btnRegister.setEnabled(true);
                        btnRegister.setText("Register");

                        String errorMessage = "Registration failed";

                        if (task.getException() != null) {
                            errorMessage =
                                    task.getException().getMessage();
                        }

                        Toast.makeText(
                                RegisterActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}