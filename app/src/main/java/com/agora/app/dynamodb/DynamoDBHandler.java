package com.agora.app.dynamodb;

import com.agora.app.backend.base.Password;

import com.agora.app.backend.base.Listing;
import com.agora.app.backend.base.User;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.io.FileWriter;
import java.io.IOException;


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
     * The DynamoDB table name for storing Listings
     */
    public static String listingsTableName = "agora_Listings";
    /**
     * The AWS Region where our DynamoDB instance is, also where our credentials are valid
     */
    public static Region awsRegion = Region.US_EAST_2; // We will only be using stuff in the us_east_2 region as this region is based in Ohio


    public static GetItemEnhancedRequest makeRequestFromPartitionKey (String partitionKey) {
        return GetItemEnhancedRequest.builder()
                                     .key(Key.builder()
                                             .partitionValue(partitionKey)
                                             .build())
                                     .build();
    }

    private static DynamoDbEnhancedClient makeClient () {
        return DynamoDbEnhancedClient.builder()
                                     .dynamoDbClient(DynamoDbClient.builder()
                                                                   .httpClient(UrlConnectionHttpClient.create())
                                                                   .region(awsRegion)
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
            DynamoDbTable<UserWrapper> userTable = makeClient().table(DynamoDBHandler.usersTableName, TableSchema.fromBean(UserWrapper.class));
            return User.createFromBase64String(userTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(username)).getUserBase64());
        } catch (DynamoDbException |NullPointerException ex) {
            try {
                FileWriter fw = new FileWriter("C:\\Users\\100ks\\.agora\\error3.txt");
                fw.write(ex.getMessage() + "\n");
                fw.write(ex.getLocalizedMessage() + "\n");
                for (StackTraceElement element : ex.getStackTrace()) {
                    fw.write(element.toString() + "\n");
                }
                fw.close();
            } catch (IOException e) {}
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
            DynamoDbTable<UserWrapper> userTable = makeClient().table(DynamoDBHandler.usersTableName, TableSchema.fromBean(UserWrapper.class));
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
            DynamoDbTable<PasswordWrapper> passwordTable = makeClient().table(DynamoDBHandler.passwordsTableName, TableSchema.fromBean(PasswordWrapper.class));
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
            DynamoDbTable<PasswordWrapper> passwordTable = makeClient().table(DynamoDBHandler.passwordsTableName, TableSchema.fromBean(PasswordWrapper.class));
            passwordTable.putItem(new PasswordWrapper(password));
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves a listing object from the database, given a specific listingUUID
     *
     * @param uuid The listingUUID of the listing
     * @return The {@code Listing} object (if it exists) that has the specified listingUUID, {@code null} if there is no {@code Listing} object in the database with that listingUUID
     */
    public static Listing getListingItem (String uuid) {
        try {
            DynamoDbTable<ListingWrapper> listingTable = makeClient().table(DynamoDBHandler.listingsTableName, TableSchema.fromBean(ListingWrapper.class));
            return Listing.createFromBase64String(listingTable.getItem(DynamoDBHandler.makeRequestFromPartitionKey(uuid)).getListingBase64());
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Puts a {@code Listing} object into the database, overwriting the current {@code Listing} object in the database if this {@code Listing} and the remote {@code Listing} object share the same listingUUID
     *
     * @param listing The {@code Listing} object to be placed into the database
     */
    public static void putListingItem (Listing listing) {
        try {
            DynamoDbTable<ListingWrapper> listingTable = makeClient().table(DynamoDBHandler.listingsTableName, TableSchema.fromBean(ListingWrapper.class));
            listingTable.putItem(new ListingWrapper(listing));
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

}
