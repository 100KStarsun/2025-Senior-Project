package com.agora.app.backend.base;

public class MessageTooLongException extends RuntimeException {
    public MessageTooLongException (String message) {
        super(message);
    }
}
