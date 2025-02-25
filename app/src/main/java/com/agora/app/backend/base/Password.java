package com.agora.app.backend.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Password implements Serializable {
    private String hash;
    private String username;

    public Password (String password, String username) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            final byte[] hashbytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            this.hash = Password.bytesToHex(hashbytes);
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        this.username = username;
    }

    public String getHash() {
        return this.hash;
    }

    public String getUsername() {
        return this.username;
    }

    public static Password createFromBase64String (String encodedPassword) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodedPassword);
        try (ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes); ObjectInputStream objectIn = new ObjectInputStream(bytesIn)) {
            return (Password) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toBase64String () {
        Base64.Encoder encoder = Base64.getEncoder();
        try (ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(); ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut)) {
            objectOut.writeObject(this);
            return encoder.encodeToString(bytesOut.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public String toString() {
        return "Hash: " + this.hash + "\nUsername: " + this.username;
    }
}
