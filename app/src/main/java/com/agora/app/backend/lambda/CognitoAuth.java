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
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;

public class CognitoAuth {
    
    private static final String identity_pool = "us-east-2:16a37b80-20f5-4c52-865e-790b5c80dc2d";
    private static final Region region = Region.US_EAST_2;

    public static AwsCredentials getTemporaryCredentials() {
        CognitoIdentityClient cognitoClient = CognitoIdentityClient.builder()
                                                                    .region(region)
                                                                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                                                                    .build();
       
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
