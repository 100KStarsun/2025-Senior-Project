package com.agora.app;
import com.agora.app.backend.Password;
import com.agora.app.backend.PaymentMethods;
import com.agora.app.backend.User;
import com.agora.app.dynamodb.DynamoDBHandler;
import org.junit.Test;

import java.io.Console;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EnumMap;
import java.util.Scanner;

public class TestClass {
    static String caseEmailDomain = "@case.edu";
    @Test
    public void someUselessTest () throws NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Case ID: ");
        String caseID = "lrl47";
        System.out.println("Enter legal first name: ");
        String legalFirst = "Levi";
        System.out.println("Enter preferred first name (leave blank if none): ");
        String preferredFirst = "";
        preferredFirst = preferredFirst.isEmpty() ? legalFirst : preferredFirst;
        System.out.println("Enter last name: ");
        String last = "Ladd";
        EnumMap<PaymentMethods, Boolean> paymentMethodsSetup = new EnumMap<>(PaymentMethods.class);
        String[] bools = {"y","y","n","y","n","n","n"};
        int index = 0;
        for (PaymentMethods method : PaymentMethods.values()) {
            System.out.println("Do you have " + method.getPaymentMethodDescription() + " setup? [y/n]: ");
            paymentMethodsSetup.put(method, bools[index].equalsIgnoreCase("y"));
            index++;
        }
        User demoUser = new User(caseID, preferredFirst, legalFirst, last, caseID + caseEmailDomain, paymentMethodsSetup);
        System.out.println("Basic account details created.");

        String password;
        do {
            System.out.println("Enter password: ");
            String password1 = "abc123";
            System.out.println("Confirm password: ");
            String password2 = "abc123";
            password = password1.equals(password2) ? password1 : "";
            if (password.isEmpty()) {
                System.out.println("Passwords did not match. Try again.");
            }
        } while (password.isEmpty());

        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashbytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        String sha3Hex = bytesToHex(hashbytes);
        Password passShaHex = new Password(sha3Hex);

        DynamoDBHandler.putUserItem(demoUser);
        assert true;
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
