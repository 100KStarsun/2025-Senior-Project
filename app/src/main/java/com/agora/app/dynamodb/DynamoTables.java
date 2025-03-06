package com.agora.app.dynamodb;

public enum DynamoTables {
    USERS("agora_users","username"),
    PASSWORDS("agora_passwords","hash"),
    CHATS("agora_chats","uuid"),
    PRODUCTS("agora_products","uuid");

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
            case "agora_products":
                return PRODUCTS;
            default:
                return null;

        }
    }
}
