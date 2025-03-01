package com.agora.app.lambda;


import com.agora.app.dynamodb.DynamoTables;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class LambdaHandler {

    private static String dynamoDBInteractionFunctionName = "dynamoInteractions";
    public static String homeDir = System.getProperty("user.home");
    public static String agoraTempDir = "\\.agora\\";
    private static String[] noDataOperations = {"BATCH_GET", "DELETE", "GET"};
    private static Region awsRegion = Region.US_EAST_2; // We will only be using stuff in the us_east_2 region as this region is based in Ohio
    private static LambdaClient awsLambda = LambdaClient.builder()
                                                        .httpClient(UrlConnectionHttpClient.create())
                                                        .region(awsRegion)
                                                        .build();

    /* Example JSON for BATCH_GET operation
     * {
     *   "Operation": "BATCH_GET",
     *   "RequestItems": {
     *     "agora_users": {
     *       "Keys": [
     *         {
     *           "username": {
     *             "S": "lrl47"
     *           }
     *         },
     *         {
     *           "username": {
     *             "S": "ssh115"
     *           }
     *         }
     *       ]
     *     },
     *     "agora_passwords": {
     *       "Keys": [
     *         {
     *           "hash": {
     *             "S": "f58fa3df820114f56e1544354379820cff464c9c41cb3ca0ad0b0843c9bb67ee"
     *           }
     *         }
     *       ]
     *     }
     *   }
     * }
     */

    /* Example JSON for GET/DELETE operation
     * {
     *   "Operation": "GET",
     *   "TableName": "agora_users",
     *   "Key": {
     *     "username": {
     *       "S": "lrl47"
     *     }
     *   }
     * }
     */

    /* Example JSON for PUT operation
     * {
     *   "Operation": "PUT",
     *   "TableName": "agora_users",
     *   "Item": {
     *     "username": {
     *       "S": "nrm98"
     *     },
     *     "base64": {
     *       "S": "not_real_base_64_this_is_purely_a_test"
     *     }
     *   }
     * }
     */

    public static JSONObject invoke (HashMap<DynamoTables, HashMap<String, String>> data, Operations operation) {
        String filename = "";
        try {
            JSONObject jsonObj = new JSONObject();
            // if there is only one table AND one key value pair for that table -> GET, PUT, DELETE
            if (data.size() == 1 && data.get(data.keySet().iterator().next()).size() == 1 && operation.isSingleOp) {
                filename = "lambdaGetPayload.json";
                DynamoTables table = data.keySet().iterator().next();
                String key = data.get(table).keySet().iterator().next();
                String value = data.get(table).get(key);

                jsonObj.put("TableName", table.tableName);

                if (operation.isDataCarryingOp) {
                    jsonObj.put("Item", buildSingleKeyValueJSON(table.partitionKeyName, key, value));
                } else {
                    jsonObj.put("Key", buildSingleKeyJSON(table.partitionKeyName, key));
                }
            } else if (operation.equals(Operations.BATCH_GET)) {
                filename = "lambdaBatchGetPayload.json";
                JSONObject tableItems = new JSONObject();
                for (DynamoTables table : data.keySet()) {
                    HashMap<String, String> items = data.get(table);
                    JSONArray keyList = new JSONArray();
                    for (String key : items.keySet()) {
                        keyList.put(buildSingleKeyJSON(table.partitionKeyName, key));
                    }
                    tableItems.put(table.tableName, new JSONObject().put("Keys", keyList));
                }
                jsonObj.put("RequestItems", tableItems);
            } else if (operation.equals(Operations.BATCH_DELETE) || operation.equals(Operations.BATCH_PUT)) {
                filename = "lambdaBatchDeletePutPayload.json";
                JSONObject tableItems = new JSONObject();
                for (DynamoTables table : data.keySet()) {
                    HashMap<String, String> items = data.get(table);
                    for (String key : items.keySet()) {
                        if (operation.isDataCarryingOp) {
                            tableItems.put("PutRequest", new JSONObject().put("Item", buildSingleKeyValueJSON(table.partitionKeyName, key, items.get(key))));
                        } else {
                            tableItems.put("DeleteRequest", new JSONObject().put("Item", buildSingleKeyJSON(table.partitionKeyName, key)));
                        }
                    }
                }
                jsonObj.put("RequestItems", tableItems);
            }
            jsonObj.put("Operation", operation);
            String jsonString = jsonObj.toString();
            FileWriter fw = new FileWriter(homeDir + agoraTempDir + filename);
            fw.write(jsonObj.toString(4));
            fw.close();

            SdkBytes payload = SdkBytes.fromUtf8String(jsonString);
            String response = awsLambda.invoke(makeRequest(payload)).payload().asUtf8String();
            return new JSONObject(response);

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

    private static JSONObject buildSingleKeyJSON (String partitionKeyName, String key) throws JSONException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(partitionKeyName, new JSONObject("{'S':'" + key + "'}"));
        return jsonObj;
    }

    private static JSONObject buildSingleKeyValueJSON (String partitionKeyName, String key, String value) throws JSONException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(partitionKeyName, new JSONObject("{'S':'" + key + "'}"));
        jsonObj.put("base64", new JSONObject("{'S':'" + value + "'}"));
        return jsonObj;
    }
}
