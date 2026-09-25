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

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnReset;
    TextView tvBackLogin;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Find Views
        etEmail = findViewById(R.id.etEmail);
        btnReset = findViewById(R.id.btnReset);
        tvBackLogin = findViewById(R.id.tvBackLogin);

        // Send Reset Link
        btnReset.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();

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

            btnReset.setEnabled(false);
            btnReset.setText("Sending...");

            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {

                        btnReset.setEnabled(true);
                        btnReset.setText("Send Reset Link");

                        if (task.isSuccessful()) {

                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "Password reset link sent to your email",
                                    Toast.LENGTH_LONG
                            ).show();

                        } else {

                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "Failed: " +
                                            task.getException().getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });

        // Back to Login
        tvBackLogin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ForgotPasswordActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
        });
    }
}