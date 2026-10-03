package com.example.campusconnect;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    // =========================
    // UI
    // =========================

    private TextView tvBack;
    private TextView tvUserName;

    private EditText etMessage;
    private Button btnSend;

    private LinearLayout messageContainer;

    // =========================
    // Firebase
    // =========================

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    // =========================
    // User information
    // =========================

    private String currentUserId;
    private String receiverId;
    private String receiverName;

    // =========================
    // Firestore listener
    // =========================

    private ListenerRegistration messageListener;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        // =========================
        // Firebase
        // =========================

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // =========================
        // Check login
        // =========================

        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        currentUserId =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();


        // =========================
        // Find Views
        // =========================

        tvBack = findViewById(
                R.id.tvBack
        );

        tvUserName = findViewById(
                R.id.tvUserName
        );

        etMessage = findViewById(
                R.id.etMessage
        );

        btnSend = findViewById(
                R.id.btnSend
        );

        messageContainer = findViewById(
                R.id.messageContainer
        );


        // =====================================================
        // GET SELECTED USER
        // =====================================================

        receiverId =
                getIntent().getStringExtra(
                        "user_id"
                );

        receiverName =
                getIntent().getStringExtra(
                        "user_name"
                );


        // =========================
        // Check Receiver
        // =========================

        if (
                receiverId == null
                        ||
                        receiverId.trim().isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Student information not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }


        // =========================
        // Default Name
        // =========================

        if (
                receiverName == null
                        ||
                        receiverName.trim().isEmpty()
        ) {

            receiverName = "Student";
        }


        tvUserName.setText(
                receiverName
        );


        // =====================================================
        // BACK
        // =====================================================

        tvBack.setOnClickListener(
                v -> finish()
        );


        // =====================================================
        // LOAD REAL-TIME MESSAGES
        // =====================================================

        listenForMessages();


        // =====================================================
        // SEND MESSAGE
        // =====================================================

        btnSend.setOnClickListener(
                v -> {

                    String message =
                            etMessage
                                    .getText()
                                    .toString()
                                    .trim();

                    if (message.isEmpty()) {

                        etMessage.setError(
                                "Write a message"
                        );

                        etMessage.requestFocus();

                        return;
                    }

                    sendMessage(
                            message
                    );
                }
        );
    }


    // =========================================================
    // CREATE CHAT ID
    // =========================================================

    private String getChatId() {

        /*
         * Same two users will always
         * generate the same chat ID.
         */

        if (
                currentUserId.compareTo(
                        receiverId
                ) < 0
        ) {

            return currentUserId
                    + "_"
                    + receiverId;

        } else {

            return receiverId
                    + "_"
                    + currentUserId;
        }
    }


    // =========================================================
    // SEND MESSAGE
    // =========================================================

    private void sendMessage(
            String messageText
    ) {

        btnSend.setEnabled(false);

        String chatId =
                getChatId();


        // =========================
        // Message Data
        // =========================

        Map<String, Object> message =
                new HashMap<>();

        message.put(
                "senderId",
                currentUserId
        );

        message.put(
                "receiverId",
                receiverId
        );

        message.put(
                "message",
                messageText
        );

        message.put(
                "timestamp",
                System.currentTimeMillis()
        );


        // =====================================================
        // SAVE TO FIRESTORE
        // =====================================================

        firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(
                        documentReference -> {

                            // Clear input
                            etMessage.setText("");

                            btnSend.setEnabled(true);
                        }
                )
                .addOnFailureListener(
                        e -> {

                            btnSend.setEnabled(true);

                            Toast.makeText(
                                    ChatActivity.this,
                                    "Failed to send message: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }


    // =========================================================
    // REAL-TIME MESSAGE LISTENER
    // =========================================================

    private void listenForMessages() {

        String chatId =
                getChatId();


        messageListener =
                firestore.collection("chats")
                        .document(chatId)
                        .collection("messages")
                        .orderBy(
                                "timestamp",
                                Query.Direction.ASCENDING
                        )
                        .addSnapshotListener(
                                (value, error) -> {

                                    if (error != null) {

                                        Toast.makeText(
                                                ChatActivity.this,
                                                "Failed to load messages: "
                                                        + error.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();

                                        return;
                                    }


                                    if (value == null) {
                                        return;
                                    }


                                    // Clear old messages
                                    messageContainer
                                            .removeAllViews();


                                    // =========================
                                    // Add Messages
                                    // =========================

                                    for (
                                            DocumentSnapshot document
                                            :
                                            value.getDocuments()
                                    ) {

                                        String senderId =
                                                document.getString(
                                                        "senderId"
                                                );

                                        String messageText =
                                                document.getString(
                                                        "message"
                                                );


                                        if (
                                                messageText == null
                                        ) {
                                            continue;
                                        }


                                        boolean isMyMessage =
                                                currentUserId.equals(
                                                        senderId
                                                );


                                        addMessage(
                                                messageText,
                                                isMyMessage
                                        );
                                    }


                                    // Scroll to bottom
                                    messageContainer.post(
                                            () -> {

                                                View parent =
                                                        (View)
                                                                messageContainer
                                                                        .getParent();

                                                if (parent instanceof android.widget.ScrollView) {

                                                    android.widget.ScrollView scrollView =
                                                            (android.widget.ScrollView)
                                                                    parent;

                                                    scrollView.fullScroll(
                                                            View.FOCUS_DOWN
                                                    );
                                                }
                                            }
                                    );
                                }
                        );
    }


    // =========================================================
    // ADD MESSAGE TO UI
    // =========================================================

    private void addMessage(
            String message,
            boolean isMyMessage
    ) {

        TextView newMessage =
                new TextView(this);


        newMessage.setText(
                message
        );

        newMessage.setTextSize(
                15
        );

        newMessage.setPadding(
                18,
                12,
                18,
                12
        );


        // =====================================================
        // MY MESSAGE
        // =====================================================

        if (isMyMessage) {

            newMessage.setTextColor(
                    getResources().getColor(
                            R.color.white
                    )
            );

            newMessage.setBackgroundColor(
                    getResources().getColor(
                            R.color.app_name
                    )
            );

        }

        // =====================================================
        // OTHER USER MESSAGE
        // =====================================================

        else {

            newMessage.setTextColor(
                    getResources().getColor(
                            R.color.black
                    )
            );

            newMessage.setBackgroundColor(
                    0xFFEFEFEF
            );
        }


        // =====================================================
        // LAYOUT
        // =====================================================

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


        // My message → right
        if (isMyMessage) {

            params.gravity =
                    Gravity.END;

            params.setMargins(
                    60,
                    8,
                    10,
                    8
            );

        }

        // Other message → left
        else {

            params.gravity =
                    Gravity.START;

            params.setMargins(
                    10,
                    8,
                    60,
                    8
            );
        }


        newMessage.setLayoutParams(
                params
        );


        messageContainer.addView(
                newMessage
        );
    }


    // =========================================================
    // REMOVE LISTENER
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (
                messageListener != null
        ) {

            messageListener.remove();
        }
    }
}