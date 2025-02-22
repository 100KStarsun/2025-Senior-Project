package com.agora.app.backend;

public class LoginException extends RuntimeException {
    public LoginException (String message) {
        super(message);
    }
}
