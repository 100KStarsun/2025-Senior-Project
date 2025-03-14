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

public class Product implements Serializable {
    private static final long serialVersionUID = 2042010294253052140L;

    public static String productsTableName = "agora_products";

    private UUID productUUID;
    private String title;
    private Date listingTime;
    private double price;
    private String description;
    private String sellerDisplayName;
    private String sellerUsername;
    private String typeOfProduct;
    private int quantity;
    private boolean hasInfiniteAvailable;
    private boolean isPublished;
    private boolean isAcceptingCash;
    private boolean isTradable;
    private ArrayList<UUID> previousBuyers;
    private String[] tags;

    //Every field provided
    public Product(UUID uuid, String title, Date listingTime, double price, String desc, String displayName, String username, String type, int quantity, boolean hasInfinite, boolean published, boolean acceptingCash, boolean tradable, ArrayList<UUID> previousBuyers, String[] tags) {
        this.productUUID = uuid;
        this.title = title;
        this.listingTime = listingTime;
        this.price = price;
        this.description = desc;
        this.sellerDisplayName = displayName;
        this.sellerUsername = username;
        this.typeOfProduct = type;
        this.quantity = quantity;
        this.hasInfiniteAvailable = hasInfinite;
        this.isPublished = published;
        this.isAcceptingCash = acceptingCash;
        this.isTradable = tradable;
        this.previousBuyers = previousBuyers;
        this.tags = tags;
    }
    //Defaults where applicable
    public Product(UUID uuid, String title, double price, String desc, String displayName, String username, String type, String[] tags) {
        this.productUUID = uuid;
        this.title = title;
        this.listingTime = Date.from(Instant.now());
        this.price = price;
        this.description = desc;
        this.sellerDisplayName = displayName;
        this.sellerUsername = username;
        this.typeOfProduct = type;
        this.quantity = 1;
        this.hasInfiniteAvailable = false;
        this.isPublished = true;
        this.isAcceptingCash = true;
        this.isTradable = false;
        this.previousBuyers = new ArrayList<>();
        this.tags = tags;
    }
    //Defaults for most things, but allows quantity and infiniteavailabilty
    public Product(UUID uuid, String title, double price, String desc, String displayName, String username, String type, int quantity, boolean infiniteAvailable, String[] tags) {
        this.productUUID = uuid;
        this.title = title;
        this.listingTime = Date.from(Instant.now());
        this.price = price;
        this.description = desc;
        this.sellerDisplayName = displayName;
        this.sellerUsername = username;
        this.typeOfProduct = type;
        this.quantity = quantity;
        this.hasInfiniteAvailable = infiniteAvailable;
        this.isPublished = true;
        this.isAcceptingCash = true;
        this.isTradable = false;
        this.previousBuyers = new ArrayList<>();
        this.tags = tags;
    }
    //Seller information comes from a User object
    public Product(UUID uuid, String title, Date listingTime, double price, String desc, User user, String type, int quantity, boolean hasInfinite, boolean published, boolean acceptingCash, boolean tradable, ArrayList<UUID> previousBuyers, String[] tags) {
        this.productUUID = uuid;
        this.title = title;
        this.listingTime = listingTime;
        this.price = price;
        this.description = desc;
        this.sellerDisplayName = user.getPreferredFirstName();
        this.sellerUsername = user.getUsername();
        this.typeOfProduct = type;
        this.quantity = quantity;
        this.hasInfiniteAvailable = hasInfinite;
        this.isPublished = published;
        this.isAcceptingCash = acceptingCash;
        this.isTradable = tradable;
        this.previousBuyers = previousBuyers;
        this.tags = tags;
    }
    /**
     * Used by the {@code ProductWrapper} class to turn the base64-encoded string back into a {@code Product}, using an {@code ObjectInputStream}, {@code ByteArrayInputStream}, and the {@code Base64.Decoder} class.
     *
     * @param encodedProduct a base64 string that should have been created by the {@code toBase64String()} method
     * @return a {@code Product} object that holds all the data it did before it was converted to a base64 string
     */
    public static Product createFromBase64String (String encodedProduct) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodedProduct);
        try (ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes); ObjectInputStream objectIn = new ObjectInputStream(bytesIn)) {
            return (Product) objectIn.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Used by the {@code ProductWrapper} class to turn a {@code Product} object into a base64-encoded string for easy database storage. This is done using a {@code ObjectOutputStream}, {@code ByteArrayOutputStream}, and the {@code Base64.Encoder} class.
     *
     * @return a string containing the base64 representation of the {@code Product} object this method was called using.
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
    public UUID getUUID () { return this.productUUID; }
    public String getTitle () { return this.title; }
    public Date getListingTime () { return this.listingTime; }
    public double getPrice () { return this.price; }
    public String getDescription () { return this.description; }
    public String getSellerDisplayName() { return this.sellerDisplayName; }
    public String getSellerUsername() { return this.sellerUsername; }
    public String getTypeOfProduct() { return this.typeOfProduct; }
    public int getQuantity() { return this.quantity; }
    public boolean getHasInfiniteAvailable() { return this.hasInfiniteAvailable; }
    public boolean getIsPublished() { return this.isPublished; }
    public boolean getIsAcceptingCash() { return this.isAcceptingCash; }
    public boolean getIsTradable() { return this.isTradable; }
    public ArrayList<UUID> getPreviousBuyers() { return this.previousBuyers; }
    public String[] getTags() { return this.tags; }
    public void setTitle (String title) { this.title = title; }
    public void setPrice (double price) { this.price = price; }
    public void setDescription (String description) { this.description = description; }
    public void setSellerDisplayName (String sellerDisplayName) { this.sellerDisplayName = sellerDisplayName; }
    public void setTypeOfProduct (String typeOfProduct) { this.typeOfProduct = typeOfProduct; }
    public void setQuantity (int quantity) { this.quantity = quantity; }
    public void toggleHasInfiniteAvailable() { this.hasInfiniteAvailable = !this.hasInfiniteAvailable; }
    public void toggleIsPublished() { this.isPublished = !this.isPublished; }
    public void toggleIsAcceptingCash() { this.isAcceptingCash = !this.isAcceptingCash; }
    public void toggleIsTradable() { this.isTradable = !this.isTradable; }
    public void addPreviousBuyer (UUID previousBuyer) { this.previousBuyers.add(previousBuyer); }
    public void setTags (String[] tags) { this.tags = tags; }
}
