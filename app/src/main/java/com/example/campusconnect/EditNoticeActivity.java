package com.example.campusconnect;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditNoticeActivity extends AppCompatActivity {

    private EditText etNoticeTitle;
    private EditText etNoticeCategory;
    private EditText etNoticeDescription;

    private Switch switchImportant;

    private Button btnUpdateNotice;
    private TextView tvBack;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String noticeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notice);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        noticeId = getIntent().getStringExtra("notice_id");

        etNoticeTitle = findViewById(R.id.etNoticeTitle);
        etNoticeCategory = findViewById(R.id.etNoticeCategory);
        etNoticeDescription = findViewById(R.id.etNoticeDescription);

        switchImportant = findViewById(R.id.switchImportant);

        btnUpdateNotice = findViewById(R.id.btnUpdateNotice);
        tvBack = findViewById(R.id.tvBack);

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

        loadNotice();

        btnUpdateNotice.setOnClickListener(v -> updateNotice());
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

                    String createdBy =
                            documentSnapshot.getString("createdBy");

                    if (auth.getCurrentUser() == null ||
                            createdBy == null ||
                            !createdBy.equals(
                                    auth.getCurrentUser().getUid()
                            )) {

                        Toast.makeText(
                                this,
                                "You cannot edit this notice",
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

                    Boolean important =
                            documentSnapshot.getBoolean("important");

                    etNoticeTitle.setText(title);
                    etNoticeCategory.setText(category);
                    etNoticeDescription.setText(description);

                    if (important != null) {
                        switchImportant.setChecked(important);
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load notice: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void updateNotice() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String title =
                etNoticeTitle.getText().toString().trim();

        String category =
                etNoticeCategory.getText().toString().trim();

        String description =
                etNoticeDescription.getText().toString().trim();

        boolean important =
                switchImportant.isChecked();

        if (title.isEmpty()) {
            etNoticeTitle.setError("Enter notice title");
            etNoticeTitle.requestFocus();
            return;
        }

        if (category.isEmpty()) {
            etNoticeCategory.setError("Enter category");
            etNoticeCategory.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            etNoticeDescription.setError(
                    "Enter notice description"
            );
            etNoticeDescription.requestFocus();
            return;
        }

        Map<String, Object> updates = new HashMap<>();

        updates.put("title", title);
        updates.put("category", category);
        updates.put("description", description);
        updates.put("important", important);

        db.collection("notices")
                .document(noticeId)
                .update(updates)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Notice updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Update failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}