package com.example.campusconnect;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class NoticeDetailsActivity extends AppCompatActivity {

    private TextView tvBack;
    private TextView tvNoticeTitle;
    private TextView tvNoticeCategory;
    private TextView tvNoticeDescription;
    private TextView tvNoticeImportant;
    private TextView tvNoticeCreator;

    private Button btnEditNotice;
    private Button btnDeleteNotice;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String noticeId;
    private String createdBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_details);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        noticeId = getIntent().getStringExtra("notice_id");

        tvBack = findViewById(R.id.tvBack);
        tvNoticeTitle = findViewById(R.id.tvNoticeTitle);
        tvNoticeCategory = findViewById(R.id.tvNoticeCategory);
        tvNoticeDescription = findViewById(R.id.tvNoticeDescription);
        tvNoticeImportant = findViewById(R.id.tvNoticeImportant);
        tvNoticeCreator = findViewById(R.id.tvNoticeCreator);

        btnEditNotice = findViewById(R.id.btnEditNotice);
        btnDeleteNotice = findViewById(R.id.btnDeleteNotice);

        tvBack.setOnClickListener(v -> finish());

        if (noticeId == null || noticeId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Notice not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        btnEditNotice.setVisibility(android.view.View.GONE);
        btnDeleteNotice.setVisibility(android.view.View.GONE);

        loadNotice();

        btnEditNotice.setOnClickListener(v -> {

            Intent intent = new Intent(
                    NoticeDetailsActivity.this,
                    EditNoticeActivity.class
            );

            intent.putExtra("notice_id", noticeId);

            startActivity(intent);
        });

        btnDeleteNotice.setOnClickListener(v -> showDeleteDialog());
    }

    private void loadNotice() {

        db.collection("notices")
                .document(noticeId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                this,
                                "Notice does not exist",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                        return;
                    }

                    String title =
                            documentSnapshot.getString("title");

                    String category =
                            documentSnapshot.getString("category");

                    String description =
                            documentSnapshot.getString("description");

                    String creator =
                            documentSnapshot.getString("creatorName");

                    Boolean importantValue =
                            documentSnapshot.getBoolean("important");

                    boolean important =
                            importantValue != null && importantValue;

                    createdBy =
                            documentSnapshot.getString("createdBy");

                    if (title == null) title = "Untitled Notice";
                    if (category == null) category = "General";
                    if (description == null) description = "";
                    if (creator == null) creator = "Unknown";

                    tvNoticeTitle.setText(title);
                    tvNoticeCategory.setText("Category: " + category);
                    tvNoticeDescription.setText(description);
                    tvNoticeCreator.setText("Published by: " + creator);

                    if (important) {
                        tvNoticeImportant.setText("⚠ IMPORTANT NOTICE");
                    } else {
                        tvNoticeImportant.setText("Regular Notice");
                    }

                    checkOwner();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load notice: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void checkOwner() {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String currentUserId =
                auth.getCurrentUser().getUid();

        if (createdBy != null &&
                createdBy.equals(currentUserId)) {

            btnEditNotice.setVisibility(android.view.View.VISIBLE);
            btnDeleteNotice.setVisibility(android.view.View.VISIBLE);
        }
    }

    private void showDeleteDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Delete Notice")
                .setMessage(
                        "Are you sure you want to delete this notice?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> deleteNotice()
                )
                .show();
    }

    private void deleteNotice() {

        db.collection("notices")
                .document(noticeId)
                .delete()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Notice deleted",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Delete failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (noticeId != null && !noticeId.isEmpty()) {
            loadNotice();
        }
    }
}