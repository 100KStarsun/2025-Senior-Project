package com.agora.app.backend.lambda;

public enum DynamoTables {
    USERS("agora_users","username"),
    PASSWORDS("agora_passwords","hash"),
    CHATS("agora_chats","uuid"),
    LISTINGS("agora_listings","uuid"),
    IMAGES("agora_images", "id"),
    IMAGE_CHUNKS("agora_image_chunks", "id");

    public final String tableName;
    public final String partitionKeyName;

    private DynamoTables(String tableName, String partitionKeyName) {
        this.tableName = tableName;
        this.partitionKeyName = partitionKeyName;
    }

    public static DynamoTables getEnumFromTableName (String tableName) {
        switch (tableName) {
            case "agora_users":
                return USERS;
            case "agora_passwords":
                return PASSWORDS;
            case "agora_chats":
                return CHATS;
            case "agora_listings":
                return LISTINGS;
            case "agora_images":
                return IMAGES;
            case "agora_image_chunks":
                return IMAGE_CHUNKS;
            default:
                return null;

        }
    }
}
