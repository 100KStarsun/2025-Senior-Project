package com.agora.app.frontend;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agora.app.R;
import com.agora.app.backend.base.User;
import com.agora.app.backend.base.Message;
import com.agora.app.backend.AppSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    //private ChatView chatView;
    private List<Message> messageList;
    private EditText messageInput;
    private ImageView sendButton;
    private Button backButton;
    private String listingOwner;
    private String listingTitle;
    private ChatView chatView;
    private User currentUser = AppSession.currentUser;

    @Override
    protected void onCreate(Bundle savedChat) {
        super.onCreate(savedChat);
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.chat_log);
        messageInput = findViewById(R.id.message_input);
        listingOwner = getIntent().getStringExtra("ownerUser");
        listingTitle = getIntent().getStringExtra("listingTitle");
        sendButton = findViewById(R.id.send_button);
        //sendButton = findViewById(R.id.back_button);

        messageList = new ArrayList<>();
        chatView = new ChatView(messageList, listingOwner);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatView);
        sendButton.setOnClickListener(view -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
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
