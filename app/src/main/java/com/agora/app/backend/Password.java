package com.agora.app.backend;

public class Password {
    private String hash;

    public Password (String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return this.hash;
    }
}
