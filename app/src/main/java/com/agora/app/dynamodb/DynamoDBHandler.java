package com.agora.app.dynamodb;

import java.security.Key;

import javax.swing.plaf.synth.Region;

import com.agora.app.backend.base.Password;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;


public class DynamoDBHandler {

    /**
     * The DynamoDB table name for storing users
     */
    public static String usersTableName = "agora_users";
    /**
     * The DynamoDB table name for storing passwords
     */
    public static String passwordsTableName = "agora_passwords";
    /**
     * The DynamoDB table name for storing chat objects
     */
    public static String chatsTableName = "agora_chats";
    /**
     * The DynamoDB table name for storing products
     */
    public static String productsTableName = "agora_products";
    /**
     * The AWS Region where our DynamoDB instance is, also where our credentials are valid
     */
    public static Region awsRegion = Region.US_EAST_2; // We will only be using stuff in the us_east_2 region as this region is based in Ohio

    private static DynamoDbClient basicClient = DynamoDbClient.builder()
                                                              //.httpClient(UrlConnectionHttpClient.create())
                                                              .region(awsRegion)
                                                              .build();
    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                                                                                 .dynamoDbClient(basicClient)
                                                                                 .build();


    public static GetItemEnhancedRequest makeRequestFromPartitionKey (String partitionKey) {
        return GetItemEnhancedRequest.builder()
                                     .key(Key.builder()
                                             .partitionValue(partitionKey)
                                             .build())
                                     .build();
    }

    /**
     * Retrieves a user object from the database, given a specific username
     *
     * @param username The username of the user (their CWRU Network ID)
     * @return The {@code User} object (if it exists) that has the specified username, {@code null} if there is no {@code User} object in the database with that username
     */
    public static User getUserItem (String username) {
        try {
            DynamoDbTable<UserWrapper> userTable = enhancedClient.table(DynamoDBHandler.usersTableName, TableSchema.fromBean(UserWrapper.class));
            return User.createFromBase64String(userTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(username)).getUserBase64());
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Puts a {@code User} object into the database, overwriting the current {@code User} object in the database if this {@code User} and the remote {@code User} object share the same username
     *
     * @param user The {@code User} object to be placed into the database
     */
    public static void putUserItem (User user) {
        try {
            DynamoDbTable<UserWrapper> userTable = enhancedClient.table(DynamoDBHandler.usersTableName, TableSchema.fromBean(UserWrapper.class));
            userTable.putItem(new UserWrapper(user));
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves a password object from the database, given a specific hash
     *
     * @param hash The hash of the password
     * @return The {@code Password} object (if it exists) that has the specified password hash, {@code null} if there is no {@code Password} object in the database with that hash
     */
    public static Password getPasswordItem (String hash) {
        try {
            DynamoDbTable<PasswordWrapper> passwordTable = enhancedClient.table(DynamoDBHandler.passwordsTableName, TableSchema.fromBean(PasswordWrapper.class));
            return Password.createFromBase64String(passwordTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(hash)).getPasswordBase64());
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Puts a {@code Password} object into the database, overwriting the current {@code Password} object in the database if this {@code Password} and the remote {@code Password} object share the same hash
     *
     * @param password The {@code Password} object to be placed into the database
     */
    public static void putPasswordItem (Password password) {
        try {
            DynamoDbTable<PasswordWrapper> passwordTable = enhancedClient.table(DynamoDBHandler.passwordsTableName, TableSchema.fromBean(PasswordWrapper.class));
            passwordTable.putItem(new PasswordWrapper(password));
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static Chat getChatItem (String otherUsername) {
        try {
            DynamoDbTable<Chat> chatTable = enhancedClient.table(DynamoDBHandler.chatsTableName, TableSchema.fromBean(Chat.class));
            return chatTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(otherUsername));
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public static Product getProductItem (String productUUID) {
        try {
            DynamoDbTable<Product> productTable = enhancedClient.table(DynamoDBHandler.productsTableName, TableSchema.fromBean(Product.class));
            return productTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(productUUID));
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

}
