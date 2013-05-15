package client;
import java.util.*;

public class ChatRoomClient {
    private final String chatRoomName;
    private ArrayList<String> connectedUsers;
    private ArrayList<Message> messageHistory;
    
    public ChatRoomClient(String nameOfChatRoom) {
        chatRoomName = nameOfChatRoom;
        connectedUsers = new ArrayList<String>();
        messageHistory = new ArrayList<Message>();
    }
    
    public void setConnectedUsers(ArrayList<String> newConnectedUsers) {
        connectedUsers = newConnectedUsers;
    }
    
    public void addMessage(Message message) {
        messageHistory.add(message);
    }
    
    public int getConnectedUserSize() {
        return connectedUsers.size();
    }
    
    public String getConnectedUserAtIndex(int i) {
        return connectedUsers.get(i);
    }

    public int getMessageHistorySize() {
        return messageHistory.size();
    }
    
    public Message getMessageAtIndex(int i) {
        return messageHistory.get(i);
    }
}
