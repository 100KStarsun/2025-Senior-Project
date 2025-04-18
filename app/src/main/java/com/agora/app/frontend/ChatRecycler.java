package com.agora.app.frontend;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.agora.app.backend.base.Chat;
import com.agora.app.R;
import com.agora.app.backend.AppSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;


/**
 * ChatRecycler works to provide a dynamic view of chat_listing on the MessagingActivity
 */
public class ChatRecycler extends RecyclerView.Adapter<ChatRecycler.ChatRecyclerHolder>{
    
    private final Context context;
    private List<Chat> chatList;
    private final String currentUsername;

    public ChatRecycler(Context context, List<Chat> chatList, String currentUsername) {
        this.context = context;
        this.chatList = chatList;
        this.currentUsername = currentUsername;
        Collections.sort(this.chatList, Collections.reverseOrder());
    }

    @Override
    public ChatRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_listing, parent, false);
        return new ChatRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatRecyclerHolder holder, int position) {
        Chat chat = chatList.get(position);
        String[] parts = chat.getId().split("_");
        String otherUsername = parts[0].equals(currentUsername) ? parts[1]: parts[0];
        holder.chatUserName.setText(otherUsername);
        String preview = chat.getLatestMessage().text();
        holder.listingName.setText(preview);
        holder.image.setImageResource(R.drawable.ic_placeholder);
        holder.chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("username", currentUsername);
            intent.putExtra("otherUsername", otherUsername);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void updateChats(List<Chat> newChats) {
        this.chatList = newChats;
        notifyDataSetChanged();
    }

    static class ChatRecyclerHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView chatUserName, listingName;
        Button chatButton;

        ChatRecyclerHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            chatUserName = itemView.findViewById(R.id.chat_user_name);
            listingName = itemView.findViewById(R.id.listing_name);
            chatButton = itemView.findViewById(R.id.chat_button);
        }
    }
}
