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
    private EnumMap<PaymentMethods, Boolean> paymentMethodsSetup; //boolean is whether or not the user has this setup
    private TreeMap<String, ArrayList<UUID>> chats; //key is the username of the other person, the ArrayList is a list of `chatUUID`s that are in the db
    private TreeMap<UUID, String> products; //key is the `productUUID` and the String is the name of the product
    private ArrayList<Product> likedProducts; //this holds references to all of the liked products

}
