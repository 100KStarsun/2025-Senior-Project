package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agora.app.R;
import com.agora.app.backend.base.Message;
import com.agora.app.backend.base.User;
import com.agora.app.backend.base.Chat;
import com.agora.app.backend.AppSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.Serializable;


/**
 * @class MessagingActivity
 * @brief Activity for messaging between users.
 */
public class MessagingActivity extends AppCompatActivity {

    private String username;
    private User currentUser = AppSession.currentUser;
    private ArrayList<Message> currentUserMessages;
    private ChatRecycler messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        Objects.requireNonNull(getSupportActionBar()).hide();
        username = getIntent().getStringExtra("username");
        TextView noChatsTextView = findViewById(R.id.no_chats);

        RecyclerView recyclerView = findViewById(R.id.message_listings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            currentUser.loadMetaChats();
            currentUser.loadChats();
            List<Chat> chatList = new ArrayList<>(currentUser.getChatObjects().values());
            Collections.sort(chatList, Collections.reverseOrder());
            runOnUiThread(() -> {
                if (chatList.isEmpty()) {
                    noChatsTextView.setVisibility(View.VISIBLE);
                } else {
                    noChatsTextView.setVisibility(View.GONE);
                }
                messageView = new ChatRecycler(this, chatList, username);
                recyclerView.setAdapter(messageView);
            });
        });

        // navigation bar routing section
        BottomNavigationView navBar = findViewById(R.id.nav_bar);

        navBar.setSelectedItemId(R.id.nav_messaging);

        // maps nav bar item to correct page redirection
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_messaging) {
                return true;
            }
            else if (itemId == R.id.nav_marketplace) {
                Intent intent = new Intent(this, MarketplaceActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("userObj", currentUser);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.nav_swiping) {
                Intent intent = new Intent(this, SwipingActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("userObj", currentUser);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.nav_user_info) {
                Intent intent = new Intent(this, UserInfoActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("userObj", currentUser);
                startActivity(intent);
                return true;
            }
            return false;
        });

        
    }
}
