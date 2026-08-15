package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChatListActivity extends AppCompatActivity {

    private LinearLayout chatAhmed;
    private LinearLayout chatNusrat;
    private LinearLayout chatTanvir;
    private LinearLayout chatFarhan;

    private TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        tvBack = findViewById(R.id.tvBack);

        chatAhmed = findViewById(R.id.chatAhmed);
        chatNusrat = findViewById(R.id.chatNusrat);
        chatTanvir = findViewById(R.id.chatTanvir);
        chatFarhan = findViewById(R.id.chatFarhan);

        // Back
        tvBack.setOnClickListener(v -> finish());

        // Ahmed
        chatAhmed.setOnClickListener(v -> openChat("Ahmed Rahman"));

        // Nusrat
        chatNusrat.setOnClickListener(v -> openChat("Nusrat Jahan"));

        // Tanvir
        chatTanvir.setOnClickListener(v -> openChat("Tanvir Hasan"));

        // Farhan
        chatFarhan.setOnClickListener(v -> openChat("Farhan Ahmed"));
    }

    private void openChat(String userName) {

        Intent intent = new Intent(
                ChatListActivity.this,
                ChatActivity.class
        );

        intent.putExtra("user_name", userName);

        startActivity(intent);
    }
}