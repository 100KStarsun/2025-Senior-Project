package com.agora.app.backend;

import com.agora.app.backend.base.Password;
import com.agora.app.dynamodb.DynamoDBHandler;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.json.JSONException;

public class LoginHandler {

    public static boolean login (String username, String password) throws JSONException, LoginException, NoSuchAlgorithmException {
        try {
            final MessageDigest digest = MessageDigest.getInstance(Password.hashAlgorithm);
            final byte[] hashbytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            Password pw = DynamoDBHandler.getPasswordItem(bytesToHex(hashbytes));
            if (pw.getUsername().equals(username)) {
                return true;
            } else {
                // this case is when both the username and password provided exist, but the username associated with the password is not correct
                throw new LoginException("Incorrect username or password.");
            }
        } catch (NullPointerException ex) {
            // this case is when either the username or password provided do not exist in the database
            throw new LoginException("Incorrect username or password.");
        }
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
}
