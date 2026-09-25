package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class PostDetailsActivity extends AppCompatActivity {

    private TextView tvBack;
    private TextView tvLikeCount;
    private TextView tvCommentCount;

    private Button btnLike;
    private Button btnComment;
    private Button btnShare;
    private Button btnSendComment;

    private EditText etComment;

    private LinearLayout commentContainer;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private String postId;

    private int likeCount = 0;
    private int commentCount = 0;

    private boolean liked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Get Post ID from HomeFeed
        postId = getIntent().getStringExtra("postId");

        // Check login
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this,
                    "Please login first",
                    Toast.LENGTH_SHORT).show();

            finish();
            return;
        }

        // Check post ID
        if (postId == null || postId.isEmpty()) {
            Toast.makeText(this,
                    "Post not found",
                    Toast.LENGTH_SHORT).show();

            finish();
            return;
        }

        // Find Views
        tvBack = findViewById(R.id.tvBack);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvCommentCount = findViewById(R.id.tvCommentCount);

        btnLike = findViewById(R.id.btnLike);
        btnComment = findViewById(R.id.btnComment);
        btnShare = findViewById(R.id.btnShare);
        btnSendComment = findViewById(R.id.btnSendComment);

        etComment = findViewById(R.id.etComment);
        commentContainer = findViewById(R.id.commentContainer);

        // Initial count
        updateCount();

        // Load post data
        loadPost();

        // Check current user's like
        checkUserLike();

        // Load comments
        loadComments();

        // Back button
        tvBack.setOnClickListener(v -> finish());

        // Like button
        btnLike.setOnClickListener(v -> toggleLike());

        // Comment button
        btnComment.setOnClickListener(v -> {
            etComment.requestFocus();
        });

        // Share button
        btnShare.setOnClickListener(v -> sharePost());

        // Send comment
        btnSendComment.setOnClickListener(v -> {
            String comment = etComment.getText()
                    .toString()
                    .trim();

            if (comment.isEmpty()) {
                etComment.setError("Write a comment");
                etComment.requestFocus();
                return;
            }

            addNewComment(comment);
        });
    }

    // ---------------------------------------------------------
    // LOAD POST
    // ---------------------------------------------------------

    private void loadPost() {

        firestore.collection("posts")
                .document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                PostDetailsActivity.this,
                                "Post not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                        return;
                    }

                    Long likes = documentSnapshot.getLong("likeCount");
                    Long comments = documentSnapshot.getLong("commentCount");

                    if (likes != null) {
                        likeCount = likes.intValue();
                    } else {
                        likeCount = 0;
                    }

                    if (comments != null) {
                        commentCount = comments.intValue();
                    } else {
                        commentCount = 0;
                    }

                    updateCount();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                PostDetailsActivity.this,
                                "Failed to load post: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    // ---------------------------------------------------------
    // LIKE / UNLIKE
    // ---------------------------------------------------------

    private void toggleLike() {

        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();

        btnLike.setEnabled(false);

        if (liked) {

            // UNLIKE

            firestore.collection("posts")
                    .document(postId)
                    .collection("likes")
                    .document(userId)
                    .delete()
                    .addOnSuccessListener(unused -> {

                        firestore.collection("posts")
                                .document(postId)
                                .update(
                                        "likeCount",
                                        FieldValue.increment(-1)
                                )
                                .addOnSuccessListener(unused2 -> {

                                    liked = false;

                                    if (likeCount > 0) {
                                        likeCount--;
                                    }

                                    updateLikeButton();
                                    updateCount();

                                    btnLike.setEnabled(true);
                                })
                                .addOnFailureListener(e -> {

                                    btnLike.setEnabled(true);

                                    Toast.makeText(
                                            PostDetailsActivity.this,
                                            "Failed to update like count",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                });

                    })
                    .addOnFailureListener(e -> {

                        btnLike.setEnabled(true);

                        Toast.makeText(
                                PostDetailsActivity.this,
                                "Failed to unlike post",
                                Toast.LENGTH_SHORT
                        ).show();
                    });

        } else {

            // LIKE

            Map<String, Object> likeData = new HashMap<>();

            likeData.put(
                    "userId",
                    userId
            );

            likeData.put(
                    "timestamp",
                    System.currentTimeMillis()
            );

            firestore.collection("posts")
                    .document(postId)
                    .collection("likes")
                    .document(userId)
                    .set(likeData)
                    .addOnSuccessListener(unused -> {

                        firestore.collection("posts")
                                .document(postId)
                                .update(
                                        "likeCount",
                                        FieldValue.increment(1)
                                )
                                .addOnSuccessListener(unused2 -> {

                                    liked = true;
                                    likeCount++;

                                    updateLikeButton();
                                    updateCount();

                                    btnLike.setEnabled(true);
                                })
                                .addOnFailureListener(e -> {

                                    btnLike.setEnabled(true);

                                    Toast.makeText(
                                            PostDetailsActivity.this,
                                            "Failed to update like count",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                });

                    })
                    .addOnFailureListener(e -> {

                        btnLike.setEnabled(true);

                        Toast.makeText(
                                PostDetailsActivity.this,
                                "Failed to like post",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        }
    }

    // ---------------------------------------------------------
    // CHECK USER LIKE
    // ---------------------------------------------------------

    private void checkUserLike() {

        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();

        firestore.collection("posts")
                .document(postId)
                .collection("likes")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    liked = documentSnapshot.exists();

                    updateLikeButton();
                });
    }

    // ---------------------------------------------------------
    // LIKE BUTTON UI
    // ---------------------------------------------------------

    private void updateLikeButton() {

        if (liked) {

            btnLike.setText("❤️ Liked");

        } else {

            btnLike.setText("♡ Like");
        }
    }

    // ---------------------------------------------------------
    // UPDATE COUNT
    // ---------------------------------------------------------

    private void updateCount() {

        tvLikeCount.setText(
                likeCount + " Likes"
        );

        tvCommentCount.setText(
                commentCount + " Comments"
        );
    }

    // ---------------------------------------------------------
    // ADD COMMENT
    // ---------------------------------------------------------

    private void addNewComment(String commentText) {

        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String userId =
                firebaseAuth.getCurrentUser().getUid();

        String email =
                firebaseAuth.getCurrentUser().getEmail();

        btnSendComment.setEnabled(false);
        btnSendComment.setText("Sending...");

        Map<String, Object> commentData =
                new HashMap<>();

        commentData.put(
                "userId",
                userId
        );

        commentData.put(
                "email",
                email
        );

        commentData.put(
                "commentText",
                commentText
        );

        commentData.put(
                "timestamp",
                System.currentTimeMillis()
        );

        firestore.collection("posts")
                .document(postId)
                .collection("comments")
                .add(commentData)
                .addOnSuccessListener(documentReference -> {

                    firestore.collection("posts")
                            .document(postId)
                            .update(
                                    "commentCount",
                                    FieldValue.increment(1)
                            )
                            .addOnSuccessListener(unused -> {

                                commentCount++;

                                updateCount();

                                etComment.setText("");

                                btnSendComment.setEnabled(true);
                                btnSendComment.setText("Send");

                                Toast.makeText(
                                        PostDetailsActivity.this,
                                        "Comment added",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadComments();
                            })
                            .addOnFailureListener(e -> {

                                btnSendComment.setEnabled(true);
                                btnSendComment.setText("Send");

                                Toast.makeText(
                                        PostDetailsActivity.this,
                                        "Comment count update failed",
                                        Toast.LENGTH_SHORT
                                ).show();
                            });

                })
                .addOnFailureListener(e -> {

                    btnSendComment.setEnabled(true);
                    btnSendComment.setText("Send");

                    Toast.makeText(
                            PostDetailsActivity.this,
                            "Failed to add comment: " +
                                    e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // ---------------------------------------------------------
    // LOAD COMMENTS
    // ---------------------------------------------------------

    private void loadComments() {

        commentContainer.removeAllViews();

        firestore.collection("posts")
                .document(postId)
                .collection("comments")
                .orderBy(
                        "timestamp",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot document :
                            queryDocumentSnapshots.getDocuments()) {

                        String email =
                                document.getString("email");

                        String commentText =
                                document.getString("commentText");

                        if (email == null) {
                            email = "Student";
                        }

                        if (commentText == null) {
                            commentText = "";
                        }

                        addCommentToUI(
                                email,
                                commentText
                        );
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                PostDetailsActivity.this,
                                "Failed to load comments: " +
                                        e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    // ---------------------------------------------------------
    // ADD COMMENT TO UI
    // ---------------------------------------------------------

    private void addCommentToUI(
            String userEmail,
            String commentText
    ) {

        LinearLayout commentLayout =
                new LinearLayout(this);

        commentLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        commentLayout.setPadding(
                15,
                10,
                15,
                10
        );

        // User name/email
        TextView userName =
                new TextView(this);

        userName.setText(userEmail);

        userName.setTextSize(15);

        userName.setTextColor(
                getResources().getColor(
                        R.color.black
                )
        );

        userName.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        // Comment
        TextView comment =
                new TextView(this);

        comment.setText(commentText);

        comment.setTextSize(14);

        comment.setTextColor(
                getResources().getColor(
                        android.R.color.darker_gray
                )
        );

        comment.setPadding(
                0,
                5,
                0,
                0
        );

        commentLayout.addView(userName);
        commentLayout.addView(comment);

        commentContainer.addView(
                commentLayout
        );
    }

    // ---------------------------------------------------------
    // SHARE POST
    // ---------------------------------------------------------

    private void sharePost() {

        Intent shareIntent =
                new Intent(Intent.ACTION_SEND);

        shareIntent.setType("text/plain");

        shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this post on CampusConnect!"
        );

        startActivity(
                Intent.createChooser(
                        shareIntent,
                        "Share Post"
                )
        );
    }
}