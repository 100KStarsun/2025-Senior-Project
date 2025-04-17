package com.agora.app.backend;

import com.agora.app.backend.base.Chat;
import com.agora.app.backend.base.User;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AppSession {
    private static final String socketURI = "wss://x6gndwesj1.execute-api.us-east-2.amazonaws.com/production/";
    public static final int socketTimeout = 0;
    public static WebSocket ws;
    public static User currentUser;
    public AppSession (User currentUser) throws IOException {
        AppSession.currentUser = currentUser;
        WebSocketFactory factory = new WebSocketFactory();
        factory.setConnectionTimeout(socketTimeout);
        AppSession.ws = factory.createSocket(socketURI);
        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket webSocket, String message) { // example message: {"from":"atk88","message":"hi levi"}
                try {
                    JSONObject jsonMessage = new JSONObject(message);
                    String fromUsername = jsonMessage.getString("from");
                    String messageText = jsonMessage.getString("message");
                    User currentUser = AppSession.currentUser;
                    Chat currentChat = currentUser.getChatObject(fromUsername);
                    currentChat.addMessage(messageText, currentUser);
                } catch (JSONException ex) {
                    throw new IllegalStateException("JSON Exception: " + ex.getMessage(), ex.getCause());
                }
            }
        });
        try {
            ws.connect();
            ws.sendText("{\"action\":\"link\",\"username\":\"" + AppSession.currentUser.getUsername() + "\"}");
        } catch (WebSocketException ex) {}
        AppSession.currentUser.loadMetaChats();
        AppSession.currentUser.loadChats();
    }

}
