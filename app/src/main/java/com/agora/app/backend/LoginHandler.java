package com.agora.app.backend;

import com.agora.app.backend.base.Password;
import com.agora.app.dynamodb.DynamoDBHandler;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginHandler {

    /**
     * Queries the database to see if login credentials provided match those in the database. If the login info is not correct, a {@ccode LoginException} will be thrown
     *
     * @param username the provided username of the user trying to log in
     * @param password the provided password of the user trying to log in
     * @return {@code true} if the login info matches
     */
    public static boolean login (String username, String password) {
        try {
            final MessageDigest digest = MessageDigest.getInstance(Password.hashMethod);
            final byte[] hashbytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            Password pw = DynamoDBHandler.getPasswordItem(bytesToHex(hashbytes));
            if (pw.getUsername().equals(username)) {
                return true;
            } else {
                // this case is when both the username and password provided exist, but the username associated with the password is not correct
                throw new LoginException("Incorrect username or password.");
            }
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            // this case is when either the username or password provided do not exist in the database
            throw new LoginException("Incorrect username or password.");
        }
        return false;
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
}
