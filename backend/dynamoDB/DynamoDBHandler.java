package dynamoDB;

import java.util.Map;

import javax.swing.plaf.synth.Region;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * To get an item from an Amazon DynamoDB table using the AWS SDK for Java V2,
 * its better practice to use the
 * Enhanced Client, see the EnhancedGetItem example.
 */
public class DynamoDBHandler {

    public static Region awsRegion = Region.US_EAST_2; // We will only be using stuff in the us_east_2 region as this region is based in Ohio

    /*
     * DynamoDbClient.builder() method code source: https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb/GetItem.java#L6
     * getItem method API: https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/dynamodb/DynamoDbClient.html#getItem(software.amazon.awssdk.services.dynamodb.model.GetItemRequest)
     */
    public static Map<String, AttributeValue> getItem (String tableName, String key, String keyValue) {
        try (DynamoDbClient dynamoDBClient = DynamoDBClient.builder().region(awsRegion).build();) {
            // need to build `request` here
            Map<String, AttributeValue> returnedItem = dyanmoDBClient.getItem(request).item();
            // if there is no matching item, `returnedItem` will be `null`
            return returnedItem;

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}