package com.agora.app.backend;

public class Password {
    private String password;
    private String hash;

    public Password (String password, String hash) {
        this.password = password;
        this.hash = hash;
    }

    private String getPassword() {
        return this.password;
    }

    public String computeHashToLookupPassword () {
        return "test_hash";
    }
}
