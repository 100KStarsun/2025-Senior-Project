package com.agora.app;
import com.agora.app.backend.LoginHandler;
import com.agora.app.backend.LoginException;
import com.agora.app.backend.base.Password;
import com.agora.app.backend.base.PaymentMethods;
import com.agora.app.backend.base.User;
import com.agora.app.dynamodb.DynamoDBHandler;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;

public class TestClass {
    private static String caseID = "lrl47";
    private static String legalFirstName = "Levi";
    private static String preferredFirstName = ""; // leave blank to automatically use legalFirstName
    private static String lastName = "Ladd";
    private static String[] bools = {"n","n","n","n","n","n","n","y","n"}; // PayPal, Zelle, CashApp, Venmo, Apple Pay, Samsung Pay, Google Pay, Cash, Check | Note: everyone has cash enabled by default
    private static String passwordString = "xX-yeet-Xx";


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

        // Put user + password into db
        boolean userExistsAlready = false;
        try {
            User test = DynamoDBHandler.getUserItem(caseID);
            if (test != null && !test.toString().contains("null")) {
                userExistsAlready = true;
            }
        } catch (NullPointerException ex) {}

        if (!userExistsAlready) {
            DynamoDBHandler.putUserItem(demoUser);
        }
        User user = DynamoDBHandler.getUserItem(TestClass.caseID);
        Password demoPassword = new Password(TestClass.passwordString, user);

        boolean passwordExistsAlready = false;
        try {
            Password test = DynamoDBHandler.getPasswordItem(new Password(passwordString, caseID).getHash());
            if (test != null && !test.toString().contains("null")) {
                passwordExistsAlready = true;
            }
        } catch (NullPointerException ex) {}

        if (!passwordExistsAlready) {
            DynamoDBHandler.putPasswordItem(demoPassword);
        }
        Password password = null;
        try {
            password = DynamoDBHandler.getPasswordItem(demoPassword.getHash());
        } catch (NullPointerException ex) {}
//        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "user.txt");
//        fw.write(user.toString() + "\n");
//        fw.write(user.getSaltString() + "\n");
//        fw.write(user.toBase64String());
//        fw.close();
//        fw = new FileWriter(homeDir + agoraTempDir + "password.txt");
//        try {
//            fw.write(password.getHash() + "\n");
//            fw.write(password.toBase64String());
//        } catch (Exception ex) {}
//        fw.close();
//        fw = new FileWriter(homeDir + agoraTempDir + "bools.txt");
//        fw.write(userExistsAlready + "\n");
//        fw.write(passwordExistsAlready + "\n");
//        fw.close();


        // make sure none of the printed fields are null which /should/ mean we have fully retrieved the user + password
        assert !user.toString().contains("null") && !password.toString().contains("null");
    }

    /**
     * try is excluded here so that if a LoginException gets thrown, the error is easier to pinpoint
     */
    @Test
    public void testCorrectLogin () {
        assert LoginHandler.login(caseID, passwordString);
    }

    /**
     * Passes if we get a LoginException, fails if not. This is because the password is not correct and that causes .login() to throw a LoginException
     */
    @Test
    public void testWrongLogin () {
        String attemptedUsername = "lrl47"; // this is a valid username
        String attemptedPassword = "xX-skeet-Xx"; // this is not the correct password for the username provided
        try {
            LoginHandler.login(attemptedUsername, attemptedPassword);
        } catch (LoginException ex) {
            assert true;
            // return needed because assert keyword doesn't automatically exit method
            return;
        }
        assert false;
    }
}
