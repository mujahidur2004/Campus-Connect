package com.example.campusconnect;

import android.content.Intent;
import android.graphics.Typeface;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostDetailsActivity extends AppCompatActivity {

    private TextView tvBack;
    private TextView tvPostName;
    private TextView tvPostInfo;
    private TextView tvPostText;
    private TextView tvPostTime;

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
    private String postOwnerId;

    private int likeCount = 0;
    private int commentCount = 0;

    private boolean liked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_details);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        postId = getIntent().getStringExtra("postId");

        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        if (postId == null || postId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Post not found",
                    Toast_SHORT
            ).show();

            finish();
            return;
        }

        tvBack = findViewById(R.id.tvBack);

        tvPostName = findViewById(R.id.tvPostName);
        tvPostInfo = findViewById(R.id.tvPostInfo);
        tvPostText = findViewById(R.id.tvPostText);
        tvPostTime = findViewById(R.id.tvPostTime);

        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvCommentCount = findViewById(R.id.tvCommentCount);

        btnLike = findViewById(R.id.btnLike);
        btnComment = findViewById(R.id.btnComment);
        btnShare = findViewById(R.id.btnShare);
        btnSendComment = findViewById(R.id.btnSendComment);

        etComment = findViewById(R.id.etComment);

        commentContainer = findViewById(R.id.commentContainer);

        tvPostName.setText("Loading...");
        tvPostInfo.setText("");
        tvPostText.setText("Loading post...");
        tvPostTime.setText("");

        updateCount();

        loadPost();
        checkUserLike();
        loadComments();

        tvBack.setOnClickListener(v -> finish());

        btnLike.setOnClickListener(v -> toggleLike());

        btnComment.setOnClickListener(v -> {

            etComment.requestFocus();

            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager)
                            getSystemService(INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.showSoftInput(
                        etComment,
                        android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
                );
            }
        });

        btnShare.setOnClickListener(v -> sharePost());

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

    private void loadPost() {

        firestore.collection("posts")
                .document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                PostDetailsActivity.this,
                                "This post no longer exists",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                        return;
                    }

                    postOwnerId =
                            documentSnapshot.getString("userId");

                    String postText =
                            documentSnapshot.getString("postText");

                    if (postText == null ||
                            postText.trim().isEmpty()) {

                        postText = "No text available";
                    }

                    tvPostText.setText(postText);

                    Long likes =
                            documentSnapshot.getLong("likeCount");

                    likeCount =
                            likes != null ? likes.intValue() : 0;

                    Long comments =
                            documentSnapshot.getLong("commentCount");

                    commentCount =
                            comments != null ? comments.intValue() : 0;

                    Long timestamp =
                            documentSnapshot.getLong("timestamp");

                    if (timestamp != null) {

                        String formattedDate =
                                new SimpleDateFormat(
                                        "dd MMM yyyy, hh:mm a",
                                        Locale.getDefault()
                                ).format(
                                        new Date(timestamp)
                                );

                        tvPostTime.setText(formattedDate);
                    }

                    updateCount();

                    loadPostOwner();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            PostDetailsActivity.this,
                            "Failed to load post: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void loadPostOwner() {

        if (postOwnerId == null ||
                postOwnerId.trim().isEmpty()) {

            tvPostName.setText("Student");
            tvPostInfo.setText("");

            return;
        }

        firestore.collection("users")
                .document(postOwnerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        tvPostName.setText("Student");
                        tvPostInfo.setText("");

                        return;
                    }

                    String name =
                            documentSnapshot.getString("name");

                    String department =
                            documentSnapshot.getString("department");

                    String session =
                            documentSnapshot.getString("session");

                    if (name == null ||
                            name.trim().isEmpty()) {

                        name = "Student";
                    }

                    tvPostName.setText(name);

                    StringBuilder info =
                            new StringBuilder();

                    if (department != null &&
                            !department.trim().isEmpty()) {

                        info.append(department);
                    }

                    if (session != null &&
                            !session.trim().isEmpty()) {

                        if (info.length() > 0) {
                            info.append(" • ");
                        }

                        info.append(session);
                    }

                    if (info.length() > 0) {

                        tvPostInfo.setText(
                                info.toString()
                        );

                    } else {

                        tvPostInfo.setText("Student");
                    }

                })
                .addOnFailureListener(e -> {

                    tvPostName.setText("Student");
                    tvPostInfo.setText("Student");
                });
    }

    // =========================
    // LIKE / UNLIKE
    // =========================

    private void toggleLike() {

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

        btnLike.setEnabled(false);

        if (liked) {

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

            Map<String, Object> likeData =
                    new HashMap<>();

            likeData.put("userId", userId);
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

                                    createNotification(
                                            "like",
                                            "liked your post"
                                    );

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

    private void checkUserLike() {

        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        String userId =
                firebaseAuth.getCurrentUser().getUid();

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

    private void updateLikeButton() {

        if (liked) {
            btnLike.setText("❤️ Liked");
        } else {
            btnLike.setText("♡ Like");
        }
    }

    private void updateCount() {

        tvLikeCount.setText(
                likeCount + " Likes"
        );

        tvCommentCount.setText(
                commentCount + " Comments"
        );
    }

    // =========================
    // ADD COMMENT
    // =========================

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

        commentData.put("userId", userId);
        commentData.put("email", email);
        commentData.put("commentText", commentText);
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

                                createNotification(
                                        "comment",
                                        "commented on your post"
                                );

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
                            "Failed to add comment: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================
    // NOTIFICATION
    // =========================

    private void createNotification(
            String type,
            String action
    ) {

        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        if (postOwnerId == null ||
                postOwnerId.trim().isEmpty()) {
            return;
        }

        String currentUserId =
                firebaseAuth.getCurrentUser().getUid();

        if (postOwnerId.equals(currentUserId)) {
            return;
        }

        String currentUserEmail =
                firebaseAuth.getCurrentUser().getEmail();

        firestore.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(userDocument -> {

                    String senderName =
                            userDocument.getString("name");

                    if (senderName == null ||
                            senderName.trim().isEmpty()) {

                        senderName = currentUserEmail;
                    }

                    saveNotification(
                            senderName,
                            currentUserId,
                            type,
                            action
                    );

                })
                .addOnFailureListener(e -> {

                    saveNotification(
                            currentUserEmail,
                            currentUserId,
                            type,
                            action
                    );
                });
    }

    private void saveNotification(
            String senderName,
            String senderId,
            String type,
            String action
    ) {

        Map<String, Object> notification =
                new HashMap<>();

        notification.put("recipientId", postOwnerId);
        notification.put("senderId", senderId);
        notification.put("senderName", senderName);
        notification.put("type", type);
        notification.put(
                "message",
                senderName + " " + action
        );
        notification.put("postId", postId);
        notification.put(
                "timestamp",
                System.currentTimeMillis()
        );
        notification.put("read", false);

        firestore.collection("notifications")
                .add(notification);
    }

    // =========================
    // LOAD COMMENTS
    // =========================

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

                        String userId =
                                document.getString("userId");

                        String email =
                                document.getString("email");

                        String commentText =
                                document.getString("commentText");

                        if (commentText == null) {
                            commentText = "";
                        }

                        loadCommentUser(
                                userId,
                                email,
                                commentText
                        );
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            PostDetailsActivity.this,
                            "Failed to load comments: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void loadCommentUser(
            String userId,
            String email,
            String commentText
    ) {

        if (userId == null ||
                userId.trim().isEmpty()) {

            addCommentToUI(
                    email != null ? email : "Student",
                    commentText
            );

            return;
        }

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    String name =
                            documentSnapshot.getString("name");

                    if (name == null ||
                            name.trim().isEmpty()) {

                        name = email;
                    }

                    if (name == null ||
                            name.trim().isEmpty()) {

                        name = "Student";
                    }

                    addCommentToUI(
                            name,
                            commentText
                    );

                })
                .addOnFailureListener(e -> {

                    String name =
                            email != null
                                    ? email
                                    : "Student";

                    addCommentToUI(
                            name,
                            commentText
                    );
                });
    }

    private void addCommentToUI(
            String userNameText,
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

        TextView userName =
                new TextView(this);

        userName.setText(userNameText);
        userName.setTextSize(15);

        userName.setTextColor(
                getResources().getColor(
                        R.color.black
                )
        );

        userName.setTypeface(
                null,
                Typeface.BOLD
        );

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

    private void sharePost() {

        String text =
                tvPostName.getText().toString()
                        + "\n\n"
                        + tvPostText.getText().toString()
                        + "\n\n"
                        + "Shared from CampusConnect";

        Intent shareIntent =
                new Intent(Intent.ACTION_SEND);

        shareIntent.setType("text/plain");

        shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                text
        );

        startActivity(
                Intent.createChooser(
                        shareIntent,
                        "Share Post"
                )
        );
    }
}