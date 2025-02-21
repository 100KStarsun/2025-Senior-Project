package com.agora.app.backend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class Password {
    private String hash;
    private String username;

    public Password (String hash, String username) {
        this.hash = hash;
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
        } catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println(e);
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
            System.err.println(e);
            e.printStackTrace();
        }
        return null;
    }
}
