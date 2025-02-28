package com.agora.app.lambda;


import org.json.JSONException;
import org.json.JSONObject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import java.io.FileWriter;
import java.io.IOException;

public class LambdaHandler {

    private static String dynamoDBInteractionFunctionName = "dynamoInteractions";
    public static String homeDir = System.getProperty("user.home");
    public static String agoraTempDir = "\\.agora\\";
    private static Region awsRegion = Region.US_EAST_2; // We will only be using stuff in the us_east_2 region as this region is based in Ohio
    private static LambdaClient awsLambda = LambdaClient.builder()
                                                        .httpClient(UrlConnectionHttpClient.create())
                                                        .region(awsRegion)
                                                        .build();

    /* Example JSON for BATCH_GET operation:
     * {
     *   "tableName": "agora_users",
     *   "operation": "BATCH_GET",
     *   "partitionKeys": [
     *     {
     *       "username": {
     *         "S": "lrl47"
     *       }
     *     },
     *     {
     *       "username": {
     *         "S": "ssh115"
     *       }
     *     }
     *   ],
     *   "partitionKey": {}
     * }
     */

    /* Example JSON for GET operation:
     * {
     *   "tableName": "agora_users",
     *   "operation": "GET",
     *   "partitionKey": {
     *     "username": {
     *       "S": "lrl47"
     *     }
     *   },
     *   "partitionKeys": {}
     * }
     */
    public static String getItem (String tableName, String partitionKeyName, String operation, String key) {
        try {
            JSONObject jsonObj = buildSinglePayload(tableName, partitionKeyName, key);
            jsonObj.put("operation", operation);
            String jsonString = jsonObj.toString();
            FileWriter fw = new FileWriter(homeDir + agoraTempDir + "jsonReqPayload.txt");
            fw.write(jsonString);
            fw.close();
            SdkBytes payload = SdkBytes.fromUtf8String(jsonString);
            String response = awsLambda.invoke(makeRequest(payload)).payload().asUtf8String();
            return response;
        } catch (JSONException | IOException ex) {
            return null;
        }
    }

    private static InvokeRequest makeRequest (SdkBytes payload) {
        return InvokeRequest.builder()
                            .functionName(dynamoDBInteractionFunctionName)
                            .payload(payload)
                            .build();
    }

    private static JSONObject buildSinglePayload (String tableName, String partitionKeyName, String key) throws JSONException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("tableName", tableName);
        jsonObj.put("key", buildSingleKeyJSON(partitionKeyName, key));
        return jsonObj;
    }

    private static JSONObject buildSingleKeyJSON (String partitionKeyName, String key) throws JSONException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(partitionKeyName, new JSONObject("'S':'" + key + "'"));
        return jsonObj;
    }
}
