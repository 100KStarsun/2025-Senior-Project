package com.agora.app.dynamodb;

import com.agora.app.backend.base.Listing;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class ListingWrapper {
    /**
     * The uuid of the {@code Listing} object. This is unique among all listings and as such, is the partition key for the listing table in the database.
     */
    private String uuid;
    /**
     * The Base64-String-based representation of the entire {@code Listing} object, as created by {@code toBase64String()} in the {@code Listing} class.
     *
     * @see Listing#toBase64String()
     */
    private String listingBase64;

    /**
     * Default constructor, not used, only included to fully implement the {@code @DynamoDbBean} requirements. Creates empty strings for {@code uuid} and {@code listingBase64}.
     */
    public ListingWrapper () {
        this.uuid = "";
        this.listingBase64 = "";
    }

    /**
     * Grabs the uuid and base64 String representation of the {@code Listing} object passed and makes a DynamoDB-friendly object from it
     *
     * @param listing The {@code Listing} object to be wrapped before being put into the DynamoDB table for listings
     */
    public ListingWrapper (Listing listing) {
        this.uuid = listing.getUUID().toString();
        this.listingBase64 = listing.toBase64String();
    }

    /**
     * Returns the uuid of the {@code Listing} object being (un)wrapped
     *
     * @return the {@code String} object that holds the uuid of the {@code Listing} object being (un)wrapped
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("uuid")
    public String getUuid () {
        return this.uuid;
    }

    /**
     * Returns the base64-encoded {@code Listing} object as a {@code String} object
     *
     * @return The base64-encoded {@code String} object that represents the {@code Listing} object being (un)wrapped
     */
    @DynamoDbAttribute("base64")
    public String getListingBase64 () {
        return this.listingBase64;
    }

    /**
     * Manually sets the uuid of the wrapped {@code Listing} object. Only implemented to satisfy {@code @DynamoDbBean} requirements.
     *
     * @param uuid the username of the {@code Listing} object
     */
    public void setUuid (String uuid) {
        this.uuid = uuid;
    }

    /**
     * Manually sets the base64 string of the wrapped {@code Listing} object. Only implemented to satisfy {@code @DynamoDbBean} requirements.
     *
     * @param listingBase64 the base64 string representation of the {@code Listing} object
     */
    public void setListingBase64 (String listingBase64) {
        this.listingBase64 = listingBase64;
    }
}
