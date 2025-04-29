package com.agora.app.backend.lambda;

public enum DynamoTables {
    USERS("agora_users","username"),
    PASSWORDS("agora_passwords","hash"),
    CHATS("agora_chats","id"),
    LISTINGS("agora_listings","id"),
    IMAGES("agora_images", "id"),
    IMAGE_CHUNKS("agora_image_chunks", "id"),
    MESSAGE_BLOCKS("agora_message_blocks", "id"),
    OFFLINE_MESSAGES("agora_offline_messages", "username");

    public final String tableName;
    public final String partitionKeyName;

    private DynamoTables(String tableName, String partitionKeyName) {
        this.tableName = tableName;
        this.partitionKeyName = partitionKeyName;
    }

    public static DynamoTables getEnumFromTableName (String tableName) {
        return switch (tableName) {
            case "agora_users" -> USERS;
            case "agora_passwords" -> PASSWORDS;
            case "agora_chats" -> CHATS;
            case "agora_listings" -> LISTINGS;
            case "agora_images" -> IMAGES;
            case "agora_image_chunks" -> IMAGE_CHUNKS;
            case "agora_message_blocks" -> MESSAGE_BLOCKS;
            case "agora_offline_messages" -> OFFLINE_MESSAGES;
            default -> null;
        };
    }
}
