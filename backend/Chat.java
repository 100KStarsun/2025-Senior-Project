import java.util.ArrayList;
import java.util.UUID;

public class Chat {
    private UUID chatUUID;
    private String username1;
    private String username2;
    private int order;
    private ArrayList<Message> messages;

    public Chat (String username1, String username2, int order) {
        this.chatUUID = UUID.randomUUID();
        this.username1 = username1;
        this.username2 = username2;
        this.order = Chat.getOrder(username1, username2);
        this.messages = new ArrayList<Message>();
    }

    private Chat addMessage (Message msg) {
        if (this.isMessageGoingToFitInCurrentChat(msg)) {
            this.messages.add(msg);
            return this;
        }
        Chat newChat = new Chat(this.username1, this.username2, Chat.getOrder(this.username1, this.username2));
        newChat.messages.add(msg); //safe to force this because we know this message will fit - though this means we need to enforce reasonable message limits
        User.getUserFromUsername(this.username1).addChat(this.username2, newChat.order);
        User.getUserFromUsername(this.username2).addChat(this.username1, newChat.order);
        return newChat;
    }

    private boolean isMessageGoingToFitInCurrentChat (Message msg) {
        // TODO: Add logic so that we check if the chat item in the db is approaching max size (390KB cutoff, 400KB max)
        return true;
    }

    private String packUsernames() {
        return "(" + this.username1 + "," + this.username2 + ")";
    }

    // gets the 2 usernames who are participating in the chat
    // gets the user object associated with username1 and checks to see if they have a chat with username2 already
    // if so, then we figure out how many chat objects this user already has and return that.
    // if none, this is first chat with username2 and we return zero
    public static int getOrder (String username1, String username2) {
        User user1 = User.getUserFromUsername(username1);
        int order = user1.getChats().containsKey(username2) ? user1.getChats().get(username2).size() : 0;
        return order;
    }

}
