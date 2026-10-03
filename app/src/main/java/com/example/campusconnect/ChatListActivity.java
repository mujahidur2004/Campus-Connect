package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    // =========================
    // UI
    // =========================

    private TextView tvBack;
    private EditText etSearchChat;
    private LinearLayout chatContainer;

    // =========================
    // Firebase
    // =========================

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    // =========================
    // User List
    // =========================

    private final List<DocumentSnapshot> allUsers =
            new ArrayList<>();


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_list);

        // =========================
        // Firebase
        // =========================

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // =========================
        // Find Views
        // =========================

        tvBack = findViewById(R.id.tvBack);

        etSearchChat = findViewById(
                R.id.etSearchChat
        );

        chatContainer = findViewById(
                R.id.chatContainer
        );

        // =========================
        // Back Button
        // =========================

        tvBack.setOnClickListener(v -> finish());

        // =========================
        // Load Users
        // =========================

        loadUsers();

        // =========================
        // Search
        // =========================

        etSearchChat.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        filterUsers(
                                s.toString().trim()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );
    }


    // =========================================================
    // LOAD USERS FROM FIRESTORE
    // =========================================================

    private void loadUsers() {

        // Check login
        if (firebaseAuth.getCurrentUser() == null) {

            showMessage(
                    "Please login first"
            );

            return;
        }

        // Current user UID
        String currentUserId =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();

        firestore.collection("users")
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            allUsers.clear();

                            for (
                                    DocumentSnapshot document
                                    :
                                    queryDocumentSnapshots
                                            .getDocuments()
                            ) {

                                String userId =
                                        document.getString(
                                                "userId"
                                        );

                                // =========================
                                // Don't show current user
                                // =========================

                                if (
                                        userId != null
                                                &&
                                                userId.equals(
                                                        currentUserId
                                                )
                                ) {
                                    continue;
                                }

                                allUsers.add(
                                        document
                                );
                            }

                            displayUsers(
                                    allUsers
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            showMessage(
                                    "Failed to load students\n"
                                            + e.getMessage()
                            );
                        }
                );
    }


    // =========================================================
    // DISPLAY USERS
    // =========================================================

    private void displayUsers(
            List<DocumentSnapshot> users
    ) {

        // Clear old list
        chatContainer.removeAllViews();

        // No users
        if (users.isEmpty()) {

            showMessage(
                    "No students found"
            );

            return;
        }

        // Add every user
        for (
                DocumentSnapshot document
                :
                users
        ) {

            createUserItem(
                    document
            );
        }
    }


    // =========================================================
    // CREATE USER ITEM
    // =========================================================

    private void createUserItem(
            DocumentSnapshot document
    ) {

        // =========================
        // Get User Data
        // =========================

        String userId =
                document.getString(
                        "userId"
                );

        String name =
                document.getString(
                        "name"
                );

        String department =
                document.getString(
                        "department"
                );

        String session =
                document.getString(
                        "session"
                );

        String email =
                document.getString(
                        "email"
                );

        // =========================
        // Default Name
        // =========================

        if (
                name == null
                        ||
                        name.trim().isEmpty()
        ) {

            name = "Student";
        }


        // =====================================================
        // MAIN ROW
        // =====================================================

        LinearLayout row =
                new LinearLayout(
                        this
                );

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        row.setPadding(
                15,
                10,
                15,
                10
        );

        row.setClickable(true);

        row.setFocusable(true);

        // =========================
        // Row Layout Params
        // =========================

        row.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        80
                )
        );


        // =====================================================
        // AVATAR
        // =====================================================

        TextView avatar =
                new TextView(
                        this
                );

        avatar.setLayoutParams(
                new LinearLayout.LayoutParams(
                        52,
                        52
                )
        );

        avatar.setGravity(
                Gravity.CENTER
        );

        avatar.setText(
                "👤"
        );

        avatar.setTextSize(
                27
        );


        // =====================================================
        // USER INFO CONTAINER
        // =====================================================

        LinearLayout info =
                new LinearLayout(
                        this
                );

        info.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        infoParams.setMargins(
                12,
                0,
                10,
                0
        );

        info.setLayoutParams(
                infoParams
        );


        // =====================================================
        // USER NAME
        // =====================================================

        TextView nameText =
                new TextView(
                        this
                );

        nameText.setText(
                name
        );

        nameText.setTextSize(
                16
        );

        nameText.setTextColor(
                getResources().getColor(
                        R.color.black
                )
        );

        nameText.setTypeface(
                null,
                Typeface.BOLD
        );


        // =====================================================
        // USER DETAILS
        // =====================================================

        TextView detailsText =
                new TextView(
                        this
                );

        StringBuilder details =
                new StringBuilder();


        // Department
        if (
                department != null
                        &&
                        !department.trim().isEmpty()
        ) {

            details.append(
                    department
            );
        }


        // Session
        if (
                session != null
                        &&
                        !session.trim().isEmpty()
        ) {

            if (
                    details.length() > 0
            ) {

                details.append(
                        " • "
                );
            }

            details.append(
                    session
            );
        }


        // Email fallback
        if (
                details.length() == 0
                        &&
                        email != null
                        &&
                        !email.trim().isEmpty()
        ) {

            details.append(
                    email
            );
        }


        detailsText.setText(
                details.toString()
        );

        detailsText.setTextSize(
                13
        );

        detailsText.setTextColor(
                getResources().getColor(
                        android.R.color.darker_gray
                )
        );

        detailsText.setPadding(
                0,
                4,
                0,
                0
        );


        // Add information
        info.addView(
                nameText
        );

        info.addView(
                detailsText
        );


        // =====================================================
        // ARROW
        // =====================================================

        TextView arrow =
                new TextView(
                        this
                );

        arrow.setLayoutParams(
                new LinearLayout.LayoutParams(
                        35,
                        55
                )
        );

        arrow.setGravity(
                Gravity.CENTER
        );

        arrow.setText(
                "›"
        );

        arrow.setTextSize(
                30
        );

        arrow.setTextColor(
                getResources().getColor(
                        android.R.color.darker_gray
                )
        );


        // =====================================================
        // ADD TO ROW
        // =====================================================

        row.addView(
                avatar
        );

        row.addView(
                info
        );

        row.addView(
                arrow
        );


        // =====================================================
        // CLICK USER
        // =====================================================

        String finalName =
                name;

        String finalEmail =
                email;

        String finalUserId =
                userId;

        row.setOnClickListener(
                v -> openChat(
                        finalUserId,
                        finalName,
                        finalEmail
                )
        );


        // =====================================================
        // ADD ROW
        // =====================================================

        chatContainer.addView(
                row
        );


        // =====================================================
        // DIVIDER
        // =====================================================

        View divider =
                new View(
                        this
                );

        divider.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                )
        );

        divider.setBackgroundColor(
                0xFFEEEEEE
        );

        chatContainer.addView(
                divider
        );
    }


    // =========================================================
    // SEARCH USERS
    // =========================================================

    private void filterUsers(
            String search
    ) {

        // Empty search
        if (
                search.isEmpty()
        ) {

            displayUsers(
                    allUsers
            );

            return;
        }


        List<DocumentSnapshot> filteredUsers =
                new ArrayList<>();

        String query =
                search.toLowerCase();


        // Search every user
        for (
                DocumentSnapshot document
                :
                allUsers
        ) {

            String name =
                    document.getString(
                            "name"
                    );

            String department =
                    document.getString(
                            "department"
                    );

            String email =
                    document.getString(
                            "email"
                    );


            if (name == null) {
                name = "";
            }

            if (department == null) {
                department = "";
            }

            if (email == null) {
                email = "";
            }


            // Match
            if (
                    name.toLowerCase()
                            .contains(query)

                            ||

                            department.toLowerCase()
                                    .contains(query)

                            ||

                            email.toLowerCase()
                                    .contains(query)
            ) {

                filteredUsers.add(
                        document
                );
            }
        }


        displayUsers(
                filteredUsers
        );
    }


    // =========================================================
    // OPEN CHAT
    // =========================================================

    private void openChat(
            String userId,
            String userName,
            String email
    ) {

        // Check user ID
        if (
                userId == null
                        ||
                        userId.trim().isEmpty()
        ) {

            showMessage(
                    "User ID not found"
            );

            return;
        }


        Intent intent =
                new Intent(
                        ChatListActivity.this,
                        ChatActivity.class
                );


        // Selected user UID
        intent.putExtra(
                "user_id",
                userId
        );


        // Selected user name
        intent.putExtra(
                "user_name",
                userName
        );


        // Selected user email
        intent.putExtra(
                "user_email",
                email
        );


        startActivity(
                intent
        );
    }


    // =========================================================
    // SHOW MESSAGE
    // =========================================================

    private void showMessage(
            String message
    ) {

        chatContainer.removeAllViews();

        TextView text =
                new TextView(
                        this
                );

        text.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        text.setText(
                message
        );

        text.setTextSize(
                15
        );

        text.setTextColor(
                getResources().getColor(
                        android.R.color.darker_gray
                )
        );

        text.setGravity(
                Gravity.CENTER
        );

        text.setPadding(
                20,
                40,
                20,
                40
        );

        chatContainer.addView(
                text
        );
    }
}