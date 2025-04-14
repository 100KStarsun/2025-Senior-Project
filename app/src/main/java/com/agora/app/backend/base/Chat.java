package com.agora.app.backend.base;

import androidx.annotation.NonNull;
import com.agora.app.backend.Session;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

public class Chat implements Serializable, Comparable<Chat> {
    // 35 messages per message block
    public static final int numMessageBlocksPerRequest = 3;
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

    private Chat (String id, ArrayList<MessageBlock> blockList) {
        this.id = id;
        this.username1 = id.split("_")[0];
        this.username2 = id.split("_")[1];
        this.messageBlocks = blockList;
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
    public boolean addMessage (String messageText, User currentUser) {
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

    public static String[] getBlockIDsFromID (String id) {
        String[] strArr = id.split("_");
        int count = Integer.parseInt(strArr[2]);
        String[] blockIDs = new String[count];
        DecimalFormat formatter = new DecimalFormat("0000");
        for (int i = 0; i <= count; i++) {
            blockIDs[i] = strArr[0] + "_" + strArr[1] + "_" + formatter.format(i);
        }
        return blockIDs;
    }

    public static HashMap<String, Chat> makeChatsFromMessageBlocks (TreeMap<String, MessageBlock> blocks, String currentUsername) {
        HashMap<String, Chat> chats = new HashMap<>();
        String[] currentNames = new String[2];
        ArrayList<MessageBlock> blocksList = new ArrayList<>();
        for (String id : blocks.keySet()) {
            String[] idParts = id.split("_");
            if (!currentNames[0].equals(idParts[0]) && !currentNames[1].equals(idParts[1])) {
                String chatID = currentNames[0] + "_" + currentNames[1] + "_" + new DecimalFormat("0000").format(blocksList.size() - 1);
                String otherUsername = currentNames[0].equals(currentUsername) ? currentNames[1] : currentNames[0];
                Chat tempChat = new Chat(chatID, blocksList);
                chats.put(otherUsername, tempChat);
                blocksList = new ArrayList<>();
                currentNames[0] = idParts[0];
                currentNames[1] = idParts[1];
            } else {
                blocksList.add(blocks.get(id));
            }
        }
        return chats;
    }

    public ArrayList<Message> getAllMessagesOldestToNewest () {
        ArrayList<Message> messages = new ArrayList<>();
        for (MessageBlock block : this.messageBlocks) {
            for (Message msg : block.getMessages()) {
                messages.add(msg);
            }
        }
        return messages;
    }

    public ArrayList<Message> getAllMessagesNewestToOldest () {
        ArrayList<Message> messages = this.getAllMessagesOldestToNewest();
        ArrayList<Message> correctList = new ArrayList<>();
        for (int i = messages.size()-1; i >= 0; i--) {
            correctList.add(messages.get(i));
        }
        return correctList;
    }
}
