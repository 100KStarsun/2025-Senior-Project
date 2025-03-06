package com.agora.app;

import com.agora.app.backend.LoginHandler;
import com.agora.app.backend.LoginException;
import com.agora.app.backend.base.Password;
import com.agora.app.backend.base.PaymentMethods;
import com.agora.app.backend.base.User;
import com.agora.app.dynamodb.DynamoTables;
import com.agora.app.lambda.KeyNotFoundException;
import com.agora.app.lambda.LambdaHandler;
import com.agora.app.lambda.Operations;
import org.json.JSONException;
import org.json.JSONObject;
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
    private static String passwordString = "100kstarsun";


    public static String caseEmailDomain = "@case.edu";
    public static String homeDir = System.getProperty("user.home");
    public static String agoraTempDir = "\\.agora\\";

    @Test
    public void puttingAndGettingUserAndPassword () throws IOException, JSONException {
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
            HashMap<String, String> items = new HashMap<>(2);
            items.put(caseID, null);

            HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(2);
            data.put(DynamoTables.USERS, items);
            JSONObject response = LambdaHandler.invoke(data, Operations.BATCH_GET);
            data = LambdaHandler.jsonToBase64(response);
            User test = User.createFromBase64String(data.get(DynamoTables.USERS).get(caseID));
            userExistsAlready = true;
        } catch (NullPointerException | KeyNotFoundException ex) {}

        if (!userExistsAlready) {
            HashMap<String, String> items = new HashMap<>(2);
            items.put(demoUser.getUsername(), demoUser.toBase64String());

            HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(2);
            data.put(DynamoTables.USERS, items);

            LambdaHandler.invoke(data, Operations.PUT);
        }

        User user = LambdaHandler.getUsers(new String[] {caseID}).get(caseID);
        Password demoPassword = new Password(TestClass.passwordString, user);

        boolean passwordExistsAlready = false;
        try {
            String hash = new Password(passwordString, user).getHash();
            Password test = LambdaHandler.getPasswords(new String[] {hash}).get(hash);
            if (test != null && !test.toString().contains("null")) {
                passwordExistsAlready = true;
            }
        } catch (NullPointerException ex) {}

        if (!passwordExistsAlready) {
            LambdaHandler.putPasswords(new String[] {demoPassword.getHash()}, new String[] {demoPassword.toBase64String()});
        }
        Password password = null;
        try {
            password = LambdaHandler.getPasswords(new String[] {demoPassword.getHash()}).get(demoPassword.getHash());
        } catch (NullPointerException ex) {}
        // make sure none of the printed fields are null which /should/ mean we have fully retrieved the user + password
        assert !user.toString().contains("null") && !password.toString().contains("null");
    }

    /**
     * try is excluded here so that if a LoginException gets thrown, the error is easier to pinpoint
     */
    @Test
    public void testCorrectLogin () throws NoSuchAlgorithmException {
        assert LoginHandler.login(caseID, passwordString);
    }

    /**
     * Passes if we get a LoginException, fails if not. This is because the password is not correct and that causes .login() to throw a LoginException
     */
    @Test
    public void testWrongLogin () throws NoSuchAlgorithmException {
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

    @Test
    public void testLambdaGetSimple () throws IOException, JSONException {
        HashMap<String, String> items = new HashMap<>(2);
        items.put("lrl47", null);

        HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(2);
        data.put(DynamoTables.USERS, items);

        JSONObject obj = LambdaHandler.invoke(data, Operations.BATCH_GET);
        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "lambda_" + Operations.BATCH_GET + "_simple_object.json");
        fw.write(obj.toString(4));
        fw.close();
        assert true;
    }

    @Test
    public void testLambdaGetNonExistant () throws IOException, JSONException {
        HashMap<String, String> items = new HashMap<>(2);
        String key = "definitelyNotAMimic";
        items.put(key, null);

        HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(2);
        DynamoTables table = DynamoTables.USERS;
        data.put(table, items);

        try {
            JSONObject obj = LambdaHandler.invoke(data, Operations.BATCH_GET);
            FileWriter fw = new FileWriter(homeDir + agoraTempDir + "lambda_" + Operations.BATCH_GET + "_simple_object_fail.json");
            fw.write(obj.toString(4));
            fw.close();
        } catch (KeyNotFoundException ex) {
            assert true;
            return;
        }
        assert false;
    }

    @Test
    public void testLambdaPut () throws IOException, JSONException {
        HashMap<String, String> items = new HashMap<>(2);
        items.put("abc123", "some_base_64_string_for_abc123");

        HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(2);
        data.put(DynamoTables.USERS, items);

        JSONObject obj = LambdaHandler.invoke(data, Operations.PUT);
        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "lambda_" + Operations.PUT + "_object.json");
        fw.write(obj.toString(4));
        fw.close();
        assert true;
    }

    @Test
    public void testLambdaDelete () throws IOException, JSONException {
        HashMap<String, String> items = new HashMap<>(2);
        items.put("abc123", null);

        HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(2);
        data.put(DynamoTables.USERS, items);

        JSONObject obj = LambdaHandler.invoke(data, Operations.DELETE);
        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "lambda_" + Operations.DELETE + "_object.json");
        fw.write(obj.toString(4));
        fw.close();
        assert true;
    }

    @Test
    public void testLambdaBatchGet () throws IOException, JSONException {
        HashMap<String, String> items = new HashMap<>(4);
        items.put("nrm98", null);
        items.put("lrl47", null);
        items.put("ssh115", null);

        HashMap<String, String> items2 = new HashMap<>(2);
        items2.put("318cc452b68dca17de02fceb63ba7e1f91e6e2bf1d3b9b7b27b0b628ba703390", null);

        HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(4);
        data.put(DynamoTables.USERS, items);
        data.put(DynamoTables.PASSWORDS, items2);

        JSONObject obj = LambdaHandler.invoke(data, Operations.BATCH_GET);
        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "lambda_" + Operations.BATCH_GET + "_object.json");
        fw.write(obj.toString(4));
        fw.close();
        assert true;
    }

    @Test
    public void testLambdaBatchPut () throws IOException, JSONException {
        HashMap<String, String> items = new HashMap<>(4);
        items.put("def654", "fake_b641");
        items.put("msc135", "fake_b642");

        HashMap<String, String> items2 = new HashMap<>(2);
        items2.put("fake_hash_1", "fake_hash_1 + def654.toBase64()");
        items2.put("fake_hash_2", "fake_hash_2 + msc135.toBase64()");

        HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(4);
        data.put(DynamoTables.USERS, items);
        data.put(DynamoTables.PASSWORDS, items2);

        JSONObject obj = LambdaHandler.invoke(data, Operations.BATCH_PUT);
        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "lambda_" + Operations.BATCH_PUT + "_object.json");
        fw.write(obj.toString(4));
        fw.close();
        assert true;
    }

    @Test
    public void testLambdaBatchDelete () throws IOException, JSONException {
        HashMap<String, String> items = new HashMap<>(4);
        items.put("def654", null);
        items.put("msc135", null);

        HashMap<String, String> items2 = new HashMap<>(2);
        items2.put("fake_hash_1", null);
        items2.put("fake_hash_2", null);

        HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(4);
        data.put(DynamoTables.USERS, items);
        data.put(DynamoTables.PASSWORDS, items2);

        JSONObject obj = LambdaHandler.invoke(data, Operations.BATCH_DELETE);
        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "lambda_" + Operations.BATCH_DELETE + "_object.json");
        fw.write(obj.toString(4));
        fw.close();
        assert true;
    }
}
