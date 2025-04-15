package com.agora.app.backend;

import com.agora.app.backend.base.Chat;
import com.agora.app.backend.base.User;
import com.agora.app.backend.lambda.LambdaHandler;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

public class Session {
    private static final String socketURI = "wss://x6gndwesj1.execute-api.us-east-2.amazonaws.com/production/";
    public static final int socketTimeout = 0;
    public static WebSocket ws;
    public static User currentUser;
    public Session (User currentUser) throws IOException {
        Session.currentUser = currentUser;
        WebSocketFactory factory = new WebSocketFactory();
        factory.setConnectionTimeout(socketTimeout);
        Session.ws = factory.createSocket(socketURI);
        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket webSocket, String message) { // example message: {"from":"atk88","message":"hi levi"}
                try {
                    JSONObject jsonMessage = new JSONObject(message);
                    String fromUsername = jsonMessage.getString("from");
                    String messageText = jsonMessage.getString("message");
                    User currentUser = Session.currentUser;
                    Chat currentChat = currentUser.getChatObject(fromUsername);
                    currentChat.addMessage(messageText, currentUser);
                } catch (JSONException ex) {
                    throw new IllegalStateException("JSON Exception: " + ex.getMessage(), ex.getCause());
                }
            }
        });
        try {
            ws.connect();
            ws.sendText("{\"action\":\"link\",\"username\":\"" + Session.currentUser.getUsername() + "\"}");
        } catch (WebSocketException ex) {}
        Session.currentUser.loadMetaChats();
        Session.currentUser.loadChats();
    }

}
