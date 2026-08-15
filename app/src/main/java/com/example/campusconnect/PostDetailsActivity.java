package com.example.campusconnect;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    private int likeCount = 12;
    private int commentCount = 5;

    private boolean liked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvBack = findViewById(R.id.tvBack);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvCommentCount = findViewById(R.id.tvCommentCount);

        btnLike = findViewById(R.id.btnLike);
        btnComment = findViewById(R.id.btnComment);
        btnShare = findViewById(R.id.btnShare);
        btnSendComment = findViewById(R.id.btnSendComment);

        etComment = findViewById(R.id.etComment);
        commentContainer = findViewById(R.id.commentContainer);

        updateCount();

        // Back
        tvBack.setOnClickListener(v -> finish());

        // Like
        btnLike.setOnClickListener(v -> {

            if (liked) {
                liked = false;
                likeCount--;

                btnLike.setText("♡ Like");

            } else {
                liked = true;
                likeCount++;

                btnLike.setText("❤️ Liked");
            }

            updateCount();
        });

        // Comment
        btnComment.setOnClickListener(v -> {

            etComment.requestFocus();

        });

        // Share
        btnShare.setOnClickListener(v -> {

            android.content.Intent shareIntent =
                    new android.content.Intent(
                            android.content.Intent.ACTION_SEND
                    );

            shareIntent.setType("text/plain");

            shareIntent.putExtra(
                    android.content.Intent.EXTRA_TEXT,
                    "Check out this post on CampusConnect!"
            );

            startActivity(
                    android.content.Intent.createChooser(
                            shareIntent,
                            "Share Post"
                    )
            );
        });

        // Send Comment
        btnSendComment.setOnClickListener(v -> {

            String comment = etComment
                    .getText()
                    .toString()
                    .trim();

            if (comment.isEmpty()) {

                etComment.setError("Write a comment");
                etComment.requestFocus();

                return;
            }

            addComment(comment);

            commentCount++;

            updateCount();

            etComment.setText("");
        });
    }

    private void updateCount() {

        tvLikeCount.setText(likeCount + " Likes");
        tvCommentCount.setText(commentCount + " Comments");
    }

    private void addComment(String commentText) {

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

        userName.setText("Md Mujahidur Rahman");
        userName.setTextSize(15);
        userName.setTextColor(
                getResources().getColor(R.color.black)
        );
        userName.setTypeface(null, 1);

        TextView comment =
                new TextView(this);

        comment.setText(commentText);
        comment.setTextSize(14);
        comment.setTextColor(
                getResources().getColor(android.R.color.darker_gray)
        );

        comment.setPadding(0, 5, 0, 0);

        commentLayout.addView(userName);
        commentLayout.addView(comment);

        commentContainer.addView(
                commentLayout,
                0
        );
    }
}