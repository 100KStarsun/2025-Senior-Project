package com.agora.app.backend.base;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
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
}
