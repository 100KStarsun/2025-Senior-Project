package com.agora.app.frontend;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agora.app.R;
import com.agora.app.backend.base.User;
import com.agora.app.backend.base.Message;
import com.agora.app.backend.base.Chat;
import com.agora.app.backend.AppSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Message> messageList;
    private EditText messageInput;
    private ImageView sendButton;
    private String otherUsername;
    private String listingTitle;
    private ChatView chatView;
    private User currentUser = AppSession.currentUser;
    private String currentUsername;
    private Chat chat;
    private TextView recipient;

    @Override
    protected void onCreate(Bundle savedChat) {
        super.onCreate(savedChat);
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.chat_log);
        messageInput = findViewById(R.id.message_input);
        otherUsername = getIntent().getStringExtra("otherUsername");
        listingTitle = getIntent().getStringExtra("listingTitle");
        chat = getIntent().getSerializableExtra("chatObj", Chat.class);
        currentUsername = AppSession.currentUser.getUsername();
        sendButton = findViewById(R.id.send_button);
        recipient = findViewById(R.id.chat_recipient);
        if (currentUsername == null || otherUsername == null) {
            Log.e("ChatActivity", "Missing usernames in Intent for Chats. Current = "+ currentUsername + " Other = " + otherUsername);
            Toast.makeText(ChatActivity.this, "Error opening chat...", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        recipient.setText(otherUsername);

        messageList = new ArrayList<>();
        chatView = new ChatView(messageList, currentUsername);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatView);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Message> pastMessages = AppSession.currentUser.getAllMessagesOldestToNewest(otherUsername);
            runOnUiThread(() -> {
                messageList.clear();
                messageList.addAll(pastMessages);
                chatView.notifyDataSetChanged();
                if (!messageList.isEmpty()) {
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            });
        });

        sendButton.setOnClickListener(view -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                executor.execute(() -> {
                    Chat.sendMessage(otherUsername, messageText);
                });
                Message message = new Message(messageText, new Date(), true);
                messageList.add(message);
                chatView.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
                messageInput.setText("");
            }
        });


        // Nav bar (Copied from Messaging, similar baseline)
        BottomNavigationView navBar = findViewById(R.id.nav_bar);
        navBar.setSelectedItemId(R.id.nav_messaging);
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_messaging) {
                Intent intent = new Intent(this, MessagingActivity.class);
                intent.putExtra("username", currentUser.getUsername());
                intent.putExtra("userObj", currentUser);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.nav_marketplace) {
                Intent intent = new Intent(this, MarketplaceActivity.class);
                intent.putExtra("username", currentUser.getUsername());
                intent.putExtra("userObj", currentUser);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.nav_swiping) {
                Intent intent = new Intent(this, SwipingActivity.class);
                intent.putExtra("username", currentUser.getUsername());
                intent.putExtra("userObj", currentUser);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.nav_user_info) {
                Intent intent = new Intent(this, UserInfoActivity.class);
                intent.putExtra("username", currentUser.getUsername());
                intent.putExtra("userObj", currentUser);
                startActivity(intent);
                return true;
            }
            return false;
        });


    }
}
