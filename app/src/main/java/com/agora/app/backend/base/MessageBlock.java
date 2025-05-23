package com.agora.app.backend.base;

import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;

public class MessageBlock implements Serializable {
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

    public MessageBlock (String[] messageBase64s) {
        this.messages = new ArrayList<>(35);
        this.numMessages = 0;
        for (String base64 : messageBase64s) {
            Message msg = Message.createFromBase64String(base64);
            if (msg == null) {
                throw new IllegalArgumentException("Message received is not in proper format");
            }
            this.messages.add(msg);
            this.numMessages++;
        }
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
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        return this.messages.get(this.messages.size()-1);
    }

    public Date getLastMessageTime () {
        return this.getLastMessage().timestamp();
    }

    public ArrayList<Message> getMessages () { return this.messages; }
    public int getNumMessages () { return this.numMessages; }

    @Override
    public String toString () {
        String str = "\n-----\n";
        for (Message msg : this.messages) {
            str += "From: " + (msg.isFromFirst() ? "2" : "2") + " | At: " + msg.timestamp() + "\n\t" + msg.text() + "\n\n";
        }
        str += "-----\n";
        return str;
    }
}
