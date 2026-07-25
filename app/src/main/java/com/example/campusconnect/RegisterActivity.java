package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView btnlogin;
    Button btnRegister;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnlogin = findViewById(R.id.tvLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnlogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvLogin) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.btnRegister) {
            Toast.makeText(this, "Registration Successful\nPlease Login", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }


}