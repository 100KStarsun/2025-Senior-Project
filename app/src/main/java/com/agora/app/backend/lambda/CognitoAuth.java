package com.agora.app.backend.lambda;


import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.GetIdRequest;
import software.amazon.awssdk.services.cognitoidentity.model.GetIdResponse;
import software.amazon.awssdk.services.cognitoidentity.model.GetCredentialsForIdentityRequest;
import software.amazon.awssdk.services.cognitoidentity.model.GetCredentialsForIdentityResponse;

public class CognitoAuth {
    
    private static final String identity_pool = "us-east-2:0d67b00b-c37e-4db9-9a4f-5daca8747ded";
    private static final Region region = Region.US_EAST_2;

    public static AwsCredentials getTemporaryCredentials() {
        CognitoIdentityClient cognitoClient = CognitoIdentityClient.builder().region(region).build();
       
        GetIdResponse idResponse = cognitoClient.getId(GetIdRequest.builder()
                                                .identityPoolId(identity_pool)
                                                .build());
        String identityId = idResponse.identityId();
        GetCredentialsForIdentityResponse credsResponse = cognitoClient.getCredentialsForIdentity(
                                                GetCredentialsForIdentityRequest.builder()
                                                .identityId(identityId)
                                                .build());
        return AwsSessionCredentials.create(
            credsResponse.credentials().accessKeyId(),
            credsResponse.credentials().secretKey(),
            credsResponse.credentials().sessionToken()
        );
    }
}
