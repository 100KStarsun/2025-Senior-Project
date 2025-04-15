package com.agora.app;

import com.agora.app.backend.LoginHandler;
import com.agora.app.backend.RegistrationHandler;
import com.agora.app.backend.LoginException;
import com.agora.app.backend.base.Chat;
import com.agora.app.backend.base.Image;
import com.agora.app.backend.base.ImageChunk;
import com.agora.app.backend.base.Message;
import com.agora.app.backend.base.Password;
import com.agora.app.backend.base.PaymentMethods;
import com.agora.app.backend.base.User;
import com.agora.app.backend.lambda.DynamoTables;
import com.agora.app.backend.lambda.KeyNotFoundException;
import com.agora.app.backend.lambda.LambdaHandler;
import com.agora.app.backend.lambda.Operations;
import com.agora.app.backend.Session;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;

public class TestClass {
    private static String caseID = "nrm98";
    private static String legalFirstName = "Noah";
    private static String preferredFirstName = ""; // leave blank to automatically use legalFirstName
    private static String lastName = "Mollerstuen";
    private static String[] bools = {"n","n","n","n","n","n","n","y","n"}; // PayPal, Zelle, CashApp, Venmo, Apple Pay, Samsung Pay, Google Pay, Cash, Check | Note: everyone has cash enabled by default
    private static String passwordString = "Penguin3000";


    public static String caseEmailDomain = "@case.edu";
    public static String homeDir = System.getProperty("user.home");
    public static String agoraTempDir = "\\.agora\\";

    public static HashMap<String, String> imageIDs = new HashMap<>();

    @BeforeClass
    public static void setup () {
        imageIDs.put("arcane_test.jpg", "54296898-4db1-4889-ba28-371a412f39fe--07");
        imageIDs.put("big_image.jpg", "9f321ffc-8be4-4845-a3dd-a4e8a55d9555--06");
    }

    //@Test
    public void testBasicLoginStuff () throws IOException, NoSuchAlgorithmException {
        boolean puttingAndGetting = puttingAndGettingUserAndPassword();
        boolean correctLogin = testCorrectLogin();
        boolean wrongLogin = testWrongLogin();
        assert puttingAndGetting && correctLogin && wrongLogin;
    }

    public boolean puttingAndGettingUserAndPassword () throws IOException {
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
        } catch (NullPointerException |KeyNotFoundException ex) {}

        if (!passwordExistsAlready) {
            LambdaHandler.putPasswords(new String[] {demoPassword.getHash()}, new String[] {demoPassword.toBase64String()});
        }
        Password password = null;
        try {
            password = LambdaHandler.getPasswords(new String[] {demoPassword.getHash()}).get(demoPassword.getHash());
        } catch (NullPointerException ex) {}
        // make sure none of the printed fields are null which /should/ mean we have fully retrieved the user + password
        return !user.toString().contains("null") && !password.toString().contains("null");
    }

    /**
     * try is excluded here so that if a LoginException gets thrown, the error is easier to pinpoint
     */
    public boolean testCorrectLogin () throws NoSuchAlgorithmException {
        return LoginHandler.login(caseID, passwordString);
    }

    /**
     * Passes if we get a LoginException, fails if not. This is because the password is not correct and that causes .login() to throw a LoginException
     */
    public boolean testWrongLogin () throws NoSuchAlgorithmException {
        String attemptedUsername = "lrl47"; // this is a valid username
        String attemptedPassword = "xX-skeet-Xx"; // this is not the correct password for the username provided
        try {
            LoginHandler.login(attemptedUsername, attemptedPassword);
        } catch (LoginException ex) {
            return true;
        }
        return false;
    }

    //@Test
    public void testAllBasicLambdaFunctions () throws JSONException, IOException {
        boolean get1 = testLambdaGetSimple();
        boolean get2 = testLambdaGetNonExistant();
        boolean put1 = testLambdaPut();
        boolean delete1 = testLambdaDelete();
        boolean get3 = testLambdaBatchGet();
        boolean put2 = testLambdaBatchPut();
        boolean delete2 = testLambdaBatchDelete();
        assert get1 && get2 && get3 && put1 && put2 && delete1 && delete2;
    }

    public boolean testLambdaGetSimple () throws IOException, JSONException {
        HashMap<String, String> items = new HashMap<>(2);
        items.put("lrl47", null);

        HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(2);
        data.put(DynamoTables.USERS, items);

        JSONObject obj = LambdaHandler.invoke(data, Operations.BATCH_GET);
        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "lambda_" + Operations.BATCH_GET + "_simple_object.json");
        fw.write(obj.toString(4));
        fw.close();
        return true;
    }


    public boolean testLambdaGetNonExistant () throws IOException, JSONException {
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
            return true;
        }
        return false;
    }


    public boolean testLambdaPut () throws IOException, JSONException {
        HashMap<String, String> items = new HashMap<>(2);
        items.put("abc123", "some_base_64_string_for_abc123");

        HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(2);
        data.put(DynamoTables.USERS, items);

        JSONObject obj = LambdaHandler.invoke(data, Operations.PUT);
        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "lambda_" + Operations.PUT + "_object.json");
        fw.write(obj.toString(4));
        fw.close();
        return true;
    }


    public boolean testLambdaDelete () throws IOException, JSONException {
        HashMap<String, String> items = new HashMap<>(2);
        items.put("abc123", null);

        HashMap<DynamoTables, HashMap<String, String>> data = new HashMap<>(2);
        data.put(DynamoTables.USERS, items);

        JSONObject obj = LambdaHandler.invoke(data, Operations.DELETE);
        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "lambda_" + Operations.DELETE + "_object.json");
        fw.write(obj.toString(4));
        fw.close();
        return true;
    }


    public boolean testLambdaBatchGet () throws IOException, JSONException {
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
        return true;
    }


    public boolean testLambdaBatchPut () throws IOException, JSONException {
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
        return true;
    }


    public boolean testLambdaBatchDelete () throws IOException, JSONException {
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
        return true;
    }

    //@Test
    public void testBreakingAndReconstructingImages () throws IOException {
        boolean extraDebugInfo = false;
        ArrayList<String> imageEquals = new ArrayList<>();
        for (String imgName : imageIDs.keySet()) {
            Image img = new Image(new File(homeDir + agoraTempDir + imgName));
            if (extraDebugInfo) {
                FileWriter fw = new FileWriter(homeDir + agoraTempDir + imgName + "_1toString.txt");
                fw.write(img.toString());
                fw.close();
            }
            ImageChunk[] chunkies = img.getChunks();
            Image img2 = Image.fromChunks(chunkies);
            if (extraDebugInfo) {
                FileWriter fw = new FileWriter(homeDir + agoraTempDir + imgName + "_2toString.txt");
                fw.write(img2.toString());
                fw.close();
            }
            Files.write(Path.of(homeDir + agoraTempDir + imgName + "_remade.jpg"), img2.getData(), StandardOpenOption.CREATE);
            imageEquals.add(img.equals(img2) + "");
        }
        assert !imageEquals.contains("false");
    }

    //@Test
    public void testSendingImageData () throws IOException {
        ArrayList<Image> images = new ArrayList<>();
        for (String imgName : imageIDs.keySet()) {
            images.add(new Image(new File(homeDir + agoraTempDir + imgName), imageIDs.get(imgName)));
        }
        LambdaHandler.putImages(images.toArray(new Image[images.size()]));
        assert true;
    }

    //@Test
    public void testGettingImageData () throws IOException {
        HashMap<String, Image> imagesHashMap = LambdaHandler.getImages(imageIDs.values().toArray(new String[imageIDs.size()]));
        ArrayList<String> equalImages = new ArrayList<>();
        for (String imgName : imageIDs.keySet()) {
            Image testImg = imagesHashMap.get(imageIDs.get(imgName));
            Files.write(Path.of(homeDir + agoraTempDir + imgName + "_pulledFromInternet.jpg"), testImg.getData(), StandardOpenOption.CREATE);
            Image baseCase = new Image(new File(homeDir + agoraTempDir + imgName), imageIDs.get(imgName));
            equalImages.add(testImg.equals(baseCase) + "");
        }
        assert !equalImages.contains("false");
    }

    //@Test
    public void testScanImageChunks () throws IOException, JSONException {
        HashMap<String, ImageChunk> imageChunkHashMap = LambdaHandler.scanImageChunks();
        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "imageChunks.json");
        for (ImageChunk ic : imageChunkHashMap.values()) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(ic.getId(), ic.getBase64());
            fw.write(jsonObj.toString(4) + "\n");
        }
        fw.close();
    }

    //@Test
    public void testWebSockets () throws IOException, WebSocketException {
        User noah = LambdaHandler.getUsers(new String[]{"nrm98"}).get("nrm98");
        Session session = new Session(noah);
        WebSocket ws = Session.ws;
        ws.connect();
        ws.sendText("{\"action\":\"sendmessage\",\"to\":\"lrl47\",\"message\":\"hi levi! From noah :)\"}");
        ws.disconnect();
    }

    //@Test
    public void testScanChats () throws IOException, JSONException {
//        User levi = LambdaHandler.getUsers(new String[]{"lrl47"}).get("lrl47");
        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "scanChatResponse.txt");
        fw.write(LambdaHandler.scanChats("lrl47").toString());
        fw.close();
    }
    @Test
    public void testChats () throws IOException {
        Session session = new Session(LambdaHandler.getUsers(new String[]{"lrl47"}).get("lrl47"));
//        Chat.sendMessage("nrm98","message 01/02 " + System.currentTimeMillis());
        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "lrl47-nrm98-chat.txt");
        fw.write(Session.currentUser.getChatObject("nrm98").toString());
        fw.close();
    }

    @Test
    public void testOfflineMessages () throws IOException {
        Session session = new Session(LambdaHandler.getUsers(new String[]{"lrl47"}).get("lrl47"));
        FileWriter fw = new FileWriter(homeDir + agoraTempDir + "lrl47-msc135-chat.txt");
        fw.write(Session.currentUser.getChatObject("msc135").toString());
        fw.close();

    }
}
