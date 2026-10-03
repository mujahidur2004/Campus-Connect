package com.example.campusconnect;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditEventActivity extends AppCompatActivity {

    private EditText etEventTitle;
    private EditText etEventDate;
    private EditText etEventTime;
    private EditText etEventPlace;
    private EditText etEventDescription;

    private Button btnUpdateEvent;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDate = findViewById(R.id.etEventDate);
        etEventTime = findViewById(R.id.etEventTime);
        etEventPlace = findViewById(R.id.etEventPlace);
        etEventDescription = findViewById(R.id.etEventDescription);

        btnUpdateEvent = findViewById(R.id.btnUpdateEvent);

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

        loadEvent();

        btnUpdateEvent.setOnClickListener(
                v -> updateEvent()
        );
    }

    private void loadEvent() {

        firestore.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {
                        finish();
                        return;
                    }

                    String createdBy =
                            documentSnapshot.getString("createdBy");

                    if (auth.getCurrentUser() == null
                            || createdBy == null
                            || !createdBy.equals(
                            auth.getCurrentUser().getUid())) {

                        Toast.makeText(
                                this,
                                "You cannot edit this event",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                        return;
                    }

                    etEventTitle.setText(
                            documentSnapshot.getString("title")
                    );

                    etEventDate.setText(
                            documentSnapshot.getString("date")
                    );

                    etEventTime.setText(
                            documentSnapshot.getString("time")
                    );

                    etEventPlace.setText(
                            documentSnapshot.getString("place")
                    );

                    etEventDescription.setText(
                            documentSnapshot.getString("description")
                    );

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load event",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                });
    }

    private void updateEvent() {

        String title =
                etEventTitle.getText().toString().trim();

        String date =
                etEventDate.getText().toString().trim();

        String time =
                etEventTime.getText().toString().trim();

        String place =
                etEventPlace.getText().toString().trim();

        String description =
                etEventDescription.getText().toString().trim();


        if (title.isEmpty()) {
            etEventTitle.setError("Enter event title");
            return;
        }

        if (date.isEmpty()) {
            etEventDate.setError("Enter date");
            return;
        }

        if (time.isEmpty()) {
            etEventTime.setError("Enter time");
            return;
        }

        if (place.isEmpty()) {
            etEventPlace.setError("Enter place");
            return;
        }


        Map<String, Object> updateData =
                new HashMap<>();

        updateData.put("title", title);
        updateData.put("date", date);
        updateData.put("time", time);
        updateData.put("place", place);
        updateData.put("description", description);

        firestore.collection("events")
                .document(eventId)
                .update(updateData)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Event updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Update failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}