package com.agora.app.lambda;

public class KeyNotFoundException extends RuntimeException {
    public KeyNotFoundException (String message) {
        super(message);
    }
}
