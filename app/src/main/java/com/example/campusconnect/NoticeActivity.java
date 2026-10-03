package com.example.campusconnect;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NoticeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private LinearLayout noticeContainer;
    private Button btnAddNotice;
    private TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notice);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        noticeContainer =
                findViewById(R.id.noticeContainer);

        btnAddNotice =
                findViewById(R.id.btnAddNotice);

        tvBack =
                findViewById(R.id.tvBack);

        tvBack.setOnClickListener(v -> finish());

        btnAddNotice.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            NoticeActivity.this,
                            AddNoticeActivity.class
                    );

            startActivity(intent);
        });

        // IMPORTANT:
        // loadNotices() এখানে নেই।
        // onResume() থেকে load হবে।
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (auth != null &&
                auth.getCurrentUser() != null) {

            loadNotices();
        }
    }

    private void loadNotices() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        noticeContainer.removeAllViews();

        db.collection("notices")
                .orderBy(
                        "timestamp",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            if (queryDocumentSnapshots.isEmpty()) {

                                TextView emptyText =
                                        new TextView(this);

                                emptyText.setText(
                                        "No notices available"
                                );

                                emptyText.setTextSize(16);

                                emptyText.setGravity(
                                        Gravity.CENTER
                                );

                                emptyText.setPadding(
                                        20,
                                        40,
                                        20,
                                        40
                                );

                                noticeContainer.addView(
                                        emptyText
                                );

                                return;
                            }

                            for (var document :
                                    queryDocumentSnapshots) {

                                String noticeId =
                                        document.getId();

                                String title =
                                        document.getString("title");

                                String category =
                                        document.getString("category");

                                String description =
                                        document.getString(
                                                "description"
                                        );

                                Boolean importantValue =
                                        document.getBoolean(
                                                "important"
                                        );

                                boolean important =
                                        importantValue != null
                                                && importantValue;

                                Long timestamp =
                                        document.getLong(
                                                "timestamp"
                                        );

                                if (title == null) {
                                    title = "Untitled Notice";
                                }

                                if (category == null) {
                                    category = "General";
                                }

                                if (description == null) {
                                    description = "";
                                }

                                createNoticeCard(
                                        noticeId,
                                        title,
                                        category,
                                        description,
                                        important,
                                        timestamp
                                );
                            }

                        }
                )
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load notices: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void createNoticeCard(
            String noticeId,
            String title,
            String category,
            String description,
            boolean important,
            Long timestamp
    ) {

        CardView cardView =
                new CardView(this);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                16,
                10,
                16,
                10
        );

        cardView.setLayoutParams(cardParams);

        cardView.setRadius(18);
        cardView.setCardElevation(5);

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                20,
                20,
                20,
                20
        );

        TextView tvTitle =
                new TextView(this);

        tvTitle.setText(title);
        tvTitle.setTextSize(19);
        tvTitle.setTypeface(
                null,
                Typeface.BOLD
        );

        layout.addView(tvTitle);

        TextView tvCategory =
                new TextView(this);

        String categoryText =
                "Category: " + category;

        if (important) {
            categoryText +=
                    "  •  IMPORTANT";
        }

        tvCategory.setText(categoryText);
        tvCategory.setTextSize(14);

        tvCategory.setPadding(
                0,
                8,
                0,
                8
        );

        layout.addView(tvCategory);

        TextView tvDescription =
                new TextView(this);

        String shortDescription =
                description;

        if (shortDescription.length() > 120) {

            shortDescription =
                    shortDescription.substring(
                            0,
                            120
                    ) + "...";
        }

        tvDescription.setText(
                shortDescription
        );

        tvDescription.setTextSize(15);

        layout.addView(tvDescription);

        Button btnDetails =
                new Button(this);

        btnDetails.setText(
                "View Details"
        );

        btnDetails.setAllCaps(false);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        buttonParams.gravity =
                Gravity.END;

        btnDetails.setLayoutParams(
                buttonParams
        );

        btnDetails.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            NoticeActivity.this,
                            NoticeDetailsActivity.class
                    );

            intent.putExtra(
                    "notice_id",
                    noticeId
            );

            startActivity(intent);
        });

        layout.addView(btnDetails);

        cardView.addView(layout);

        noticeContainer.addView(cardView);
    }
}