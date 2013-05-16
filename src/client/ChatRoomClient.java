package client;
import java.awt.Color;
import java.util.*;

import javax.swing.text.*;


/**
 * Must be initialized and added to the hashmap in client before 
 *
 */
public class ChatRoomClient {
    private final String chatRoomName;
    private ArrayList<String> connectedUsers;
    private ArrayList<Message> messageHistory;
    private DefaultStyledDocument displayedMessages;
    private final String myUsername;
    
    public ChatRoomClient(String nameOfChatRoom, String username) {
        chatRoomName = nameOfChatRoom;
        connectedUsers = new ArrayList<String>();
        messageHistory = new ArrayList<Message>();
        displayedMessages = new DefaultStyledDocument();
        myUsername = username;
    }
    
    public void setConnectedUsers(ArrayList<String> newConnectedUsers) {
        connectedUsers = newConnectedUsers;
    }
    
    public void addMessage(Message message) throws BadLocationException {
        messageHistory.add(message);
        SimpleAttributeSet userStyle = new SimpleAttributeSet();
        StyleConstants.setBold(userStyle, true);
        if (message.getUsername()==myUsername) {
            StyleConstants.setForeground(userStyle, Color.blue);
        }
        displayedMessages.insertString(displayedMessages.getLength(), message.getUsername() + ": ", userStyle);
        displayedMessages.insertString(displayedMessages.getLength(), message.getMessage() + "\n", null);

    }
    
    public DefaultStyledDocument getDoc() {
        return displayedMessages;
    }
    
    public String getChatRoomName() {
        return chatRoomName;
    }
    
    public ArrayList<String> getConnectedUsers() {
        return connectedUsers;
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
