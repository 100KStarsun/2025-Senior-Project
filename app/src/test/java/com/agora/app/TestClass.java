package com.agora.app;
import com.agora.app.backend.LoginHandler;
import com.agora.app.backend.base.Password;
import com.agora.app.backend.base.PaymentMethods;
import com.agora.app.backend.base.User;
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

        Password passShaHex = new Password(password, caseID);

        DynamoDBHandler.putUserItem(demoUser);
        DynamoDBHandler.putPasswordItem(passShaHex);
        User user = DynamoDBHandler.getUserItem(caseID);
        assert !user.toString().isEmpty();
    }

    @Test
    public void testLoginHandler () throws NoSuchAlgorithmException {
        String trueUsername = "lrl47";
        String attemptedUsername = "lrl47";
        String attemptedPassword = "abc123";
        assert LoginHandler.login(attemptedUsername, attemptedPassword);
    }
}
