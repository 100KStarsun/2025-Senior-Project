package com.agora.app.frontend;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.agora.app.R;
import com.agora.app.backend.base.Message;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    //private ChatView chatView;
    private List<Message> messageList;
    private EditText messageInput;
    private ImageButton sendButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedChat) {
        super.onCreate(savedChat);
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.chat_log);
        messageInput = findViewById(R.id.message_input);
        //sendButton = findViewById(R.id.send_button);
        //sendButton = findViewById(R.id.back_button);

    }

}
