package com.agora.app.dynamodb;

import com.agora.app.backend.base.Password;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class PasswordWrapper {

    /**
     * The salted hash of a user's password. This is unique among all passwords and as such, is the partition key for the password table in the database.
     */
    private String hash;
    /**
     * The Base64-String-based representation of the entire {@code Password} object, as created by {@code toBase64String()} in the {@code Password} class.
     *
     * @see Password#toBase64String()
     */
    private String passwordBase64;

    /**
     * Default constructor, not used, only included to fully implement the {@code @DynamoDbBean} requirements. Creates empty strings for {@code hash} and {@code passwordBase64}.
     */
    public PasswordWrapper () {
        this.hash = "";
        this.passwordBase64 = "";
    }

    /**
     * Grabs the hash and base64 String representation of the {@code Password} object passed and makes a DynamoDB-friendly object from it
     *
     * @param password The {@code Password} object to be wrapped before being put into the DynamoDB table for passwords
     */
    public PasswordWrapper (Password password) {
        this.hash = password.getHash();
        this.passwordBase64 = password.toBase64String();
    }

    /**
     * Returns the hash of the {@code Password} object being (un)wrapped
     *
     * @return the {@code String} object that holds the hash of the {@code Password} object being (un)wrapped
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("hash")
    public String getHash () {
        return hash;
    }

    /**
     * Returns the base64-encoded {@code Password} object as a {@code String} object
     *
     * @return The base64-encoded {@code String} object that represents the {@code Password} object being (un)wrapped
     */
    @DynamoDbAttribute("base64")
    public String getPasswordBase64 () {
        return passwordBase64;
    }

    /**
     * Manually sets the hash of the wrapped {@code Password} object. Only implemented to satisfy {@code @DynamoDbBean} requirements.
     *
     * @param hash the hash of the {@code Password} object
     */
    public void setHash (String hash) {
        this.hash = hash;
    }

    /**
     * Manually sets the base64 string of the wrapped {@code Password} object. Only implemented to satisfy {@code @DynamoDbBean} requirements.
     *
     * @param passwordBase64 the base64 string representation of the {@code Password} object
     */
    public void setPasswordBase64 (String passwordBase64) {
        this.passwordBase64 = passwordBase64;
    }
}
