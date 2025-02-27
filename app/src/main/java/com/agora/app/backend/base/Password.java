package com.agora.app.backend.base;

import com.agora.app.dynamodb.DynamoDBHandler;

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

    /**
     * Used by the {@code PasswordWrapper} class to turn the base64-encoded string back into a {@code Password}, using an {@code ObjectInputStream}, {@code ByteArrayInputStream}, and the {@code Base64.Decoder} class.
     *
     * @param encodedPassword a base64 string that should have been created by the {@code toBase64String()} method
     * @return a {@code Password} object that holds all the data it did before it was converted to a base64 string
     */
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

    /**
     * Used by the {@code PasswordWrapper} class to turn a {@code Password} object into a base64-encoded string for easy database storage. This is done using a {@code ObjectOutputStream}, {@code ByteArrayOutputStream}, and the {@code Base64.Encoder} class.
     *
     * @return a string containing the base64 representation of the {@code Password} object this method was called using.
     */
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

    /**
     * Used by the constructor of this class to convert a {@code Byte[]} to a hexadecimal string
     *
     * @param hash the bytes of the hashed password
     * @return a hexadecimal string representing the bytes of the hashed password
     */
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

    /**
     * Two {@code Password} objects are equal if they share the same hash and username
     *
     * @param obj the second {@code Password} object with which to compare the first to
     * @return true if the two objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return this.hash.equals(((Password) obj).hash) && this.username.equals(((Password) obj).username);
    }
}
