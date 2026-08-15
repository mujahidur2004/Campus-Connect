package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreatePostActivity extends AppCompatActivity {

    private EditText etPost;
    private Button btnPost;
    private Button btnImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        etPost = findViewById(R.id.etPost);
        btnPost = findViewById(R.id.btnPost);
        btnImage = findViewById(R.id.btnImage);

        // Choose Image
        btnImage.setOnClickListener(v -> {

            Toast.makeText(
                    CreatePostActivity.this,
                    "Image upload will be added next",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // Post button
        btnPost.setOnClickListener(v -> {

            String postText = etPost.getText().toString().trim();

            if (postText.isEmpty()) {

                etPost.setError("Please write something");
                etPost.requestFocus();

                return;
            }

            Toast.makeText(
                    CreatePostActivity.this,
                    "Post created successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    CreatePostActivity.this,
                    HomeFeedActivity.class
            );

            startActivity(intent);

            finish();
        });
    }
}