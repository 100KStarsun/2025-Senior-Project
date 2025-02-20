package com.agora.app.backend;

import com.agora.app.dynamodb.DynamoDBHandler;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.EnumMap;
import java.util.TreeMap;
import java.util.UUID;


@DynamoDbBean
public class User implements Serializable {

    private String username;
    private final String preferredFirstName;
    private String legalFirstName;
    private String lastName;
    private String email;
    private String university;
    private Instant timeCreated;
    private int numSwaps;
    private short rating;
    private EnumMap<PaymentMethods, Boolean> paymentMethodsSetup; // boolean is whether the user has this setup
    private TreeMap<String, ArrayList<UUID>> chats; // key is the username of the other person, the ArrayList is a list of `chatUUID`s that are in the db
    private ArrayList<UUID> draftedProducts; // a list of UUIDs of products the user has drafted
    private ArrayList<UUID> publishedProducts; // a list of UUIDs of products the user has published
    private ArrayList<UUID> likedProducts; // a list of UUIDs of all products the user has liked
    private ArrayList<UUID> mutedUsers; // a list of UUIDs of all users that this user has muted (i.e. no notifications at all for new messages, but they still get sent)
    private ArrayList<UUID> blockedUsers; // a list of UUIDs of all users that this user has blocked (i.e. chat is closed and other user doesn't know that this user has blocked them)


    public User () {
        this.username = "";
        this.preferredFirstName = "";
        this.legalFirstName = "";
        this.lastName = "";
        this.email = "";
        this.timeCreated = Instant.now();
        this.numSwaps = 0;
        this.rating = 0;
        this.paymentMethodsSetup = createDefaultPaymentMethods();
        this.chats = new TreeMap<>();
        this.draftedProducts = new ArrayList<>();
        this.publishedProducts = new ArrayList<>();
        this.likedProducts = new ArrayList<>();
        this.mutedUsers = new ArrayList<>();
        this.blockedUsers = new ArrayList<>();

    }

    // this constructor is for when a new user registers
    public User (String username, String preferredFirstName, String legalFirstName, String lastName, String email, EnumMap<PaymentMethods, Boolean> paymentMethodsSetup) {
        this.username = username;
        this.preferredFirstName = preferredFirstName;
        this.legalFirstName = legalFirstName;
        this.lastName = lastName;
        this.email = email;
        this.timeCreated = Instant.now();
        numSwaps = 0;
        rating = 0;
        this.paymentMethodsSetup = paymentMethodsSetup;
        this.chats = new TreeMap<>();
        this.draftedProducts = new ArrayList<>();
        this.publishedProducts = new ArrayList<>();
        this.likedProducts = new ArrayList<>();
        this.mutedUsers = new ArrayList<>();
        this.blockedUsers = new ArrayList<>();
    }

    // this constructor is for when we're re-creating a user from the db
    // maybe actually, it's possible that User.createFromBase64String() does this already and this constructor isn't actually needed
    public User (String username, String preferredFirstName, String legalFirstName, String lastName, String email, String university, Instant timeCreated, int numSwaps, short rating, EnumMap<PaymentMethods, Boolean> paymentMethodsSetup, TreeMap<String, ArrayList<UUID>> chats, ArrayList<UUID> draftedProducts, ArrayList<UUID> publishedProducts, ArrayList<UUID> likedProducts, ArrayList<UUID> mutedUsers, ArrayList<UUID> blockedUsers) {
        this.username = username;
        this.preferredFirstName = preferredFirstName;
        this.legalFirstName = legalFirstName;
        this.lastName = lastName;
        this.email = email;
        this.university = university;
        this.timeCreated = timeCreated;
        this.numSwaps = numSwaps;
        this.rating = rating;
        this.paymentMethodsSetup = paymentMethodsSetup;
        this.chats = chats;
        this.draftedProducts = draftedProducts;
        this.publishedProducts = publishedProducts;
        this.likedProducts = likedProducts;
        this.mutedUsers = mutedUsers;
        this.blockedUsers = blockedUsers;
    }

    private static EnumMap<PaymentMethods, Boolean> createDefaultPaymentMethods () {
        EnumMap<PaymentMethods, Boolean> paymentMethodsSetup = new EnumMap<>(PaymentMethods.class);
        for (PaymentMethods method : PaymentMethods.values()) {
            paymentMethodsSetup.put(method, false);
        }
        return paymentMethodsSetup;
    }

    public static User createFromBase64String (String encodedUser) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodedUser);
        try (ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes); ObjectInputStream objectIn = new ObjectInputStream(bytesIn)) {
            return (User) objectIn.readObject();
        } catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println(e);
            e.printStackTrace();
        }
        return null;
    }

    public static User getUserFromUsername (String username) {
        return DynamoDBHandler.getUserItem(username);
    }

    public String toBase64String () {
        Base64.Encoder encoder = Base64.getEncoder();
        try (ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(); ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut)) {
            objectOut.writeObject(this);
            return encoder.encodeToString(bytesOut.toByteArray());
        } catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
        }
        return null;
    }

    public String getLegalFirstName () {
        return legalFirstName;
    }

    public void setLegalFirstName (String legalFirstName) {
        this.legalFirstName = legalFirstName;
    }

    public String getLastName () {
        return lastName;
    }

    public void setLastName (String lastName) {
        this.lastName = lastName;
    }

    public String getEmail () {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getUniversity () {
        return university;
    }

    public void setUniversity (String university) {
        this.university = university;
    }

    public Instant getTimeCreated () {
        return timeCreated;
    }

    public void setTimeCreated (Instant timeCreated) {
        this.timeCreated = timeCreated;
    }

    public int getNumSwaps () {
        return numSwaps;
    }

    public void setNumSwaps (int numSwaps) {
        this.numSwaps = numSwaps;
    }

    public short getRating () {
        return rating;
    }

    public void setRating (short rating) {
        this.rating = rating;
    }

    public EnumMap<PaymentMethods, Boolean> getPaymentMethodsSetup () {
        return paymentMethodsSetup;
    }

    public void setPaymentMethodsSetup (EnumMap<PaymentMethods, Boolean> paymentMethodsSetup) {
        this.paymentMethodsSetup = paymentMethodsSetup;
    }

    public ArrayList<UUID> getDraftedProducts () {
        return draftedProducts;
    }

    public void setDraftedProducts (ArrayList<UUID> draftedProducts) {
        this.draftedProducts = draftedProducts;
    }

    public ArrayList<UUID> getPublishedProducts () {
        return publishedProducts;
    }

    public void setPublishedProducts (ArrayList<UUID> publishedProducts) {
        this.publishedProducts = publishedProducts;
    }

    public ArrayList<UUID> getLikedProducts () {
        return likedProducts;
    }

    public void setLikedProducts (ArrayList<UUID> likedProducts) {
        this.likedProducts = likedProducts;
    }

    public ArrayList<UUID> getMutedUsers () {
        return mutedUsers;
    }

    public void setMutedUsers (ArrayList<UUID> mutedUsers) {
        this.mutedUsers = mutedUsers;
    }

    public ArrayList<UUID> getBlockedUsers () {
        return blockedUsers;
    }

    public void setBlockedUsers (ArrayList<UUID> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public void addChat (String otherUsername, int chatOrder) {
    }

    public TreeMap<String, ArrayList<UUID>> getChats () {
        return chats;
    }

    public void setChats (TreeMap<String, ArrayList<UUID>> chats) {
        this.chats = chats;
    }

}
