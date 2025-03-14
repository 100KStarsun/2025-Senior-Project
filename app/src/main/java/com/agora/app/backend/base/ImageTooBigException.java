package com.agora.app.backend.base;

public class ImageTooBigException extends RuntimeException {
    public ImageTooBigException (String message) {
        super(message);
    }
}
