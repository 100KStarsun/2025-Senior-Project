package com.agora.app.lambda;

import com.agora.app.backend.base.Password;
import com.agora.app.backend.base.Product;
import com.agora.app.backend.base.User;
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
import java.util.Iterator;
import java.util.Map;

public class LambdaHandler {

    private static String dynamoDBInteractionFunctionName = "dynamoInteractions";
    public static String homeDir = System.getProperty("user.home");
    public static String agoraTempDir = "\\.agora\\";
    private static Region awsRegion = Region.US_EAST_2; // We will only be using stuff in the us_east_2 region as this region is based in Ohio
    private static LambdaClient awsLambda = LambdaClient.builder()
                                                        .httpClient(UrlConnectionHttpClient.create())
                                                        .region(awsRegion)
                                                        .build();

    /*
     * Example JSON for GET
     * {
     *     "TableName": "agora_users",
     *     "Operation": "GET",
     *     "Key": {"username": {"S": "lrl47"}}
     * }
     *
     * Example JSON for BATCH_GET
     * {
     *     "RequestItems": {
     *         "agora_passwords": {"Keys": [{"hash": {"S": "f58fa3df820114f56e1544354379820cff464c9c41cb3ca0ad0b0843c9bb67ee"}}]},
     *         "agora_users": {"Keys": [
     *             {"username": {"S": "ssh115"}},
     *             {"username": {"S": "lrl47"}}
     *         ]}
     *     },
     *     "Operation": "BATCH_GET"
     * }
     *
     * Example JSON for BATCH_PUT
     * {
     *     "RequestItems": {
     *         "agora_passwords": [
     *             {"PutRequest": {"Item": {
     *                 "base64": {"S": "fake_hash_1 + nrm98.toBase64()"},
     *                 "hash": {"S": "fake_hash_1"}
     *             }}},
     *             {"PutRequest": {"Item": {
     *                 "base64": {"S": "fake_hash_2 + msc135.toBase64()"},
     *                 "hash": {"S": "fake_hash_2"}
     *             }}}
     *         ],
     *         "agora_users": [
     *             {"PutRequest": {"Item": {
     *                 "base64": {"S": "fake_b642"},
     *                 "username": {"S": "msc135"}
     *             }}},
     *             {"PutRequest": {"Item": {
     *                 "base64": {"S": "fake_b641"},
     *                 "username": {"S": "nrm98"}
     *             }}}
     *         ]
     *     },
     *     "Operation": "BATCH_PUT"
     * }
     *
     * Example JSON for BATCH_DELETE
     * {
     *     "RequestItems": {
     *         "agora_passwords": [
     *             {"DeleteRequest": {"Key": {"hash": {"S": "fake_hash_1"}}}},
     *             {"DeleteRequest": {"Key": {"hash": {"S": "fake_hash_2"}}}}
     *         ],
     *         "agora_users": [
     *             {"DeleteRequest": {"Key": {"username": {"S": "msc135"}}}},
     *             {"DeleteRequest": {"Key": {"username": {"S": "def892"}}}}
     *         ]
     *     },
     *     "Operation": "BATCH_DELETE"
     * }
     */

    public static HashMap<String, User> getUsers (String[] usernames) {
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.USERS, usernames, new String[0]);
        JSONObject response = invoke(payload, Operations.BATCH_GET);
        return getUsersFromStruct(jsonToBase64(response));
    }

    public static HashMap<DynamoTables, HashMap<String, String>> putUsers (String[] usernames, String[] base64s) {
        if (usernames.length != base64s.length) {
            throw new IllegalArgumentException("usernames and base64s are not the same length");
        }
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.USERS, usernames, base64s);
        JSONObject response = invoke(payload, Operations.BATCH_PUT);
        return jsonToBase64(response);
    }

    public static HashMap<DynamoTables, HashMap<String, String>> deleteUsers (String[] usernames) {
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.USERS, usernames, new String[0]);
        JSONObject response = invoke(payload, Operations.BATCH_DELETE);
        return jsonToBase64(response);
    }

    public static HashMap<String, Password> getPasswords (String[] hashes) {
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.PASSWORDS, hashes, new String[0]);
        JSONObject response = invoke(payload, Operations.BATCH_GET);
        return getPasswordsFromStruct(jsonToBase64(response));
    }

    public static HashMap<DynamoTables, HashMap<String, String>> putPasswords (String[] hashes, String[] base64s) {
        if (hashes.length != base64s.length) {
            throw new IllegalArgumentException("hashes and base64s are not the same length");
        }
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.PASSWORDS, hashes, base64s);
        JSONObject response = invoke(payload, Operations.BATCH_PUT);
        return jsonToBase64(response);
    }

    public static HashMap<DynamoTables, HashMap<String, String>> deletePasswords (String[] hashes) {
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.PASSWORDS, hashes, new String[0]);
        JSONObject response = invoke(payload, Operations.BATCH_DELETE);
        return jsonToBase64(response);
    }

    public static HashMap<String, Product> getProducts (String[] products) {
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.PASSWORDS, products, new String[0]);
        JSONObject response = invoke(payload, Operations.BATCH_GET);
        return getProductsFromStruct(jsonToBase64(response));
    }

    public static HashMap<DynamoTables, HashMap<String, String>> putProducts (String[] products, String[] base64s) {
        if (products.length != base64s.length) {
            throw new IllegalArgumentException("products and base64s are not the same length");
        }
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.PRODUCTS, products, base64s);
        JSONObject response = invoke(payload, Operations.BATCH_PUT);
        return jsonToBase64(response);
    }

    public static HashMap<DynamoTables, HashMap<String, String>> deleteProducts (String[] products) {
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.PRODUCTS, products, new String[0]);
        JSONObject response = invoke(payload, Operations.BATCH_DELETE);
        return jsonToBase64(response);
    }

    private static HashMap<DynamoTables, HashMap<String, String>> makePayload (DynamoTables table, String[] keys, String[] values) {
        HashMap<String, String> map = new HashMap<>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values.length != 0 ? values[i] : null);
        }
        HashMap<DynamoTables, HashMap<String, String>> payload = new HashMap<>();
        payload.put(table, map);
        return payload;
    }

    public static HashMap<String, User> getUsersFromStruct (HashMap<DynamoTables, HashMap<String, String>> struct) {
        HashMap<String, String> data = struct.get(DynamoTables.USERS);
        if (data.keySet().isEmpty()) {
            return null;
        }
        HashMap<String, User> users = new HashMap<>();
        for (String key : data.keySet()) {
            users.put(key, User.createFromBase64String(data.get(key)));
        }
        return users;
    }

    public static HashMap<String, Password> getPasswordsFromStruct (HashMap<DynamoTables, HashMap<String, String>> struct) {
        HashMap<String, String> data = struct.get(DynamoTables.PASSWORDS);
        if (data.keySet().isEmpty()) {
            return null;
        }
        HashMap<String, Password> passwords = new HashMap<>();
        for (String key : data.keySet()) {
            passwords.put(key, Password.createFromBase64String(data.get(key)));
        }
        return passwords;
    }

    public static HashMap<String, Product> getProductsFromStruct (HashMap<DynamoTables, HashMap<String, String>> struct) {
        HashMap<String, String> data = struct.get(DynamoTables.PRODUCTS);
        if (data.keySet().isEmpty()) {
            return null;
        }
        HashMap<String, Product> products = new HashMap<>();
        for (String key : data.keySet()) {
            products.put(key, Product.createFromBase64String(data.get(key)));
        }
        return products;
    }

    public static JSONObject invoke (HashMap<DynamoTables, HashMap<String, String>> data, Operations operation) {
        String filename = "";
        try {
            JSONObject jsonObj = new JSONObject();
            // if there is only one table AND one key value pair for that table -> GET, PUT, DELETE
            if (data.size() == 1 && data.get(data.keySet().iterator().next()).size() == 1 && operation.isSingleOp) {
                filename = "lambda_" + operation + "_payload.json";
                DynamoTables table = data.keySet().iterator().next();
                String key = data.get(table).keySet().iterator().next();
                String value = data.get(table).get(key);

                jsonObj.put("TableName", table.tableName);

                if (operation.isDataCarryingOp) {
                    jsonObj.put("Item", buildSingleKeyValueJSON(table.partitionKeyName, key, value));
                } else {
                    jsonObj.put("Key", buildSingleKeyJSON(table.partitionKeyName, key));
                }
            } else if (!operation.isSingleOp) {
                filename = "lambda_" + operation + "_payload.json";
                JSONObject tableItems = new JSONObject();
                for (DynamoTables table : data.keySet()) {
                    HashMap<String, String> items = data.get(table);
                    JSONArray list = new JSONArray();
                    for (String key : items.keySet()) {
                        if (operation == Operations.BATCH_GET) {
                            list.put(buildSingleKeyJSON(table.partitionKeyName, key));
                        } else if (operation == Operations.BATCH_PUT) {
                            list.put(new JSONObject().put("PutRequest", new JSONObject().put("Item", buildSingleKeyValueJSON(table.partitionKeyName, key, items.get(key)))));
                        } else if (operation == Operations.BATCH_DELETE) {
                            list.put(new JSONObject().put("DeleteRequest", new JSONObject().put("Key", buildSingleKeyJSON(table.partitionKeyName, key))));
                        } else {
                            throw new RuntimeException("Error: Operation " + operation + " not yet implemented");
                        }
                    }
                    if (operation == Operations.BATCH_GET) {
                        tableItems.put(table.tableName, new JSONObject().put("Keys", list));
                    } else {
                        tableItems.put(table.tableName, list);
                    }
                }
                jsonObj.put("RequestItems", tableItems);
            } else {
                throw new IllegalArgumentException("Error: Operation " + operation + " had too many arguments in data.");
            }
            jsonObj.put("Operation", operation);
            String jsonString = jsonObj.toString();
            FileWriter fw = new FileWriter(homeDir + agoraTempDir + filename);
            fw.write(jsonObj.toString(4));
            fw.close();

            SdkBytes payload = SdkBytes.fromUtf8String(jsonString);
            JSONObject response = new JSONObject(awsLambda.invoke(makeRequest(payload)).payload().asUtf8String());
            if (!response.get("statusCode").equals("200")) {
                throw new RuntimeException("Error: Some kind of lambda error");
            }
            response = LambdaHandler.bodyStringToObject(response);

            // check if keys passed match up with keys received
            switch (operation) {
                case PUT:
                case DELETE:
                case BATCH_PUT:
                case BATCH_DELETE:
                    break;
                case BATCH_GET:
                    JSONObject responses = response.getJSONObject("Responses");
                    JSONObject unfoundKeys = new JSONObject();
                    for (DynamoTables table : data.keySet()) {
                        HashMap<String, String> items = data.get(table);
                        JSONArray list = new JSONArray();
                        JSONArray tableResponses = responses.getJSONArray(table.tableName);
                        for (String key : items.keySet()) {
                            boolean wasFound = false;
                            for (int i = 0; i < tableResponses.length(); i++) {
                                if (tableResponses.getJSONObject(i).getJSONObject(table.partitionKeyName).getString("S").equals(key)) {
                                    wasFound = true;
                                    break;
                                }
                            }
                            if (!wasFound) {
                                list.put(key);
                            }
                        }
                        if (list.length() != 0) {
                            unfoundKeys.put(table.tableName, list);
                        }
                    }
                    if (unfoundKeys.length() != 0) {
                        throw new KeyNotFoundException("There were keys that were not found");
                    }

            }

            return response;

        } catch (JSONException | IOException ex) {
            return null;
        }
    }

    /**
     * Isolates the body of the Lambda response from the rest of the clutter, removes metadata from response
     *
     * @param obj the json object representing the response from AWS Lambda
     * @return a JSON Object that represents the body of the response from AWS Lambda
     * @throws JSONException if something goes wrong lol
     */
    private static JSONObject bodyStringToObject (JSONObject obj) throws JSONException {
        JSONObject obj2 = new JSONObject((String)obj.remove("body"));
        obj2.remove("ResponseMetadata");
        return obj2;
    }

    public static HashMap<DynamoTables, HashMap<String, String>> jsonToBase64 (JSONObject obj) {
        try {
            HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>();
            obj = obj.getJSONObject("Responses");
            Iterator<String> iter = obj.keys();
            while (iter.hasNext()) {
                String tableName = iter.next();
                HashMap<String, String> base64Data = new HashMap<>();
                JSONArray tableResponses = obj.getJSONArray(tableName);
                for (int i = 0; i < tableResponses.length(); i++) {
                    JSONObject entry = tableResponses.getJSONObject(i);
                    String pKey = entry.getJSONObject(DynamoTables.getEnumFromTableName(tableName).partitionKeyName).getString("S");
                    String b64 = entry.getJSONObject("base64").getString("S");
                    base64Data.put(pKey, b64);
                }
                data.put(DynamoTables.getEnumFromTableName(tableName), base64Data);
            }
            return data;
        } catch (JSONException ex) {
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
