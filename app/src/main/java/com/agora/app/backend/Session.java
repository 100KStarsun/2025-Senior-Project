package com.agora.app.backend;

import com.agora.app.backend.base.User;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;

public class Session {
    private static final String socketURI = "wss://x6gndwesj1.execute-api.us-east-2.amazonaws.com/production/";
    public static final int socketTimeout = 0;
    public static final int numPreLoadedChats = 3;
    public static WebSocket ws;
    public static User currentUser;
    public Session (User currentUser) throws IOException {
        Session.currentUser = currentUser;
        WebSocketFactory factory = new WebSocketFactory();
        factory.setConnectionTimeout(socketTimeout);
        Session.ws = factory.createSocket(socketURI);
        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket webSocket, String message) throws Exception {
                // TODO: Check if the user we got a message from already has an existing chat object inside our user, if not then we need to grab it from the database
                // TODO: If that chat doesn't exist in the database then we need to create said chat object
                // TODO: then update this chat object with the new message

            }
        });
        try {
            ws.connect();
            ws.sendText("{\"action\":\"link\",\"username\":\"" + Session.currentUser.getUsername() + "\"}");
        } catch (WebSocketException ex) {}

        // TODO: load the X most recent chats and all chats with new messages
    }

}
