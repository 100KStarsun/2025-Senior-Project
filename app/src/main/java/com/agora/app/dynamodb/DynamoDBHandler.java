package com.agora.app.dynamodb;

import com.agora.app.backend.*;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;


public class DynamoDBHandler {

    public static String usersTableName = "agora_users";
    public static String passwordsTableName = "agora_passwords";
    public static String chatsTableName = "agora_chats";
    public static String productsTableName = "agora_products";
    public static Region awsRegion = Region.US_EAST_2; // We will only be using stuff in the us_east_2 region as this region is based in Ohio

    public static GetItemEnhancedRequest makeRequestFromPartitionKey (String partitionKey) {
        return GetItemEnhancedRequest.builder().key(Key.builder().partitionValue(partitionKey).build()).build();
    }

    public static DynamoDbEnhancedClient makeDynamoClient () {
        DynamoDbClient basicClient = DynamoDbClient.builder().region(awsRegion).build();
        return DynamoDbEnhancedClient.builder().dynamoDbClient(basicClient).build();
    }

    public static User getUserItem (String username) {
        try {

            DynamoDbEnhancedClient enhancedClient = makeDynamoClient();
            DynamoDbTable<User> userTable = enhancedClient.table(DynamoDBHandler.usersTableName, TableSchema.fromBean(User.class));
            return userTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(username));
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void putUserItem (User user) {
        try {
            DynamoDbEnhancedClient enhancedClient = makeDynamoClient();
            DynamoDbTable<User> userTable = enhancedClient.table(DynamoDBHandler.usersTableName, TableSchema.fromBean(User.class));
            userTable.putItem(user);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static Password getPasswordItem (String hash) {
        try {
            DynamoDbEnhancedClient enhancedClient = makeDynamoClient();
            DynamoDbTable<Password> passwordTable = enhancedClient.table(DynamoDBHandler.passwordsTableName, TableSchema.fromBean(Password.class));
            return passwordTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(hash));
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void putPasswordItem (Password password) {
        try {
            DynamoDbEnhancedClient enhancedClient = makeDynamoClient();
            DynamoDbTable<Password> passwordTable = enhancedClient.table(DynamoDBHandler.passwordsTableName, TableSchema.fromBean(Password.class));
            passwordTable.putItem(password);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static Chat getChatItem (String otherUsername) {
        try {
            DynamoDbEnhancedClient enhancedClient = makeDynamoClient();
            DynamoDbTable<Chat> chatTable = enhancedClient.table(DynamoDBHandler.chatsTableName, TableSchema.fromBean(Chat.class));
            return chatTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(otherUsername));
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Product getProductItem (String productUUID) {
        try {
            DynamoDbEnhancedClient enhancedClient = makeDynamoClient();
            DynamoDbTable<Product> productTable = enhancedClient.table(DynamoDBHandler.productsTableName, TableSchema.fromBean(Product.class));
            return productTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(productUUID));
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
