package com.agora.app.dynamodb;

import com.agora.app.backend.base.Chat;
import com.agora.app.backend.base.Password;
import com.agora.app.backend.base.Product;
import com.agora.app.backend.base.User;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;


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

    /**
     * Creates the request object that asks for the item with the specified partition key
     *
     * @param partitionKey A unique string that can be used to find the item in the DynamoDB table
     * @return a {@code GetItemEnhancedRequest} object that is the properly formatted object for retrieving an item from a DynamoDB table
     */
    public static GetItemEnhancedRequest makeRequestFromPartitionKey (String partitionKey) {
        return GetItemEnhancedRequest.builder().key(Key.builder().partitionValue(partitionKey).build()).build();
    }

    /**
     * Creates the {@code DynamoDbEnhancedClient} required to interface with the DynamoDB tables
     *
     * @return a {@code DynamoDbEnhancedClient} object that is set up to be based in the AWS Region specified earlier
     */
    public static DynamoDbEnhancedClient makeDynamoClient () {
        DynamoDbClient basicClient = DynamoDbClient.builder().region(awsRegion).build();
        return DynamoDbEnhancedClient.builder().dynamoDbClient(basicClient).build();
    }

    /**
     * Retrieves a user object from the database, given a specific username
     *
     * @param username The username of the user (their CWRU Network ID)
     * @return The {@code User} object (if it exists) that has the specified username, {@code null} if there is no User object in the database with that username
     * @throws NullPointerException
     */
    public static User getUserItem (String username) {
        try {
            DynamoDbEnhancedClient enhancedClient = makeDynamoClient();
            DynamoDbTable<UserWrapper> userTable = enhancedClient.table(DynamoDBHandler.usersTableName, TableSchema.fromBean(UserWrapper.class));
            return User.createFromBase64String(userTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(username)).getUserBase64());
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public static void putUserItem (User user) {
        try {
            DynamoDbEnhancedClient enhancedClient = makeDynamoClient();
            DynamoDbTable<UserWrapper> userTable = enhancedClient.table(DynamoDBHandler.usersTableName, TableSchema.fromBean(UserWrapper.class));
            userTable.putItem(new UserWrapper(user));
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static Password getPasswordItem (String hash) throws NullPointerException {
        try {
            DynamoDbEnhancedClient enhancedClient = makeDynamoClient();
            DynamoDbTable<PasswordWrapper> passwordTable = enhancedClient.table(DynamoDBHandler.passwordsTableName, TableSchema.fromBean(PasswordWrapper.class));
            return Password.createFromBase64String(passwordTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(hash)).getPasswordBase64());
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public static void putPasswordItem (Password password) {
        try {
            DynamoDbEnhancedClient enhancedClient = makeDynamoClient();
            DynamoDbTable<PasswordWrapper> passwordTable = enhancedClient.table(DynamoDBHandler.passwordsTableName, TableSchema.fromBean(PasswordWrapper.class));
            passwordTable.putItem(new PasswordWrapper(password));
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static Chat getChatItem (String otherUsername) throws NullPointerException {
        try {
            DynamoDbEnhancedClient enhancedClient = makeDynamoClient();
            DynamoDbTable<Chat> chatTable = enhancedClient.table(DynamoDBHandler.chatsTableName, TableSchema.fromBean(Chat.class));
            return chatTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(otherUsername));
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public static Product getProductItem (String productUUID) throws NullPointerException {
        try {
            DynamoDbEnhancedClient enhancedClient = makeDynamoClient();
            DynamoDbTable<Product> productTable = enhancedClient.table(DynamoDBHandler.productsTableName, TableSchema.fromBean(Product.class));
            return productTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(productUUID));
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

}
