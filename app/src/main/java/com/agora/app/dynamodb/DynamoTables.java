package com.agora.app.dynamodb;

public enum DynamoTables {
    USERS("agora_users","username"),
    PASSWORDS("agora_passwords","password"),
    CHATS("agora_chats","uuid"),
    PRODUCTS("agora_products","uuid");

    public final String tableName;
    public final String partitionKeyName;

    private DynamoTables(String tableName, String partitionKeyName) {
        this.tableName = tableName;
        this.partitionKeyName = partitionKeyName;
    }
}
