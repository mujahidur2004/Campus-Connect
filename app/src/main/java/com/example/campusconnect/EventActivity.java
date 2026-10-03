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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EventActivity extends AppCompatActivity {

    private LinearLayout eventContainer;
    private Button btnAddEvent;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event);

        eventContainer =
                findViewById(R.id.eventContainer);

        btnAddEvent =
                findViewById(R.id.btnAddEvent);

        firestore =
                FirebaseFirestore.getInstance();

        btnAddEvent.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            EventActivity.this,
                            AddEventActivity.class
                    );

            startActivity(intent);
        });

        // IMPORTANT:
        // loadEvents() এখানে নেই।
        // onResume() থেকে load হবে।
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadEvents();
    }

    private void loadEvents() {

        eventContainer.removeAllViews();

        firestore.collection("events")
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
                                        "No events available"
                                );

                                emptyText.setTextSize(17);

                                emptyText.setTextColor(
                                        getResources()
                                                .getColor(
                                                        android.R.color
                                                                .darker_gray
                                                )
                                );

                                emptyText.setGravity(
                                        Gravity.CENTER
                                );

                                emptyText.setPadding(
                                        20,
                                        50,
                                        20,
                                        50
                                );

                                eventContainer.addView(
                                        emptyText
                                );

                                return;
                            }

                            queryDocumentSnapshots.forEach(
                                    documentSnapshot -> {

                                        String eventId =
                                                documentSnapshot.getId();

                                        String title =
                                                documentSnapshot.getString(
                                                        "title"
                                                );

                                        String date =
                                                documentSnapshot.getString(
                                                        "date"
                                                );

                                        String time =
                                                documentSnapshot.getString(
                                                        "time"
                                                );

                                        String place =
                                                documentSnapshot.getString(
                                                        "place"
                                                );

                                        if (title == null ||
                                                title.isEmpty()) {

                                            title =
                                                    "Untitled Event";
                                        }

                                        if (date == null ||
                                                date.isEmpty()) {

                                            date =
                                                    "Date not available";
                                        }

                                        if (time == null ||
                                                time.isEmpty()) {

                                            time =
                                                    "Time not available";
                                        }

                                        if (place == null ||
                                                place.isEmpty()) {

                                            place =
                                                    "Place not available";
                                        }

                                        addEventCard(
                                                eventId,
                                                title,
                                                date,
                                                time,
                                                place
                                        );
                                    }
                            );

                        }
                )
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            EventActivity.this,
                            "Failed to load events: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void addEventCard(
            String eventId,
            String title,
            String date,
            String time,
            String place
    ) {

        CardView cardView =
                new CardView(this);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                12
        );

        cardView.setLayoutParams(
                cardParams
        );

        cardView.setRadius(12);
        cardView.setCardElevation(3);

        LinearLayout cardLayout =
                new LinearLayout(this);

        cardLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        cardLayout.setPadding(
                15,
                15,
                15,
                15
        );

        TextView tvTitle =
                new TextView(this);

        tvTitle.setText(title);
        tvTitle.setTextSize(18);

        tvTitle.setTextColor(
                getResources().getColor(
                        android.R.color.black
                )
        );

        tvTitle.setTypeface(
                null,
                Typeface.BOLD
        );

        TextView tvDate =
                new TextView(this);

        tvDate.setText(
                date + " | " + time
        );

        tvDate.setTextSize(14);

        tvDate.setTextColor(
                getResources().getColor(
                        android.R.color.darker_gray
                )
        );

        TextView tvPlace =
                new TextView(this);

        tvPlace.setText(
                "Place : " + place
        );

        tvPlace.setTextSize(15);

        tvPlace.setTextColor(
                getResources().getColor(
                        android.R.color.black
                )
        );

        tvPlace.setPadding(
                0,
                8,
                0,
                8
        );

        Button btnDetails =
                new Button(this);

        btnDetails.setText(
                "View Details"
        );

        btnDetails.setAllCaps(false);

        btnDetails.setTextColor(
                getResources().getColor(
                        android.R.color.white
                )
        );

        btnDetails.setBackgroundTintList(
                getResources().getColorStateList(
                        R.color.app_name
                )
        );

        btnDetails.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            EventActivity.this,
                            EventDetailsActivity.class
                    );

            intent.putExtra(
                    "event_id",
                    eventId
            );

            startActivity(intent);
        });

        cardLayout.addView(tvTitle);
        cardLayout.addView(tvDate);
        cardLayout.addView(tvPlace);
        cardLayout.addView(btnDetails);

        cardView.addView(cardLayout);

        eventContainer.addView(cardView);
    }
}