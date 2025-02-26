package com.agora.app.dynamodb;

import com.agora.app.backend.base.User;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class UserWrapper {

    /**
     * The username of the {@code User} object. This is unique among all users and as such, is the partition key for the user table in the database.
     */
    private String username;
    /**
     * The Base64-String-based representation of the entire {@code User} object, as created by {@code toBase64String()} in the {@code User} class.
     *
     * @see User#toBase64String()
     */
    private String userBase64;

    /**
     * Default constructor, not used, only included to fully implement the {@code @DynamoDbBean} requirements. Creates empty strings for {@code username} and {@code userBase64}.
     */
    public UserWrapper () {
        this.username = "";
        this.userBase64 = "";
    }

    /**
     * Grabs the username and base64 String representation of the {@code User} object passed and makes a DynamoDB-friendly object from it
     *
     * @param user The {@code User} object to be wrapped before being put into the DynamoDB table for users
     */
    public UserWrapper (User user) {
        this.username = user.getUsername();
        this.userBase64 = user.toBase64String();
    }

    /**
     * Returns the username of the {@code User} object being (un)wrapped
     *
     * @return the {@code String} object that holds the username of the {@code User} object being (un)wrapped
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("username")
    public String getUsername () {
        return this.username;
    }

    /**
     * Returns the base64-encoded {@code User} object as a {@code String} object
     *
     * @return The base64-encoded {@code String} object that represents the {@code User} object being (un)wrapped
     */
    @DynamoDbAttribute("base64")
    public String getUserBase64 () {
        return this.userBase64;
    }

    /**
     * Manually sets the username of the wrapped {@code User} object. Only implemented to satisfy {@code @DynamoDbBean} requirements.
     *
     * @param username the username of the {@code User} object
     */
    public void setUsername (String username) {
        this.username = username;
    }

    /**
     * Manually sets the base64 string of the wrapped {@code User} object. Only implemented to satisfy {@code @DynamoDbBean} requirements.
     *
     * @param userBase64 the base64 string representation of the {@code User} object
     */
    public void setUserBase64 (String userBase64) {
        this.userBase64 = userBase64;
    }
}
