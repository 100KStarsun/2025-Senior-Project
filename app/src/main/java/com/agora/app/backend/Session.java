package com.agora.app.backend;

import com.agora.app.backend.base.User;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Session {
    private String socketURI = "wss://x6gndwesj1.execute-api.us-east-2.amazonaws.com/production/";
    private int socketTimeout = 0;
    private WebSocket ws;
    private User currentUser;
    public Session (User currentUser) throws IOException {
        this.currentUser = currentUser;
        WebSocketFactory factory = new WebSocketFactory();
        factory.setConnectionTimeout(socketTimeout);
        this.ws = factory.createSocket(socketURI);
        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onConnected(WebSocket webSocket, Map<String, List<String>> headers) {
                // send the link command
            }
            @Override
            public void onTextMessage(WebSocket webSocket, String message) throws Exception {
                // Received a chat
            }
        });
    }

    public User getCurrentUser() { return this.currentUser; }
    public WebSocket getWs() { return this.ws; }
}
