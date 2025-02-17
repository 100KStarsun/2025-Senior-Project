package com.agora.app.backend.dynamodb;

import java.util.Map;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.Key;


public class DynamoDBHandler {

    public static Region awsRegion = Region.US_EAST_2; // We will only be using stuff in the us_east_2 region as this region is based in Ohio

    /*
     * 
     */
    public static Map<String, AttributeValue> getItem (String tableName, String key, String keyValue) {
        try {
            DynamoDbEnhancedClient ddbec = DynamoDbEnhancedClient.create();
            Key keyToPass = Key.builder()
                    .partitionValue("")
                    .build();
            Map<String, AttributeValue> returnedItem = ddbec.getItem(request).item();
            // if there is no matching item, `returnedItem` will be `null`
            return returnedItem;

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}