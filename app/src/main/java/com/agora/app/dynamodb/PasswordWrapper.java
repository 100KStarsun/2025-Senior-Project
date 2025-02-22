package com.agora.app.dynamodb;

import com.agora.app.backend.base.Password;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class PasswordWrapper {
    private String hash;
    private String passwordBase64;

    public PasswordWrapper () {
        this.hash = "";
        this.passwordBase64 = "";
    }

    public PasswordWrapper (Password password) {
        this.hash = password.getHash();
        this.passwordBase64 = password.toBase64String();
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("hashcode")
    public String getHash () {
        return hash;
    }

    @DynamoDbAttribute("base64")
    public String getPasswordBase64 () {
        return passwordBase64;
    }

    public void setHash (String hash) {
        this.hash = hash;
    }

    public void setPasswordBase64 (String passwordBase64) {
        this.passwordBase64 = passwordBase64;
    }
}
