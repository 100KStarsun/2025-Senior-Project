package com.agora.app.frontend;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agora.app.R;
import com.agora.app.backend.base.Message;

import java.util.List;


/**
 * This class is designed to organize the layouts of chats in a Recycler view fashion, similar to listings
 */
public class ChatView extends RecyclerView.Adapter<ChatView.ChatViewHolder> {
    
    private List<Message> messages;
    private String listingOwner;

    public ChatView(List<Message> messages, String listingOwner) {
        this.messages = messages;
        this.listingOwner = listingOwner;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isFromFirst() ? 1 : 0;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bubble, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_chat_bubble, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Message msg = messages.get(position);
        holder.chatText.setText(msg.text());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView chatText;

        public ChatViewHolder(View itemView) {
            super(itemView);
            chatText = itemView.findViewById(R.id.chat_user_name);
        }
    }
}
