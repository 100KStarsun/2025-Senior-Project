package com.agora.app.backend.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class Chat implements Serializable {
    private static final long serialVersionUID = 2042010294253052140L;
    private static final int maxChatSize = 200000;

    private String id;
    private String username1;
    private String username2;
    private ArrayList<Message> messages;
    private boolean isFull;

    public Chat (String username1, String username2, int index) {
        this.id = username1 + "_" + username2 + "_" + index;
        this.username1 = username1;
        this.username2 = username2;
        this.messages = new ArrayList<>(20);
        this.isFull = false;
    }

    private String getOtherUsername (User currentUser) {
        return this.username1.equals(currentUser.getUsername()) ? this.username2 : this.username1;
    }

    private Chat addMessage (String messageText, User currentUser, boolean fromUser1) {
        Message message = new Message(messageText, new Date(), fromUser1);

        if (this.toBase64String().length() < maxChatSize) {
            this.messages.add(this.messages.size(), message);
            return null;
        }
        this.isFull = true;
        return null;
    }

    public static Chat createFromBase64String (String encodedChat) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodedChat);
        try (ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes); ObjectInputStream objectIn = new ObjectInputStream(bytesIn)) {
            return (Chat) objectIn.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String toBase64String () {
        Base64.Encoder encoder = Base64.getEncoder();
        try (ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(); ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut)) {
            objectOut.writeObject(this);
            return encoder.encodeToString(bytesOut.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
