package com.agora.app.backend.lambda;

import com.agora.app.backend.base.Image;
import com.agora.app.backend.base.ImageChunk;
import com.agora.app.backend.base.Password;
import com.agora.app.backend.base.Listing;
import com.agora.app.backend.base.User;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class LambdaHandler {

    private static final String dynamoDBInteractionFunctionName = "dynamoInteractions";
    public static final String homeDir = System.getProperty("user.home");
    public static final String agoraTempDir = "\\.agora\\";
    public static final boolean writeOutputs = false;
    private static final Region awsRegion = Region.US_EAST_2; // We will only be using stuff in the us_east_2 region as this region is based in Ohio
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
        if (usernames.length == 0) throw new IllegalArgumentException("usernames cannot be empty");
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.USERS, usernames, new String[0]);
        JSONObject response = invoke(payload, Operations.BATCH_GET);
        return getUsersFromStruct(jsonToBase64(response));
    }

    public static void putUsers (String[] usernames, String[] base64s) {
        if (usernames.length != base64s.length) throw new IllegalArgumentException("usernames and base64s are not the same length");
        if (usernames.length == 0) throw new IllegalArgumentException("usernames cannot be empty");
        if (base64s.length == 0) throw new IllegalArgumentException("base64s cannot be empty");
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.USERS, usernames, base64s);
        invoke(payload, Operations.BATCH_PUT);
    }

    public static void deleteUsers (String[] usernames) {
        if (usernames.length == 0) throw new IllegalArgumentException("Usernames cannot be empty");
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.USERS, usernames, new String[0]);
        invoke(payload, Operations.BATCH_DELETE);
    }

    public static HashMap<String, Password> getPasswords (String[] hashes) {
        if (hashes.length == 0) throw new IllegalArgumentException("hashes cannot be empty");
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.PASSWORDS, hashes, new String[0]);
        JSONObject response = invoke(payload, Operations.BATCH_GET);
        return getPasswordsFromStruct(jsonToBase64(response));
    }

    public static void putPasswords (String[] hashes, String[] base64s) {
        if (hashes.length != base64s.length) throw new IllegalArgumentException("hashes and base64s are not the same length");
        if (hashes.length == 0) throw new IllegalArgumentException("hashes cannot be empty");
        if (base64s.length == 0) throw new IllegalArgumentException("base64s cannot be empty");
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.PASSWORDS, hashes, base64s);
        invoke(payload, Operations.BATCH_PUT);
    }

    public static void deletePasswords (String[] hashes) {
        if (hashes.length == 0) throw new IllegalArgumentException("hashes cannot be empty");
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.PASSWORDS, hashes, new String[0]);
        invoke(payload, Operations.BATCH_DELETE);
    }

    public static HashMap<String, Listing> getListings (String[] listings) {
        if (listings.length == 0) throw new IllegalArgumentException("listings cannot be empty");
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.LISTINGS, listings, new String[0]);
        JSONObject response = invoke(payload, Operations.BATCH_GET);
        return getListingsFromStruct(jsonToBase64(response));
    }

    public static HashMap<String, Listing> scanListings () {
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.LISTINGS, new String[0], new String[0]);
        JSONObject response = invoke(payload, Operations.SCAN);
        return getListingsFromStruct(jsonToBase64(response));
    }

    public static void putListings (String[] listings, String[] base64s) {
        if (listings.length != base64s.length) throw new IllegalArgumentException("listings and base64s are not the same length");
        if (listings.length == 0) throw new IllegalArgumentException("listings cannot be empty");
        if (base64s.length == 0) throw new IllegalArgumentException("base64s cannot be empty");
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.LISTINGS, listings, base64s);
        invoke(payload, Operations.BATCH_PUT);
    }

    public static void deleteListings (String[] listings) {
        if (listings.length == 0) throw new IllegalArgumentException("listings cannot be empty");
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.LISTINGS, listings, new String[0]);
        invoke(payload, Operations.BATCH_DELETE);
    }

    public static HashMap<String, Image> getImages (String[] imageIDs) {
        if (imageIDs.length == 0) throw new IllegalArgumentException("imageIDs cannot be empty");
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.IMAGES, imageIDs, new String[0]);
        HashMap<String, String> chunks = new HashMap<>();
        for (String imgID : imageIDs) {
            String[] chunkIDs = Image.getChunkIDsFromID(imgID);
            for (String chunkID : chunkIDs) {
                chunks.put(chunkID, "");
            }
        }
        payload.put(DynamoTables.IMAGE_CHUNKS, chunks);
        JSONObject request = makeRequestBody(payload, Operations.BATCH_GET);
        if (writeOutputs) {
            try {
                FileWriter fw = new FileWriter(homeDir + agoraTempDir + "image_get.json");
                fw.write(request.toString(4));
                fw.close();
            } catch (IOException | JSONException ex) {}
        }
        JSONObject response = invoke(payload, Operations.BATCH_GET);
        if (writeOutputs) {
            try {
                FileWriter fw = new FileWriter(homeDir + agoraTempDir + "image_get_response.json");
                fw.write(response.toString(4));
                fw.close();
            } catch (IOException | JSONException ex) {}
        }

        return getImagesFromStruct(jsonToBase64(response));
    }

    public static void putImages (Image[] images) {
        if (images.length == 0) throw new IllegalArgumentException("images cannot be empty");
        String[] imageIDs = new String[images.length];
        String[] base64s = new String[images.length];
        String[] imageChunkIDs = new String[0];
        String[] imageChunkBase64s = new String[0];
        for (int i = 0; i < images.length; i++) {
            Image img = images[i];
            imageIDs[i] = img.getId();
            base64s[i] = img.toBase64String();
            int index = imageChunkIDs.length;
            int count = Integer.parseInt(img.getId().substring(38));
            imageChunkIDs = Arrays.copyOf(imageChunkIDs, index + count);
            System.arraycopy(img.getChunkIDs(), 0, imageChunkIDs, index, count);
            imageChunkBase64s = Arrays.copyOf(imageChunkBase64s, index + count);
            System.arraycopy(img.getChunkBase64s(), 0, imageChunkBase64s, index, count);
        }
        putImages(imageIDs, base64s, imageChunkIDs, imageChunkBase64s);
    }

    public static void putImages (String[] imageIDs, String[] base64s, String[] imageChunkIDs, String[] imageChunkBase64s) {
        if (imageIDs.length != base64s.length || imageChunkIDs.length != imageChunkBase64s.length) {
            throw new IllegalArgumentException("imageIDs and base64s are not the same length or their data are not the same length");
        }
        if (imageIDs.length == 0) throw new IllegalArgumentException("imageIDs cannot be empty");
        if (base64s.length == 0) throw new IllegalArgumentException("base64s cannot be empty");
        if (imageChunkIDs.length == 0) throw new IllegalArgumentException("imageChunkIDs cannot be empty");
        if (imageChunkBase64s.length == 0) throw new IllegalArgumentException("imageChunkBase64s cannot be empty");
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.IMAGES, imageIDs, base64s);
        HashMap<String, String> chunks = new HashMap<>();
        for (int i = 0; i < imageChunkIDs.length; i++) {
            chunks.put(imageChunkIDs[i], imageChunkBase64s[i]);
        }
        payload.put(DynamoTables.IMAGE_CHUNKS, chunks);


        JSONObject request = makeRequestBody(payload, Operations.BATCH_PUT);
        if (writeOutputs) {
            try {
                FileWriter fw = new FileWriter(homeDir + agoraTempDir + "image_put.json");
                fw.write(request.toString(4));
                fw.close();
            } catch (IOException | JSONException ex) {}
        }
        invoke(payload, Operations.BATCH_PUT);
    }

    public static HashMap<String, ImageChunk> scanImageChunks () {
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.IMAGE_CHUNKS, new String[0], new String[0]);
        JSONObject response = invoke(payload, Operations.SCAN);
        return getImageChunksFromStruct(jsonToBase64(response));
    }

    public static void deleteImages (String[] imageIDs) {
        if (imageIDs.length == 0) throw new IllegalArgumentException("imageIDs cannot be empty");
        HashMap<DynamoTables, HashMap<String, String>> payload = makePayload(DynamoTables.IMAGES, imageIDs, new String[0]);
        HashMap<String, String> chunks = new HashMap<>();
        for (String imgID : imageIDs) {
            String[] chunkIDs = Image.getChunkIDsFromID(imgID);
            for (String chunkID : chunkIDs) {
                chunks.put(chunkID, "");
            }
        }
        payload.put(DynamoTables.IMAGE_CHUNKS, chunks);
        invoke(payload, Operations.BATCH_DELETE);
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

    public static HashMap<String, Listing> getListingsFromStruct (HashMap<DynamoTables, HashMap<String, String>> struct) {
        HashMap<String, String> data = struct.get(DynamoTables.LISTINGS);
        if (data.keySet().isEmpty()) {
            return null;
        }
        HashMap<String, Listing> listings = new HashMap<>();
        for (String key : data.keySet()) {
            listings.put(key, Listing.createFromBase64String(data.get(key)));
        }
        return listings;
    }

    public static HashMap<String, ImageChunk> getImageChunksFromStruct (HashMap<DynamoTables, HashMap<String, String>> struct) {
        HashMap<String, String> data = struct.get(DynamoTables.IMAGE_CHUNKS);
        if (data.keySet().isEmpty()) {
            return null;
        }
        HashMap<String, ImageChunk> imageChunks = new HashMap<>();
        for (String key : data.keySet()) {
            imageChunks.put(key, new ImageChunk(key, data.get(key)));
        }
        return imageChunks;
    }

    public static HashMap<String, Image> getImagesFromStruct (HashMap<DynamoTables, HashMap<String, String>> struct) {
        HashMap<String, String> metaData = struct.get(DynamoTables.IMAGES);
        HashMap<String, String> chunkData = struct.get(DynamoTables.IMAGE_CHUNKS);
        if (metaData.keySet().isEmpty() || chunkData.keySet().isEmpty()) {
            throw new IllegalArgumentException("Cannot have null images or imageChunks");
        }
        ImageChunk[] allChunks = new ImageChunk[chunkData.size()];
        int index = 0;
        for (String key : chunkData.keySet()) {
            allChunks[index] = new ImageChunk(key, chunkData.get(key));
            index++;
        }
        Arrays.sort(allChunks);
        HashMap<String, Image> images = new HashMap<>();
        for (String imgID : metaData.keySet()) {
            int low = Arrays.binarySearch(allChunks,new ImageChunk(imgID.substring(0,36) + "_00"));
            int count = Integer.parseInt(imgID.substring(38));
            ImageChunk[] chunks = Arrays.copyOfRange(allChunks,low,low + count);
            Image img = Image.fromChunks(chunks);
            images.put(imgID, img);
        }
        return images;
    }

    public static JSONObject makeRequestBody (HashMap<DynamoTables, HashMap<String, String>> data, Operations operation) {
        if (data == null || operation == null) throw new IllegalArgumentException("Cannot have null arguments");
        JSONObject jsonObj = new JSONObject();
        // if there is only one table AND one key value pair for that table -> GET, PUT, DELETE
        if (data.size() == 1 && data.get(data.keySet().iterator().next()).size() == 1 && operation.isSingleOp) {
            DynamoTables table = data.keySet().iterator().next();
            String key = data.get(table).keySet().iterator().next();
            String value = data.get(table).get(key);

            try {
                jsonObj.put("TableName", table.tableName);
            } catch (JSONException ex) {
                throw new IllegalStateException();
            }
            try {
                if (operation.isDataCarryingOp) {
                    jsonObj.put("Item", buildSingleKeyValueJSON(table.partitionKeyName, key, value));
                } else {
                    jsonObj.put("Key", buildSingleKeyJSON(table.partitionKeyName, key));
                }
            } catch (JSONException ex) {
                throw new IllegalStateException();
            }
        } else if (operation == Operations.SCAN) {
            if (data.size() > 1) {
                throw new IllegalArgumentException("Scanning is only allowed for one table at a time, too many tables specified");
            }
            if (data.size() < 1) {
                throw new IllegalArgumentException("Table name is required for scan operation. Please specify exactly one table to scan");
            }
            DynamoTables table = data.keySet().iterator().next();
            try {
                jsonObj.put("TableName", table.tableName);
            } catch (JSONException ex) {
                throw new IllegalStateException();
            }
        } else if (!operation.isSingleOp) {
            JSONObject tableItems = new JSONObject();
            for (DynamoTables table : data.keySet()) {
                HashMap<String, String> items = data.get(table);
                JSONArray list = new JSONArray();
                for (String key : items.keySet()) {
                    try {
                        if (operation == Operations.BATCH_GET) {
                            list.put(buildSingleKeyJSON(table.partitionKeyName, key));
                        } else if (operation == Operations.BATCH_PUT) {
                            list.put(new JSONObject().put("PutRequest", new JSONObject().put("Item", buildSingleKeyValueJSON(table.partitionKeyName, key, items.get(key)))));
                        } else if (operation == Operations.BATCH_DELETE) {
                            list.put(new JSONObject().put("DeleteRequest", new JSONObject().put("Key", buildSingleKeyJSON(table.partitionKeyName, key))));
                        } else {
                            throw new RuntimeException("Error: Operation " + operation + " not yet implemented");
                        }
                    } catch (JSONException ex) {
                        throw new IllegalStateException();
                    }
                }
                try {
                    if (operation == Operations.BATCH_GET) {
                        tableItems.put(table.tableName, new JSONObject().put("Keys", list));
                    } else {
                        tableItems.put(table.tableName, list);
                    }
                } catch (JSONException ex) {
                    throw new NullPointerException("the list of keys is probably null");
                }
            }
            try {
                jsonObj.put("RequestItems", tableItems);
            } catch (JSONException ex) {
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalArgumentException("Error: Operation " + operation + " had too many or not enough arguments in data.");
        }
        try {
            jsonObj.put("Operation", operation);
        } catch (JSONException ex) {
            throw new IllegalStateException();
        }
        return jsonObj;
    }

    public static JSONObject extractKeysMissingFromResponse (HashMap<DynamoTables, HashMap<String, String>> request, JSONObject response) {
        JSONObject unfoundKeys = new JSONObject();
        for (DynamoTables table : request.keySet()) {
            HashMap<String, String> items = request.get(table);
            JSONArray list = new JSONArray();
            JSONArray tableResponses = response.optJSONArray(table.tableName);
            for (String key : items.keySet()) {
                boolean wasFound = false;
                for (int i = 0; i < tableResponses.length(); i++) {
                    if (tableResponses.optJSONObject(i).optJSONObject(table.partitionKeyName).optString("S").equals(key)) {
                        wasFound = true;
                        break;
                    }
                }
                if (!wasFound) {
                    list.put(key);
                }
            }
            if (list.length() != 0) {
                try {
                    unfoundKeys.put(table.tableName, list);
                } catch (JSONException ex) {}
            }
        }
        return unfoundKeys;
    }

    public static JSONObject invoke (HashMap<DynamoTables, HashMap<String, String>> data, Operations operation){
        JSONObject jsonObj = makeRequestBody(data, operation);
        String jsonString = jsonObj.toString();

        boolean requestAgain;
        JSONObject response = new JSONObject();
        do {
            if (writeOutputs) {
                try {
                    FileWriter fw = new FileWriter(homeDir + agoraTempDir + "requestJSON" + System.currentTimeMillis() + ".json");
                    JSONObject tempRequestObj = new JSONObject(jsonString);
                    fw.write(tempRequestObj.toString(4));
                    fw.close();
                } catch (IOException | JSONException ex) {
                    throw new IllegalStateException("Uh oh.");
                }
            }
            SdkBytes payload = SdkBytes.fromUtf8String(jsonString);
            JSONObject localResponse;
            try {
                localResponse = new JSONObject(awsLambda.invoke(makeRequest(payload)).payload().asUtf8String());
            } catch (JSONException ex) {
                throw new IllegalStateException("Didn't get valid JSON from AWS Lambda");
            }
            try {
                localResponse = new JSONObject(localResponse.optString("body"));
            } catch (JSONException ex) {
                throw new IllegalStateException("body of response not formatted correctly");
            }
            localResponse.remove("ResponseMetadata");
            String unprocessed = operation.isDataCarryingOp ? "UnprocessedItems" : (operation == Operations.SCAN ? "LastEvaluatedKey" : "UnprocessedKeys");
            if (localResponse.has(unprocessed)) {
                requestAgain = localResponse.optJSONObject(unprocessed).length() != 0;
            } else {
                requestAgain = false;
            }
            JSONObject newRequest = new JSONObject();
            try {
                String requestKeyword = operation == Operations.SCAN ? "ExclusiveStartKey" : "RequestItems";
                newRequest.put(requestKeyword, localResponse.optJSONObject(unprocessed));
                newRequest.put("Operation", operation);
                if (operation == Operations.SCAN) {
                    newRequest.put("TableName", data.keySet().iterator().next().tableName);
                }
            } catch (JSONException ex) {
                throw new IllegalStateException(unprocessed + " was supposed to have items in it, and yet, it doesn't.");
            }
            if (operation == Operations.BATCH_GET || operation == Operations.SCAN) {
                if (operation == Operations.SCAN) {
                    JSONObject tableData = new JSONObject();
                    try {
                        tableData.put(data.keySet().iterator().next().tableName, localResponse.optJSONArray("Items"));
                        localResponse.put("Responses", tableData);
                    } catch (JSONException ex) {}
                }
                localResponse = localResponse.optJSONObject("Responses");
                Iterator<String> tableIter = localResponse.keys();
                while (tableIter.hasNext()) {
                    String tableName = tableIter.next();
                    JSONArray values = localResponse.optJSONArray(tableName);
                    for (int i = 0; i < values.length(); i++) {
                        try {
                            response.accumulate(tableName, values.opt(i));
                        } catch (JSONException ex) {
                            throw new IllegalArgumentException("JSONException: " + ex.getMessage());
                        }
                    }
                    try {
                        JSONObject singleton = response.getJSONObject(tableName);
                        response.remove(tableName);
                        JSONArray tempArr = new JSONArray();
                        response.put(tableName, tempArr.put(singleton));
                    } catch (JSONException ex) {}
                }
            }

            jsonString = newRequest.toString();
        } while (requestAgain);

        // check if keys passed match up with keys received
        if (operation == Operations.BATCH_GET) {
            JSONObject missingKeys = extractKeysMissingFromResponse(data, response);
            if (missingKeys.length() != 0) {
                throw new KeyNotFoundException(missingKeys.toString());
            }
        }

        if (writeOutputs) {
            try {
                Random randy = new Random();
                FileWriter fw = new FileWriter(homeDir + agoraTempDir + "trueResponse" + System.currentTimeMillis() + ".json");
                fw.write(response.toString(4));
                fw.close();
            } catch (IOException | JSONException ex) {}
        }

        return response;
    }

    public static HashMap<DynamoTables, HashMap<String, String>> jsonToBase64 (JSONObject obj) {
        try {
            HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>();
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

    private static JSONObject buildSingleKeyJSON (String partitionKeyName, String key) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(partitionKeyName, new JSONObject("{'S':'" + key + "'}"));
            return jsonObj;
        } catch (JSONException ex) {
            throw new NullPointerException("key or partitionKeyName was null");
        }
    }

    private static JSONObject buildSingleKeyValueJSON (String partitionKeyName, String key, String value) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(partitionKeyName, new JSONObject("{'S':'" + key + "'}"));
            jsonObj.put("base64", new JSONObject("{'S':'" + value + "'}"));
            return jsonObj;
        } catch (JSONException ex) {
            throw new NullPointerException("key, value, or partitionKeyName was null");
        }
    }

}
