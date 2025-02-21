package com.agora.app;
import com.agora.app.backend.Password;
import com.agora.app.backend.PaymentMethods;
import com.agora.app.backend.User;
import com.agora.app.dynamodb.DynamoDBHandler;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EnumMap;

public class TestClass {
    static String caseEmailDomain = "@case.edu";
    @Test
    public void puttingAndGetting () throws NoSuchAlgorithmException {
        String caseID = "lrl47";
        String legalFirst = "Levi";
        String preferredFirst = "";
        String last = "Ladd";
        String[] bools = {"y","y","n","y","n","n","n","y","n"}; //PayPal, Zelle, CashApp, Venmo, Apple Pay, Samsung Pay, Google Pay, Cash, Check
        String password = "abc123";
        preferredFirst = preferredFirst.isEmpty() ? legalFirst : preferredFirst;
        EnumMap<PaymentMethods, Boolean> paymentMethodsSetup = new EnumMap<>(PaymentMethods.class);
        int index = 0;
        for (PaymentMethods method : PaymentMethods.values()) {
            paymentMethodsSetup.put(method, bools[index].equalsIgnoreCase("y"));
            index++;
        }
        User demoUser = new User(caseID, preferredFirst, legalFirst, last, caseID + caseEmailDomain, paymentMethodsSetup);

        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashbytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        String sha3Hex = bytesToHex(hashbytes);
        Password passShaHex = new Password(sha3Hex, caseID);

        DynamoDBHandler.putUserItem(demoUser);
        User user = DynamoDBHandler.getUserItem(caseID);
        assert user != null;
    }
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
