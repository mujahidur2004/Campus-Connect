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

        // Image button
        // Image upload is not being used
        btnImage.setOnClickListener(v -> {

            Toast.makeText(
                    CreatePostActivity.this,
                    "Image upload is not available",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Post button
        btnPost.setOnClickListener(v -> createPost());
    }

    // =========================================================
    // CREATE POST
    // =========================================================

    private void createPost() {

        String postText =
                etPost.getText().toString().trim();

        // Check empty post
        if (postText.isEmpty()) {

            etPost.setError(
                    "Please write something"
            );

            etPost.requestFocus();

            return;
        }

        // Check login
        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // User ID
        String userId =
                firebaseAuth.getCurrentUser().getUid();

        // Email
        String email =
                firebaseAuth.getCurrentUser().getEmail();

        // Disable button
        btnPost.setEnabled(false);
        btnPost.setText("Posting...");

        // =====================================================
        // POST DATA
        // =====================================================

        Map<String, Object> post =
                new HashMap<>();

        post.put(
                "userId",
                userId
        );

        post.put(
                "email",
                email
        );

        post.put(
                "postText",
                postText
        );

        // Initial Like Count
        post.put(
                "likeCount",
                0
        );

        // Initial Comment Count
        post.put(
                "commentCount",
                0
        );

        // Timestamp
        post.put(
                "timestamp",
                System.currentTimeMillis()
        );

        // =====================================================
        // SAVE TO FIRESTORE
        // =====================================================

        firestore
                .collection("posts")
                .add(post)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            CreatePostActivity.this,
                            "Post created successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Go to Home Feed
                    Intent intent =
                            new Intent(
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