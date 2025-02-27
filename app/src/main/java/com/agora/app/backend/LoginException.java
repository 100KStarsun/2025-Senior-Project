package com.agora.app.backend;

/**
 * Thrown to indicate that there was an issue with the information the user provided for logging in
 */
public class LoginException extends RuntimeException {

    /**
     * A basic constructor that sets the message of the parent {@code RuntimeException}
     * @param message a short description describing the issue with logging in
     */
    public LoginException (String message) {
        super(message);
    }
}
