package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView tvEventTitle;
    private TextView tvEventDate;
    private TextView tvEventTime;
    private TextView tvEventPlace;
    private TextView tvEventDescription;
    private TextView tvInterestedCount;

    private Button btnInterested;
    private Button btnEditEvent;
    private Button btnDeleteEvent;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private String eventId;
    private String createdBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        tvEventTitle = findViewById(R.id.tvEventTitle);
        tvEventDate = findViewById(R.id.tvEventDate);
        tvEventTime = findViewById(R.id.tvEventTime);
        tvEventPlace = findViewById(R.id.tvEventPlace);
        tvEventDescription = findViewById(R.id.tvEventDescription);
        tvInterestedCount = findViewById(R.id.tvInterestedCount);

        btnInterested = findViewById(R.id.btnInterested);
        btnEditEvent = findViewById(R.id.btnEditEvent);
        btnDeleteEvent = findViewById(R.id.btnDeleteEvent);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        TextView tvBack = findViewById(R.id.tvBack);

        tvBack.setOnClickListener(v -> finish());

        eventId = getIntent().getStringExtra("event_id");

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(
                    this,
                    "Event not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        btnEditEvent.setVisibility(android.view.View.GONE);
        btnDeleteEvent.setVisibility(android.view.View.GONE);

        loadEvent();

        btnInterested.setOnClickListener(v -> toggleInterested());

        btnEditEvent.setOnClickListener(v -> {

            Intent intent = new Intent(
                    EventDetailsActivity.this,
                    EditEventActivity.class
            );

            intent.putExtra("event_id", eventId);

            startActivity(intent);
        });

        btnDeleteEvent.setOnClickListener(v -> deleteEvent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (eventId != null) {
            loadEvent();
        }
    }

    private void loadEvent() {

        firestore.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                this,
                                "Event does not exist",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                        return;
                    }

                    String title =
                            documentSnapshot.getString("title");

                    String date =
                            documentSnapshot.getString("date");

                    String time =
                            documentSnapshot.getString("time");

                    String place =
                            documentSnapshot.getString("place");

                    String description =
                            documentSnapshot.getString("description");

                    createdBy =
                            documentSnapshot.getString("createdBy");

                    tvEventTitle.setText(
                            title != null ? title : "Event"
                    );

                    tvEventDate.setText(
                            date != null
                                    ? date
                                    : "Date not available"
                    );

                    tvEventTime.setText(
                            time != null
                                    ? time
                                    : "Time not available"
                    );

                    tvEventPlace.setText(
                            place != null
                                    ? place
                                    : "Place not available"
                    );

                    tvEventDescription.setText(
                            description != null
                                    && !description.isEmpty()
                                    ? description
                                    : "No description available."
                    );

                    // Show edit/delete only to event creator
                    if (auth.getCurrentUser() != null
                            && createdBy != null
                            && createdBy.equals(
                            auth.getCurrentUser().getUid())) {

                        btnEditEvent.setVisibility(
                                android.view.View.VISIBLE
                        );

                        btnDeleteEvent.setVisibility(
                                android.view.View.VISIBLE
                        );
                    }

                    checkInterested();
                    loadInterestedCount();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load event: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =====================================================
    // INTERESTED
    // =====================================================

    private void checkInterested() {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        firestore.collection("events")
                .document(eventId)
                .collection("interested")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        btnInterested.setText(
                                "Interested ✓"
                        );

                    } else {

                        btnInterested.setText(
                                "Interested"
                        );
                    }
                });
    }

    private void toggleInterested() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        String userName =
                auth.getCurrentUser().getEmail();

        var interestRef =
                firestore.collection("events")
                        .document(eventId)
                        .collection("interested")
                        .document(userId);

        interestRef.get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        interestRef.delete()
                                .addOnSuccessListener(unused -> {

                                    btnInterested.setText(
                                            "Interested"
                                    );

                                    loadInterestedCount();

                                    Toast.makeText(
                                            this,
                                            "Interest removed",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                });

                    } else {

                        java.util.Map<String, Object> data =
                                new java.util.HashMap<>();

                        data.put("userId", userId);
                        data.put("email", userName);
                        data.put(
                                "timestamp",
                                System.currentTimeMillis()
                        );

                        interestRef.set(data)
                                .addOnSuccessListener(unused -> {

                                    btnInterested.setText(
                                            "Interested ✓"
                                    );

                                    loadInterestedCount();

                                    Toast.makeText(
                                            this,
                                            "You are interested",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                });
                    }
                });
    }

    private void loadInterestedCount() {

        firestore.collection("events")
                .document(eventId)
                .collection("interested")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    int count =
                            querySnapshot.size();

                    tvInterestedCount.setText(
                            count + " students interested"
                    );
                });
    }

    // =====================================================
    // DELETE EVENT
    // =====================================================

    private void deleteEvent() {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage(
                        "Are you sure you want to delete this event?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            firestore.collection("events")
                                    .document(eventId)
                                    .delete()
                                    .addOnSuccessListener(unused -> {

                                        Toast.makeText(
                                                this,
                                                "Event deleted",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        finish();
                                    })
                                    .addOnFailureListener(e -> {

                                        Toast.makeText(
                                                this,
                                                "Delete failed: "
                                                        + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    });
                        }
                )
                .show();
    }
}