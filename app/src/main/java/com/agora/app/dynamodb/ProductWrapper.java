package com.agora.app.dynamodb;

import com.agora.app.backend.base.Product;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class ProductWrapper {
    /**
     * The uuid of the {@code Product} object. This is unique among all products and as such, is the partition key for the product table in the database.
     */
    private String uuid;
    /**
     * The Base64-String-based representation of the entire {@code Product} object, as created by {@code toBase64String()} in the {@code Product} class.
     *
     * @see Product#toBase64String()
     */
    private String productBase64;

    /**
     * Default constructor, not used, only included to fully implement the {@code @DynamoDbBean} requirements. Creates empty strings for {@code uuid} and {@code productBase64}.
     */
    public ProductWrapper () {
        this.uuid = "";
        this.productBase64 = "";
    }

    /**
     * Grabs the uuid and base64 String representation of the {@code Product} object passed and makes a DynamoDB-friendly object from it
     *
     * @param product The {@code Product} object to be wrapped before being put into the DynamoDB table for products
     */
    public ProductWrapper (Product product) {
        this.uuid = product.getUUID().toString();
        this.productBase64 = product.toBase64String();
    }

    /**
     * Returns the uuid of the {@code Product} object being (un)wrapped
     *
     * @return the {@code String} object that holds the uuid of the {@code Product} object being (un)wrapped
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute("uuid")
    public String getUuid () {
        return this.uuid;
    }

    /**
     * Returns the base64-encoded {@code Product} object as a {@code String} object
     *
     * @return The base64-encoded {@code String} object that represents the {@code Product} object being (un)wrapped
     */
    @DynamoDbAttribute("base64")
    public String getProductBase64 () {
        return this.productBase64;
    }

    /**
     * Manually sets the uuid of the wrapped {@code Product} object. Only implemented to satisfy {@code @DynamoDbBean} requirements.
     *
     * @param uuid the username of the {@code Product} object
     */
    public void setUuid (String uuid) {
        this.uuid = uuid;
    }

    /**
     * Manually sets the base64 string of the wrapped {@code Product} object. Only implemented to satisfy {@code @DynamoDbBean} requirements.
     *
     * @param productBase64 the base64 string representation of the {@code Product} object
     */
    public void setProductBase64 (String productBase64) {
        this.productBase64 = productBase64;
    }
}
