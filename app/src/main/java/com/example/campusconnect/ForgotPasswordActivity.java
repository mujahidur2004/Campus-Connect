package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnReset;
    private TextView tvBackLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnReset = findViewById(R.id.btnReset);
        tvBackLogin = findViewById(R.id.tvBackLogin);

        btnReset.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Enter your email");
                return;
            }

            Toast.makeText(
                    ForgotPasswordActivity.this,
                    "Reset link sent to " + email,
                    Toast.LENGTH_LONG
            ).show();
        });

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