package client;

/**
 * This is a message object that contains private
 * string fields for a username and for a message.
 *
 */
public class Message {
    private final String username;
    private final String message;
    
    /**
     * Constructor method
     * @param u Username String
     * @param m Message String
     */
    public Message(String u, String m) {
        username = u;
        message = m;
    }

    /**
     * Getter method for the username
     * @return username String
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Getter method for the message
     * @return message String
     */
    public String getMessage() {
        return message;
    }
}
