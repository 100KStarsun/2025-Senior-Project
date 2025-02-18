package com.agora.app.dynamodb;

import java.util.Map;

import com.agora.app.backend.User;
import com.agora.app.backend.Password;
import com.agora.app.backend.Chat;
import com.agora.app.backend.Product;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
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

    public static User getUserItem (String username) {
        try {
            DynamoDbEnhancedClient ddbec = DynamoDbEnhancedClient.create();
            DynamoDbTable<User> userTable = ddbec.table(DynamoDBHandler.usersTableName, TableSchema.fromBean(User.class));
            return ddbec.getItem(DynamoDBHandler.makeRequestFromPartitionKey(username));
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Password getPasswordItem (String hash) {
        try {
            DynamoDbEnhancedClient ddbec = DynamoDbEnhancedClient.create();
            DynamoDbTable<Password> passwordTable = ddbec.table(DynamoDBHandler.passwordsTableNameTableName, TableSchema.fromBean(Password.class));
            return ddbec.getItem(DynamoDBHandler.makeRequestFromPartitionKey(hash));
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static User getChatItem (String otherUsername) {
        try {
            DynamoDbEnhancedClient ddbec = DynamoDbEnhancedClient.create();
            DynamoDbTable<Chat> chatTable = ddbec.table(DynamoDBHandler.chatsTableNamesTableName, TableSchema.fromBean(Chat.class));
            return ddbec.getItem(DynamoDBHandler.makeRequestFromPartitionKey(otherUsername));
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static User getProductItem (String productUUID) {
        try {
            DynamoDbEnhancedClient ddbec = DynamoDbEnhancedClient.create();
            DynamoDbTable<User> productTable = ddbec.table(DynamoDBHandler.productsTableName, TableSchema.fromBean(Product.class));
            return ddbec.getItem(DynamoDBHandler.makeRequestFromPartitionKey(productUUID));
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}