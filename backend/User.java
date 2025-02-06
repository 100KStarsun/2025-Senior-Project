import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.TreeMap;
import java.util.UUID;

public class User {
    private UUID userUUID;
    private String username;
    private String password;
    private String preferredFirstName;
    private String legalFirstName;
    private String lastName;
    private String email;
    private Instant timeCreated;
    private int numSwaps;
    private short rating; 
    private String university;
    private EnumMap<PaymentMethods, Boolean> paymentMethodsSetup; // boolean is whether or not the user has this setup
    private TreeMap<String, ArrayList<UUID>> chats; // key is the username of the other person, the ArrayList is a list of `chatUUID`s that are in the db
    private ArrayList<UUID> draftedProducts; // a list of UUIDs of products the user has drafted
    private ArrayList<UUID> publishedProducts; // a list of UUIDs of products the user has published
    private ArrayList<UUID> likedProducts; // a list of UUIDs of all products the user has liked
    private ArrayList<UUID> mutedUsers; // a list of UUIDs of all users that this user has muted (i.e. no notifications at all for new messages, but they still get sent)
    private ArrayList<UUID> blockedUsers; // a list of UUIDs of all users that this user has blocked (i.e. chat is closed and other user doesn't know that this user has blocked them)
}
