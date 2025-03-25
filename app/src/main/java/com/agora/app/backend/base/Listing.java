package com.agora.app.backend.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class Listing implements Serializable {
    private static final long serialVersionUID = 2042010294253052140L;

    public static String listingsTableName = "agora_listings";

    private UUID listingUUID;
    private String title;
    private Date listingTime;
    private double price;
    private String description;
    private String sellerDisplayName;
    private String sellerUsername;
    private String typeOfListing;
    private int quantity;
    private boolean hasInfiniteAvailable;
    private boolean isPublished;
    private boolean isAcceptingCash;
    private boolean isTradable;
    private ArrayList<UUID> previousBuyers;
    private String[] tags;
    private boolean completed;

    //Every field provided
    public Listing(UUID uuid, String title, Date listingTime, double price, String desc, String displayName, String username, String type, int quantity, boolean hasInfinite, boolean published, boolean acceptingCash, boolean tradable, ArrayList<UUID> previousBuyers, String[] tags) {
        this.listingUUID = uuid;
        this.title = title;
        this.listingTime = listingTime;
        this.price = price;
        this.description = desc;
        this.sellerDisplayName = displayName;
        this.sellerUsername = username;
        this.typeOfListing = type;
        this.quantity = quantity;
        this.hasInfiniteAvailable = hasInfinite;
        this.isPublished = published;
        this.isAcceptingCash = acceptingCash;
        this.isTradable = tradable;
        this.previousBuyers = previousBuyers;
        this.tags = tags;
        this.completed = false;
    }
    //Defaults where applicable
    public Listing(UUID uuid, String title, double price, String desc, String displayName, String username, String type, String[] tags) {
        this.listingUUID = uuid;
        this.title = title;
        this.listingTime = Date.from(Instant.now());
        this.price = price;
        this.description = desc;
        this.sellerDisplayName = displayName;
        this.sellerUsername = username;
        this.typeOfListing = type;
        this.quantity = 1;
        this.hasInfiniteAvailable = false;
        this.isPublished = true;
        this.isAcceptingCash = true;
        this.isTradable = false;
        this.previousBuyers = new ArrayList<>();
        this.tags = tags;
        this.completed = false;
    }
    //Defaults for most things, but allows quantity and infiniteavailabilty
    public Listing(UUID uuid, String title, double price, String desc, String displayName, String username, String type, int quantity, boolean infiniteAvailable, String[] tags) {
        this.listingUUID = uuid;
        this.title = title;
        this.listingTime = Date.from(Instant.now());
        this.price = price;
        this.description = desc;
        this.sellerDisplayName = displayName;
        this.sellerUsername = username;
        this.typeOfListing = type;
        this.quantity = quantity;
        this.hasInfiniteAvailable = infiniteAvailable;
        this.isPublished = true;
        this.isAcceptingCash = true;
        this.isTradable = false;
        this.previousBuyers = new ArrayList<>();
        this.tags = tags;
        this.completed = false;
    }
    //Seller information comes from a User object
    public Listing(UUID uuid, String title, Date listingTime, double price, String desc, User user, String type, int quantity, boolean hasInfinite, boolean published, boolean acceptingCash, boolean tradable, ArrayList<UUID> previousBuyers, String[] tags) {
        this.listingUUID = uuid;
        this.title = title;
        this.listingTime = listingTime;
        this.price = price;
        this.description = desc;
        this.sellerDisplayName = user.getPreferredFirstName();
        this.sellerUsername = user.getUsername();
        this.typeOfListing = type;
        this.quantity = quantity;
        this.hasInfiniteAvailable = hasInfinite;
        this.isPublished = published;
        this.isAcceptingCash = acceptingCash;
        this.isTradable = tradable;
        this.previousBuyers = previousBuyers;
        this.tags = tags;
        this.completed = false;
    }
    /**
     * Used by the {@code ListingWrapper} class to turn the base64-encoded string back into a {@code Listing}, using an {@code ObjectInputStream}, {@code ByteArrayInputStream}, and the {@code Base64.Decoder} class.
     *
     * @param encodedListing a base64 string that should have been created by the {@code toBase64String()} method
     * @return a {@code Listing} object that holds all the data it did before it was converted to a base64 string
     */
    public static Listing createFromBase64String (String encodedListing) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodedListing);
        try (ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes); ObjectInputStream objectIn = new ObjectInputStream(bytesIn)) {
            return (Listing) objectIn.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Used by the {@code ListingWrapper} class to turn a {@code Listing} object into a base64-encoded string for easy database storage. This is done using a {@code ObjectOutputStream}, {@code ByteArrayOutputStream}, and the {@code Base64.Encoder} class.
     *
     * @return a string containing the base64 representation of the {@code Listing} object this method was called using.
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

    //1 million getters + setters
    public UUID getUUID () { return this.listingUUID; }
    public String getTitle () { return this.title; }
    public Date getListingTime () { return this.listingTime; }
    public double getPrice () { return this.price; }
    public String getDescription () { return this.description; }
    public String getSellerDisplayName() { return this.sellerDisplayName; }
    public String getSellerUsername() { return this.sellerUsername; }
    public String getTypeOfListing() { return this.typeOfListing; }
    public int getQuantity() { return this.quantity; }
    public boolean getHasInfiniteAvailable() { return this.hasInfiniteAvailable; }
    public boolean getIsPublished() { return this.isPublished; }
    public boolean getIsAcceptingCash() { return this.isAcceptingCash; }
    public boolean getIsTradable() { return this.isTradable; }
    public ArrayList<UUID> getPreviousBuyers() { return this.previousBuyers; }
    public String[] getTags() { return this.tags; }
    public boolean getCompleted() { return this.completed; }
    public void setTitle (String title) { this.title = title; }
    public void setPrice (double price) { this.price = price; }
    public void setDescription (String description) { this.description = description; }
    public void setSellerDisplayName (String sellerDisplayName) { this.sellerDisplayName = sellerDisplayName; }
    public void setTypeOfListing (String typeOfListing) { this.typeOfListing = typeOfListing; }
    public void setQuantity (int quantity) { this.quantity = quantity; }
    public void toggleHasInfiniteAvailable() { this.hasInfiniteAvailable = !this.hasInfiniteAvailable; }
    public void toggleIsPublished() { this.isPublished = !this.isPublished; }
    public void toggleIsAcceptingCash() { this.isAcceptingCash = !this.isAcceptingCash; }
    public void toggleIsTradable() { this.isTradable = !this.isTradable; }
    public void addPreviousBuyer (UUID previousBuyer) { this.previousBuyers.add(previousBuyer); }
    public void setTags (String[] tags) { this.tags = tags; }
    public void toggleCompleted() { this.completed = !this.completed; }
}
