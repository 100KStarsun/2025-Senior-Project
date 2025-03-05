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
import java.util.UUID;

public class Product implements Serializable {

    public static String productsTableName = "agora_products";

    private UUID productUUID;
    private String title;
    private Instant listingTime;
    private double price;
    private String description;
    private UUID sellerUUID;
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

    public UUID getUUID () {
        return this.productUUID;
    }
}
