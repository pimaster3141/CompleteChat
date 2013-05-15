package client;

public class Message {
    private final String username;
    private final String message;
    
    public Message(String u, String m) {
        username = u;
        message = m;
    }

    public String getUsername() {
        return username;
    }
    
    public String getMessage() {
        return message;
    }
}
