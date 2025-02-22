package com.agora.app.backend.base;

import com.agora.app.dynamodb.DynamoDBHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Locale;
import java.util.TreeMap;
import java.util.UUID;


public class User implements Serializable {

    private String username;
    private String preferredFirstName;
    private String legalFirstName;
    private String lastName;
    private String email;
    private final Date timeCreated;
    private int numSwaps;
    private short rating;
    private EnumMap<PaymentMethods, Boolean> paymentMethodsSetup; // boolean is whether the user has this setup
    private TreeMap<String, ArrayList<UUID>> chats; // key is the username of the other person, the ArrayList is a list of `chatUUID`s that are in the db
    private ArrayList<UUID> draftedProducts; // a list of UUIDs of products the user has drafted
    private ArrayList<UUID> publishedProducts; // a list of UUIDs of products the user has published
    private ArrayList<UUID> likedProducts; // a list of UUIDs of all products the user has liked
    private ArrayList<UUID> mutedUsers; // a list of UUIDs of all users that this user has muted (i.e. no notifications at all for new messages, but they still get sent)
    private ArrayList<UUID> blockedUsers; // a list of UUIDs of all users that this user has blocked (i.e. chat is closed and other user doesn't know that this user has blocked them)

    public static final Locale locale = Locale.ENGLISH;

    // this constructor is for when a new user registers
    public User (String username, String preferredFirstName, String legalFirstName, String lastName, String email, EnumMap<PaymentMethods, Boolean> paymentMethodsSetup) {
        this.username = username;
        this.preferredFirstName = preferredFirstName;
        this.legalFirstName = legalFirstName;
        this.lastName = lastName;
        this.email = email;
        this.timeCreated = Date.from(Instant.now());
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

    private static EnumMap<PaymentMethods, Boolean> createDefaultPaymentMethods () {
        EnumMap<PaymentMethods, Boolean> paymentMethodsSetup = new EnumMap<>(PaymentMethods.class);
        for (PaymentMethods method : PaymentMethods.values()) {
            paymentMethodsSetup.put(method, false);
        }
        return paymentMethodsSetup;
    }

    public static User getUserFromUsername (String username) {
        return DynamoDBHandler.getUserItem(username);
    }

    public static User createFromBase64String (String encodedUser) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodedUser);
        try (ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes); ObjectInputStream objectIn = new ObjectInputStream(bytesIn)) {
            return (User) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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

    @Override
    public boolean equals (Object obj) {
        try {
            return this.toString().equals(((User) obj).toString());
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public String toString () {
        return "Username: " + this.username + ", Name: " + this.preferredFirstName + " " + this.lastName + " (Legal First Name: " + this.legalFirstName + "), Rating: " + this.rating + ", Swaps: " + this.numSwaps + ", Member since: " + this.timeCreatedToString();
    }

    // will output string in the form DD MMMM YYYY
    public String timeCreatedToString () {
        Calendar cal = new Calendar.Builder().setInstant(this.timeCreated).build();
        return cal.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG_FORMAT, User.locale) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, User.locale) + " " + cal.getDisplayName(Calendar.YEAR, Calendar.LONG_FORMAT, User.locale);
    }

    public String getUsername () { return username; }

    public void setUsername (String username) { this.username = username; }

    public String getPreferredFirstName () { return preferredFirstName; }

    public void setPreferredFirstName (String preferredFirstName) { this.preferredFirstName = preferredFirstName; }

    public String getLegalFirstName () { return legalFirstName; }

    public void setLegalFirstName (String legalFirstName) { this.legalFirstName = legalFirstName; }

    public String getLastName () { return lastName; }

    public void setLastName (String lastName) { this.lastName = lastName; }

    public String getEmail () { return email; }

    public void setEmail (String email) { this.email = email; }

    public Date getTimeCreated () { return timeCreated; }

    public int getNumSwaps () { return numSwaps; }

    public void setNumSwaps (int numSwaps) { this.numSwaps = numSwaps; }

    public short getRating () { return rating; }

    public void setRating (short rating) { this.rating = rating; }

    public EnumMap<PaymentMethods, Boolean> getPaymentMethodsSetup () { return paymentMethodsSetup; }

    public void setPaymentMethodsSetup (EnumMap<PaymentMethods, Boolean> paymentMethodsSetup) { this.paymentMethodsSetup = paymentMethodsSetup; }

    public TreeMap<String, ArrayList<UUID>> getChats () { return chats; }

    public void setChats (TreeMap<String, ArrayList<UUID>> chats) { this.chats = chats; }

    public ArrayList<UUID> getDraftedProducts () { return draftedProducts; }

    public void setDraftedProducts (ArrayList<UUID> draftedProducts) { this.draftedProducts = draftedProducts; }

    public ArrayList<UUID> getPublishedProducts () { return publishedProducts; }

    public void setPublishedProducts (ArrayList<UUID> publishedProducts) { this.publishedProducts = publishedProducts; }

    public ArrayList<UUID> getLikedProducts () { return likedProducts; }

    public void setLikedProducts (ArrayList<UUID> likedProducts) { this.likedProducts = likedProducts; }

    public ArrayList<UUID> getMutedUsers () { return mutedUsers; }

    public void setMutedUsers (ArrayList<UUID> mutedUsers) { this.mutedUsers = mutedUsers; }

    public ArrayList<UUID> getBlockedUsers () { return blockedUsers; }

    public void setBlockedUsers (ArrayList<UUID> blockedUsers) { this.blockedUsers = blockedUsers; }
}
