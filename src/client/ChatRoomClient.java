package client;
import java.util.*;

public class ChatRoomClient {
    private final String chatRoomName;
    private ArrayList<String> connectedUsers;
    private ArrayList<Message> messageHistory;
    private String displayedMessages;
    
    public ChatRoomClient(String nameOfChatRoom) {
        chatRoomName = nameOfChatRoom;
        connectedUsers = new ArrayList<String>();
        messageHistory = new ArrayList<Message>();
        displayedMessages = "";
    }
    
    public void setConnectedUsers(ArrayList<String> newConnectedUsers) {
        connectedUsers = newConnectedUsers;
    }
    
    public void addMessage(Message message) {
        messageHistory.add(message);
    }
    
    public String getChatRoomName() {
        return chatRoomName;
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
