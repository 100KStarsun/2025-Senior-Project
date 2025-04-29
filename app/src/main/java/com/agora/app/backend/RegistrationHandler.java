package com.agora.app.backend;

import com.agora.app.backend.base.User;
import com.agora.app.backend.base.Password;
import com.agora.app.backend.lambda.LambdaHandler;

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
        String email = username + "@case.edu";
        User regUser = new User(username, null, null, null, email, null);
        LambdaHandler.putUsers(new String[]{username}, new String[]{regUser.toBase64String()}); //Need base 64 here
        Password regPass = new Password(password, username);
        LambdaHandler.putPasswords(new String[]{regPass.getHash()}, new String[]{regPass.toBase64String()});
        return true; //Should the put methods for db return boolean types to indicate success?
    }
}
