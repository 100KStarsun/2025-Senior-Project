package com.agora.app.backend.base;

import java.util.ArrayList;
import java.util.Date;

public class MessageBlock {
    public static final int maxMessagesPerBlock = 35;
    private int numMessages;
    private ArrayList<Message> messages;

    public MessageBlock () {
        this.messages = new ArrayList<>(35);
        this.numMessages = 0;
    }

    public MessageBlock (Message initialMessage) {
        this.messages = new ArrayList<>(35);
        this.messages.add(initialMessage);
        this.numMessages = 1;
    }

    public MessageBlock addMessage (Message newMessage) {
        if (this.numMessages == maxMessagesPerBlock) {
            return new MessageBlock(newMessage);
        } else {
            this.messages.add(newMessage);
            this.numMessages++;
            return this;
        }
    }

    public Message getLastMessage () {
        return this.messages.get(this.messages.size()-1);
    }

    public Date getLastMessageTime () {
        return this.getLastMessage().timestamp();
    }

    public ArrayList<Message> getMessages () { return this.messages; }
    public int getNumMessages () { return this.numMessages; }
}
