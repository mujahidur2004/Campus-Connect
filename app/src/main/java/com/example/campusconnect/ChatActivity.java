package com.example.campusconnect;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    private TextView tvBack;
    private TextView tvUserName;

    private EditText etMessage;
    private Button btnSend;

    private LinearLayout messageContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvBack = findViewById(R.id.tvBack);
        tvUserName = findViewById(R.id.tvUserName);

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        messageContainer = findViewById(R.id.messageContainer);

        // Get selected user
        String userName = getIntent().getStringExtra("user_name");

        if (userName == null || userName.isEmpty()) {
            userName = "Student";
        }

        tvUserName.setText(userName);

        // Back
        tvBack.setOnClickListener(v -> finish());

        // Send message
        btnSend.setOnClickListener(v -> {

            String message = etMessage
                    .getText()
                    .toString()
                    .trim();

            if (message.isEmpty()) {
                return;
            }

            addMessage(message);

            etMessage.setText("");
        });
    }

    private void addMessage(String message) {

        TextView newMessage = new TextView(this);

        newMessage.setText(message);
        newMessage.setTextSize(15);
        newMessage.setTextColor(getResources().getColor(R.color.white));

        newMessage.setPadding(18, 12, 18, 12);

        newMessage.setBackgroundColor(
                getResources().getColor(R.color.app_name)
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.gravity = android.view.Gravity.END;

        params.setMargins(60, 8, 10, 8);

        newMessage.setLayoutParams(params);

        messageContainer.addView(newMessage);
    }
}