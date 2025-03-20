package com.agora.app.backend.lambda;

public class KeyNotFoundException extends RuntimeException {
    public KeyNotFoundException (String message) {
        super(message);
    }
}
