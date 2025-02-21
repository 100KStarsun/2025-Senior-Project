package com.agora.app.dynamodb;

import com.agora.app.backend.User;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class UserWrapper {

    private String username;
    private String userBase64;

    public UserWrapper () {
        this.username = "";
        this.userBase64 = "";
    }

    public UserWrapper (User user) {
        this.username = user.getUsername();
        this.userBase64 = user.toBase64String();
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("username")
    public String getUsername () {
        return this.username;
    }

    @DynamoDbAttribute("base64")
    public String getUserBase64 () {
        return this.userBase64;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public void setUserBase64 (String userBase64) {
        this.userBase64 = userBase64;
    }

}
