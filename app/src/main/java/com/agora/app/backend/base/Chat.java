package com.agora.app.backend.base;

import com.agora.app.backend.Session;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class Chat implements Serializable, Comparable<Chat> {
    private static final int maxMessageLength = 6500; // 7000 generally seems fine but 6500 gives us some extra padding, with pure base64 encoding

    private String id;
    private String username1;
    private String username2;
    private ArrayList<MessageBlock> messageBlocks;


    public Chat (String username1, String username2, int index) {
        this.id = username1 + "_" + username2 + "_" + new DecimalFormat("0000").format(index);
        this.username1 = username1;
        this.username2 = username2;
        this.messageBlocks = new ArrayList<>(20);
        this.messageBlocks.add(new MessageBlock());
    }

    private String getOtherUsername (User currentUser) {
        return this.username1.equals(currentUser.getUsername()) ? this.username2 : this.username1;
    }

    private String getFirstUsername () {
        return this.id.split("_")[0];
    }

    private static String getSendChatMessage (String toUsername, String message) {
        return "{\"action\":\"sendmessage\",\"to\":\"" + toUsername + "\",\"message\":\"" + message + "\"}";
    }

    // if this returns true then a new message block was created
    private boolean addMessage (String messageText, User currentUser) {
        if (messageText.length() > maxMessageLength) {
            throw new MessageTooLongException("Message of " + messageText.length() + " characters is too long to send. The max message length is " + maxMessageLength + " characters.");
        }
        Message newMessage = new Message(messageText, new Date(), this.username1.equals(currentUser.getUsername()));
        MessageBlock currentBlock = messageBlocks.get(messageBlocks.size()-1);
        MessageBlock newBlock = currentBlock.addMessage(newMessage);
        if (currentBlock != newBlock) {
            // *should* be safe to use != because MessageBlock.addMessage() returns the same block passed if we don't need a new block
            messageBlocks.add(newBlock);
            return true;
        }
        return false;
    }

    // Chat.sendMessage("abc123", "hi Alice, it's Bob!");
    public static boolean sendMessage (String toUsername, String message) {
        Chat currentChat = Session.currentUser.getChatObject(toUsername);
        return currentChat.safeSendMessage(toUsername, message);
    }

    private boolean safeSendMessage (String toUsername, String message) {
        Session.ws.sendText(getSendChatMessage(toUsername, message));
        return this.addMessage(message, Session.currentUser);
    }

    public MessageBlock getLastMessageBlock () {
        return this.messageBlocks.get(messageBlocks.size()-1);
    }

    @Override
    public int compareTo (Chat otherChat) {
        return this.getLastMessageBlock().getLastMessageTime().compareTo(otherChat.getLastMessageBlock().getLastMessageTime());
    }
}
