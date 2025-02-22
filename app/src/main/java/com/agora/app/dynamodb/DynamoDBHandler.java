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

    public static User getUserItem (String username) throws NullPointerException {
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
