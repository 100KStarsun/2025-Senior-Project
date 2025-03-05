package com.agora.app.dynamodb;

import com.agora.app.backend.base.Password;

import com.agora.app.backend.base.Product;
import com.agora.app.backend.base.User;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.regions.Region;
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
     * @param saltedHash The hash of the password with salt in it
     * @return The {@code Password} object (if it exists) that has the specified password hash, {@code null} if there is no {@code Password} object in the database with that hash
     */
    public static Password getPasswordItem (String saltedHash) {
        try {
            DynamoDbTable<PasswordWrapper> passwordTable = enhancedClient.table(DynamoDBHandler.passwordsTableName, TableSchema.fromBean(PasswordWrapper.class));
            return Password.createFromBase64String(passwordTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(saltedHash)).getPasswordBase64());
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

    /**
     * Retrieves a product object from the database, given a specific productUUID
     *
     * @param uuid The productUUID of the product
     * @return The {@code Product} object (if it exists) that has the specified productUUID, {@code null} if there is no {@code Product} object in the database with that productUUID
     */
    public static Product getProductItem (String uuid) {
        try {
            DynamoDbTable<ProductWrapper> productTable = enhancedClient.table(DynamoDBHandler.productsTableName, TableSchema.fromBean(ProductWrapper.class));
            return Product.createFromBase64String(productTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(uuid)).getProductBase64());
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Puts a {@code Product} object into the database, overwriting the current {@code Product} object in the database if this {@code Product} and the remote {@code Product} object share the same productUUID
     *
     * @param product The {@code Product} object to be placed into the database
     */
    public static void putProductItem (Product product) {
        try {
            DynamoDbTable<ProductWrapper> productTable = enhancedClient.table(DynamoDBHandler.productsTableName, TableSchema.fromBean(ProductWrapper.class));
            productTable.putItem(new ProductWrapper(product));
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

}
