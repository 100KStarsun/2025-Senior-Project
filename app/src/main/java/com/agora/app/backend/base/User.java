package com.agora.app.backend.base;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
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
import java.util.Collections;
import java.security.SecureRandom;


public class User implements Serializable {
    private static final long serialVersionUID = 2042010294253052140L;


    private String username;
    private String preferredFirstName;
    private String legalFirstName;
    private String lastName;
    private String email;
    private SecureRandom rng = new SecureRandom();
    private byte[] salt;
    private String saltString;
    private final Date timeCreated;
    private int numSwaps;
    private short rating;
    private EnumMap<PaymentMethods, Boolean> paymentMethodsSetup; // boolean is whether the user has this setup
    private TreeMap<String, ArrayList<UUID>> chats; // key is the username of the other person, the ArrayList is a list of `chatUUID`s that are in the db
    private ArrayList<UUID> draftedListings; // a list of UUIDs of listings the user has drafted
    private ArrayList<UUID> publishedListings; // a list of UUIDs of listings the user has published
    private ArrayList<UUID> likedListings; // a list of UUIDs of all listings the user has liked
    private ArrayList<UUID> mutedUsers; // a list of UUIDs of all users that this user has muted (i.e. no notifications at all for new messages, but they still get sent)
    private ArrayList<UUID> blockedUsers; // a list of UUIDs of all users that this user has blocked (i.e. chat is closed and other user doesn't know that this user has blocked them)4
    private ArrayList<Boolean> userPreferences;

    public static final Locale locale = Locale.ENGLISH;

    // this constructor is for when a new user registers
    public User (String username, String preferredFirstName, String legalFirstName, String lastName, String email, EnumMap<PaymentMethods, Boolean> paymentMethodsSetup) {
        this.username = username;
        this.preferredFirstName = preferredFirstName;
        this.legalFirstName = legalFirstName;
        this.lastName = lastName;
        this.email = email;
        this.salt = new byte[16];
        rng.nextBytes(salt);
        this.saltString = Base64.getEncoder().encodeToString(salt);
        this.timeCreated = Date.from(Instant.now());
        numSwaps = 0;
        rating = 0;
        this.paymentMethodsSetup = paymentMethodsSetup;
        this.chats = new TreeMap<>();
        this.draftedListings = new ArrayList<>();
        this.publishedListings = new ArrayList<>();
        this.likedListings = new ArrayList<>();
        this.mutedUsers = new ArrayList<>();
        this.blockedUsers = new ArrayList<>();
        this.userPreferences = new ArrayList<>();
        Collections.fill(userPreferences, false);
    }

    /**
     * Creates a default EnumMap of the appropriate type, with the boolean fields all set to false. This would display as the user not having any valid payment method.
     *
     * @return an {@code EnumMap<PaymentMethods, Boolean>} where all values are set to false
     */
    private static EnumMap<PaymentMethods, Boolean> createDefaultPaymentMethods () {
        EnumMap<PaymentMethods, Boolean> paymentMethodsSetup = new EnumMap<>(PaymentMethods.class);
        for (PaymentMethods method : PaymentMethods.values()) {
            paymentMethodsSetup.put(method, false);
        }
        return paymentMethodsSetup;
    }

    /**
     * Used by the {@code UserWrapper} class to turn the base64-encoded string back into a {@code User}, using an {@code ObjectInputStream}, {@code ByteArrayInputStream}, and the {@code Base64.Decoder} class.
     *
     * @param encodedUser a base64 string that should have been created by the {@code toBase64String()} method
     * @return a {@code User} object that holds all the data it did before it was converted to a base64 string
     */
    public static User createFromBase64String (String encodedUser) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodedUser);
        try (ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes); ObjectInputStream objectIn = new ObjectInputStream(bytesIn)) {
            return (User) objectIn.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Exception Decoding User");
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Used by the {@code UserWrapper} class to turn a {@code User} object into a base64-encoded string for easy database storage. This is done using a {@code ObjectOutputStream}, {@code ByteArrayOutputStream}, and the {@code Base64.Encoder} class.
     *
     * @return a string containing the base64 representation of the {@code User} object this method was called using.
     */
    public String toBase64String () {
        Base64.Encoder encoder = Base64.getEncoder();
        try (ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(); ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut)) {
            objectOut.writeObject(this);
            return encoder.encodeToString(bytesOut.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }



    /**
     * Two users are considered the same if they share the same username
     *
     * @param obj the second user with which to compare the first user to
     * @return true if the objects are the same, false otherwise
     */
    @Override
    public boolean equals (Object obj) {
        try {
            return this.username.equals(((User) obj).username);
        } catch (ClassCastException ex) {
            return false;
        }
    }

    @Override
    public String toString () {
        return "Username: " + this.username + ", Name: " + this.preferredFirstName + " " + this.lastName + " (Legal First Name: " + this.legalFirstName + "), Rating: " + this.rating + ", Swaps: " + this.numSwaps + ", Member since: " + this.timeCreatedToString() + ", Salt: " + this.saltString;
    }

    /**
     * Pretty-prints the time this {@code User} was created at
     *
     * @return a string with the date of the user's creation in the form DD MMMM YYYY
     */
    public String timeCreatedToString () {
        Calendar cal = new Calendar.Builder().setInstant(this.timeCreated).build();
        // will output string in the form DD MMMM YYYY
        return cal.get(Calendar.DAY_OF_MONTH) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, User.locale) + " " + cal.get(Calendar.YEAR);
    }

    public String getUsername () { return username; }

    public void setUsername (String username) { this.username = username; }

    public String getSaltString () { return saltString; }

    public TreeMap<String, ArrayList<UUID>> getChats () { return chats; }

    public String getPreferredFirstName () { return this.preferredFirstName; }

    public void setPreferences (ArrayList<Boolean> preferences) { this.userPreferences = preferences;}

    public ArrayList<Boolean> getPreferences() { return this.userPreferences; }

    public void setLikedListings(ArrayList<UUID> likedListings) {
        this.likedListings = likedListings;
    }

    public ArrayList<UUID> getLikedListings() {
        return likedListings;
    }
}
