package com.agora.app;
import com.agora.app.backend.LoginHandler;
import com.agora.app.backend.LoginException;
import com.agora.app.backend.base.Password;
import com.agora.app.backend.base.PaymentMethods;
import com.agora.app.backend.base.User;
import com.agora.app.dynamodb.DynamoDBHandler;
import com.agora.app.dynamodb.DynamoTables;
import com.agora.app.lambda.LambdaHandler;
import com.agora.app.lambda.Operations;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.EnumMap;
import java.util.HashMap;

public class TestClass {

    private static String caseID = "lrl47";
    private static String legalFirstName = "Levi";
    private static String preferredFirstName = ""; // leave blank to automatically use legalFirstName
    private static String lastName = "Ladd";
    private static String[] bools = {"n","n","n","n","n","n","n","y","n"}; // PayPal, Zelle, CashApp, Venmo, Apple Pay, Samsung Pay, Google Pay, Cash, Check | Note: everyone has cash enabled by default
    private static String passwordString = "abc123";


    public static String caseEmailDomain = "@case.edu";
    public static String homeDir = System.getProperty("user.home");
    public static String agoraTempDir = "\\.agora\\";

    @Test
    public void puttingAndGettingUserAndPassword () throws IOException {
        // logic for setting preferred name
        TestClass.preferredFirstName = TestClass.preferredFirstName.isEmpty() ? TestClass.legalFirstName : TestClass.preferredFirstName;

        // creating the paymentMethods map
        EnumMap<PaymentMethods, Boolean> paymentMethodsSetup = new EnumMap<>(PaymentMethods.class);
        int index = 0;
        for (PaymentMethods method : PaymentMethods.values()) {
            paymentMethodsSetup.put(method, TestClass.bools[index].equalsIgnoreCase("y"));
            index++;
        }

        // Create user + password
        User demoUser = new User(TestClass.caseID, TestClass.preferredFirstName, TestClass.legalFirstName, TestClass.lastName, TestClass.caseID + TestClass.caseEmailDomain, paymentMethodsSetup);
        Password demoPassword = new Password(TestClass.passwordString, TestClass.caseID);

        // Put user + password into db
        DynamoDBHandler.putUserItem(demoUser);
        DynamoDBHandler.putPasswordItem(demoPassword);

        // retrieve user + password
        User user = DynamoDBHandler.getUserItem(TestClass.caseID);
        Password password = DynamoDBHandler.getPasswordItem(demoPassword.getHash());

        // make sure none of the printed fields are null which /should/ mean we have fully retrieved the user + password
        assert !user.toString().contains("null") && !password.toString().contains("null");
    }

    /**
     * try is excluded here so that if a LoginException gets thrown, the error is easier to pinpoint
     */
    //@Test
    public void testCorrectLogin () throws NoSuchAlgorithmException {
        String attemptedUsername = "lrl47"; // this is correct
        String attemptedPassword = "abc123"; // this is correct
        assert LoginHandler.login(attemptedUsername, attemptedPassword);
    }

    /**
     * Passes if we get a LoginException, fails if not. This is because the password is not correct and that causes .login() to throw a LoginException
     */
    //@Test
    public void testWrongLogin () throws NoSuchAlgorithmException {
        String attemptedUsername = "lrl47"; // this is a valid username
        String attemptedPassword = "123abc"; // this is not the correct password for the username provided
        try {
            LoginHandler.login(attemptedUsername, attemptedPassword);
        } catch (LoginException ex) {
            assert true;
            // return needed because assert keyword doesn't automatically exit method
            return;
        }
        assert false;
    }

    @Test
    public void testLambdaGet () {
        String username = "lrl47";
        HashMap<String, String> items = new HashMap<>(2);
        items.put(username, null);

        HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(2);
        data.put(DynamoTables.USERS, data, Operations.GET);
    }
}
