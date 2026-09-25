package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    private EditText etPost;
    private Button btnPost;
    private Button btnImage;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Find Views
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
        btnPost.setOnClickListener(v -> createPost());
    }

    private void createPost() {

        String postText = etPost.getText().toString().trim();

        // Empty post check
        if (postText.isEmpty()) {

            etPost.setError("Please write something");
            etPost.requestFocus();

            return;
        }

        // Check login
        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    CreatePostActivity.this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Get current user information
        String userId = firebaseAuth.getCurrentUser().getUid();

        String email = firebaseAuth.getCurrentUser().getEmail();

        // Disable button while saving
        btnPost.setEnabled(false);
        btnPost.setText("Posting...");

        // Create post data
        Map<String, Object> post = new HashMap<>();

        post.put("userId", userId);
        post.put("email", email);
        post.put("postText", postText);
        post.put("timestamp", System.currentTimeMillis());

        // Save to Firestore
        firestore.collection("posts")
                .add(post)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            CreatePostActivity.this,
                            "Post created successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Go to Home Feed
                    Intent intent = new Intent(
                            CreatePostActivity.this,
                            HomeFeedActivity.class
                    );

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                    );

                    startActivity(intent);

                    finish();
                })
                .addOnFailureListener(e -> {

                    btnPost.setEnabled(true);
                    btnPost.setText("Post");

                    Toast.makeText(
                            CreatePostActivity.this,
                            "Failed to create post: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}