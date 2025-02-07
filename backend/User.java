import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.EnumMap;
import java.util.TreeMap;
import java.util.UUID;

public class User implements Serializable {
    private UUID userUUID;
    private String username;
    private String password;
    private String preferredFirstName;
    private String legalFirstName;
    private String lastName;
    private String email;
    private String university;
    private Instant timeCreated;
    private int numSwaps;
    private short rating;
    private EnumMap<PaymentMethods, Boolean> paymentMethodsSetup; // boolean is whether or not the user has this setup
    private TreeMap<String, ArrayList<UUID>> chats; // key is the username of the other person, the ArrayList is a list of `chatUUID`s that are in the db
    private ArrayList<UUID> draftedProducts; // a list of UUIDs of products the user has drafted
    private ArrayList<UUID> publishedProducts; // a list of UUIDs of products the user has published
    private ArrayList<UUID> likedProducts; // a list of UUIDs of all products the user has liked
    private ArrayList<UUID> mutedUsers; // a list of UUIDs of all users that this user has muted (i.e. no notifications at all for new messages, but they still get sent)
    private ArrayList<UUID> blockedUsers; // a list of UUIDs of all users that this user has blocked (i.e. chat is closed and other user doesn't know that this user has blocked them)

    // this constructor is for when a new user registers
    public User (String username, String password, String preferredFirstName, String legalFirstName, String lastName, String email, String university, EnumMap<PaymentMethods, Boolean> paymentMethodsSetup) {
        this.userUUID = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.preferredFirstName = preferredFirstName;
        this.legalFirstName = legalFirstName;
        this.lastName = lastName;
        this.email = email;
        this.university = university;
        this.timeCreated = Instant.now();
        numSwaps = 0;
        rating = 0;
        this.paymentMethodsSetup = paymentMethodsSetup;
        this.chats = new TreeMap<String, ArrayList<UUID>>();
        this.draftedProducts = new ArrayList<UUID>();
        this.publishedProducts = new ArrayList<UUID>();
        this.likedProducts = new ArrayList<UUID>();
        this.mutedUsers = new ArrayList<UUID>();
        this.blockedUsers = new ArrayList<UUID>();
    }

    // this constructor is for when we're re-creating a user from the db
    public User (UUID userUUID, String username, String preferredFirstName, String legalFirstName, String lastName, String email, String university, Instant timeCreated, int numSwaps, short rating, EnumMap<PaymentMethods, Boolean> paymentMethodsSetup, TreeMap<String, ArrayList<UUID>> chats, ArrayList<UUID> draftedProducts, ArrayList<UUID> publishedProducts, ArrayList<UUID> likedProducts, ArrayList<UUID> mutedUsers, ArrayList<UUID> blockedUsers) {
        this.userUUID = userUUID;
        this.username = username;
        this.preferredFirstName = preferredFirstName;
        this.legalFirstName = legalFirstName;
        this.lastName = lastName;
        this.email = email;
        this.university = university;
        this.timeCreated = timeCreated;
        this.numSwaps = numSwaps;
        this.rating = rating;
        this.paymentMethodsSetup = paymentMethodsSetup;
        this.chats = chats;
        this.draftedProducts = draftedProducts;
        this.publishedProducts = publishedProducts;
        this.likedProducts = likedProducts;
        this.mutedUsers = mutedUsers;
        this.blockedUsers = blockedUsers;
    }

    private String toBase64String () {
        Base64.Encoder encoder = Base64.getEncoder();
        try (
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut)
        ) {
            objectOut.writeObject(this);
            return encoder.encodeToString(bytesOut.toByteArray());
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return "";
    }

    public static User fromBase64String (String encodedUser) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodedUser);
        try (
            ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes);
            ObjectInputStream objectIn = new ObjectInputStream(bytesIn);
        ) {
            return (User) objectIn.readObject();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }
}
