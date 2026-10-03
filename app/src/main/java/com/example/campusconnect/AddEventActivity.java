package com.example.campusconnect;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    private EditText etEventTitle;
    private EditText etEventDate;
    private EditText etEventTime;
    private EditText etEventPlace;
    private EditText etEventDescription;

    private Button btnSaveEvent;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDate = findViewById(R.id.etEventDate);
        etEventTime = findViewById(R.id.etEventTime);
        etEventPlace = findViewById(R.id.etEventPlace);
        etEventDescription = findViewById(R.id.etEventDescription);

        btnSaveEvent = findViewById(R.id.btnSaveEvent);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnSaveEvent.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {

        String title = etEventTitle.getText().toString().trim();
        String date = etEventDate.getText().toString().trim();
        String time = etEventTime.getText().toString().trim();
        String place = etEventPlace.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();

        if (title.isEmpty()) {
            etEventTitle.setError("Enter event title");
            etEventTitle.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            etEventDate.setError("Enter event date");
            etEventDate.requestFocus();
            return;
        }

        if (time.isEmpty()) {
            etEventTime.setError("Enter event time");
            etEventTime.requestFocus();
            return;
        }

        if (place.isEmpty()) {
            etEventPlace.setError("Enter event place");
            etEventPlace.requestFocus();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> event = new HashMap<>();

        event.put("title", title);
        event.put("date", date);
        event.put("time", time);
        event.put("place", place);
        event.put("description", description);
        event.put("createdBy", userId);
        event.put("timestamp", System.currentTimeMillis());

        firestore.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            this,
                            "Event added successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to add event: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}