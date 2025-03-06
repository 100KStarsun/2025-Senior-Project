package com.agora.app.backend;

import com.agora.app.backend.base.User;
import com.agora.app.backend.base.Password;
import com.agora.app.dynamodb.DynamoDBHandler;

import org.json.JSONException;

public class RegistrationHandler {

    /**
     * register handles the business logic behind account registration
     * Communicates directly with DynamoDBHandler to insert a user record into the table
     * 
     * @param username  Default username for the account (currently case id)
     * @param password  Password provided by the user at registration
     * @return          Success of creating new user record in the database
     */
    public static boolean register (String username, String password) {
        try {
            String email = username + "@case.edu";
            User regUser = new User(username, null, null, null, email, null);
            Password regPass = new Password(password, username);
            DynamoDBHandler.putUserItem(regUser);
            DynamoDBHandler.putPasswordItem(regPass);
            return true; //Should the put methods for db return boolean types to indicate success?
        } catch (JSONException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
