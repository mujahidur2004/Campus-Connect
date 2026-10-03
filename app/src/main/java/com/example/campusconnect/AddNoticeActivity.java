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

public class AddNoticeActivity extends AppCompatActivity {

    private EditText etNoticeTitle;
    private EditText etNoticeCategory;
    private EditText etNoticeDescription;

    private Switch switchImportant;

    private Button btnSaveNotice;
    private TextView tvBack;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etNoticeTitle = findViewById(R.id.etNoticeTitle);
        etNoticeCategory = findViewById(R.id.etNoticeCategory);
        etNoticeDescription = findViewById(R.id.etNoticeDescription);

        switchImportant = findViewById(R.id.switchImportant);

        btnSaveNotice = findViewById(R.id.btnSaveNotice);
        tvBack = findViewById(R.id.tvBack);

        tvBack.setOnClickListener(v -> finish());

        btnSaveNotice.setOnClickListener(v -> saveNotice());
    }

    private void saveNotice() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String title = etNoticeTitle.getText().toString().trim();
        String category = etNoticeCategory.getText().toString().trim();
        String description = etNoticeDescription.getText().toString().trim();

        boolean important = switchImportant.isChecked();

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
            etNoticeDescription.setError("Enter notice description");
            etNoticeDescription.requestFocus();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        String email = auth.getCurrentUser().getEmail();

        Map<String, Object> notice = new HashMap<>();

        notice.put("title", title);
        notice.put("category", category);
        notice.put("description", description);
        notice.put("important", important);
        notice.put("createdBy", userId);
        notice.put("creatorName", email);
        notice.put("timestamp", System.currentTimeMillis());

        db.collection("notices")
                .add(notice)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            AddNoticeActivity.this,
                            "Notice published successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AddNoticeActivity.this,
                            "Failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}