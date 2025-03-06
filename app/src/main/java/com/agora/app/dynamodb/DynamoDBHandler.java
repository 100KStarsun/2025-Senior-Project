package com.agora.app.dynamodb;

import com.agora.app.backend.base.*;
import com.agora.app.dynamodb.DynamoTables;
import com.agora.app.lambda.LambdaHandler;
import com.agora.app.lambda.Operations;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.EnumMap;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class DynamoDBHandler {

    public static String usersTableName = "agora_users";
    public static String passwordsTableName = "agora_passwords";
    public static String chatsTableName = "agora_chats";
    public static String productsTableName = "agora_products";
    public static Region awsRegion = Region.US_EAST_2; // We will only be using stuff in the us_east_2 region as this region is based in Ohio

    private static DynamoDbClient basicClient = DynamoDbClient.builder()
                                                              .httpClient(UrlConnectionHttpClient.create())
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

    public static User getUserItem (String username) throws NullPointerException {
        try {
            DynamoDbTable<UserWrapper> userTable = enhancedClient.table(DynamoDBHandler.usersTableName, TableSchema.fromBean(UserWrapper.class));
            return User.createFromBase64String(userTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(username)).getUserBase64());
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public static void putUserItem (User user) throws JSONException{
        try {
            /*
            DynamoDbTable<UserWrapper> userTable = enhancedClient.table(DynamoDBHandler.usersTableName, TableSchema.fromBean(UserWrapper.class));
            userTable.putItem(new UserWrapper(user));
            */
            HashMap<String, String> inputUser = new HashMap<>(2);
            inputUser.put(user.getUsername(), user.toBase64String());
            HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(2);
            data.put(DynamoTables.USERS, inputUser);
            LambdaHandler.invoke(data, Operations.PUT).getString("body");
            /* Likely not necessary since this is a put method
            JSONObject obj = new JSONObject(LambdaHandler.invoke(data, Operations.PUT).getString("body"));
            obj.remove("ResponseMetadata");
            */
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Given the hash of an input password, attempts to return the Password object associated with that password
     * Implements AWS Lambda as opposed to DynamoDB
     * 
     * @param hash            Hash of input user password
     * @return                Password object corresponding to input password (contains username)
     * @return                Null if no password object found within the Password table
     * @throws JSONException  JSONException thrown when invalid JSON found, or improper handling of JSON
     */
    public static Password getPasswordItem (String hash) throws JSONException {
        try {
            HashMap<String, String> items = new HashMap<>(2);
            items.put(hash, null);
            HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(2);
            data.put(DynamoTables.PASSWORDS, items);
            JSONObject obj = new JSONObject(LambdaHandler.invoke(data, Operations.GET).getString("body"));
            obj.remove("ResponseMetadata");
            JSONObject item = obj.optJSONObject("Item");
            if (item != null) {
                JSONObject base64Obj = item.optJSONObject("base64");
                if (base64Obj != null && base64Obj.has("S")) {
                    String encodedPassword = base64Obj.getString("S");
                    return Password.createFromBase64String(encodedPassword);
                }
            }
            return null;
        } catch (JSONException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public static void putPasswordItem (Password password) throws JSONException{
        try {
            /*
            DynamoDbTable<PasswordWrapper> passwordTable = enhancedClient.table(DynamoDBHandler.passwordsTableName, TableSchema.fromBean(PasswordWrapper.class));
            passwordTable.putItem(new PasswordWrapper(password));
            */
            HashMap<String, String> inputPassword = new HashMap<>(2);
            inputPassword.put(password.getHash(), password.toBase64String());
            HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(2);
            data.put(DynamoTables.PASSWORDS, inputPassword);
            LambdaHandler.invoke(data, Operations.PUT).getString("body");
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static Chat getChatItem (String otherUsername) throws NullPointerException {
        try {
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
            DynamoDbTable<Product> productTable = enhancedClient.table(DynamoDBHandler.productsTableName, TableSchema.fromBean(Product.class));
            return productTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(productUUID));
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }
}
