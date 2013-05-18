package client;
import java.awt.Color;
import java.util.*;

import javax.swing.DefaultListModel;
import javax.swing.text.*;


/**
 * This is a ChatRoomClient that manages a chat room from the
 * client side.  It has the chat room name, the message history,
 * the displayed message document, and the list of users, and 
 * the username of the user using the chatroom client.
 *
 */
public class ChatRoomClient {
    private final String chatRoomName;
    private ArrayList<Message> messageHistory;
    private DefaultStyledDocument displayedMessages;
    private DefaultListModel userModel;
    private final String myUsername;
    
    /**
     * Constructs a ChatRoomClient with the given chatroom name
     * and the username
     * @param nameOfChatRoom
     * @param username
     */
    public ChatRoomClient(String nameOfChatRoom, String username) {
        chatRoomName = nameOfChatRoom;
        messageHistory = new ArrayList<Message>();
        displayedMessages = new DefaultStyledDocument();
        userModel = new DefaultListModel();
        myUsername = username;
    }
    
    /**
     * Adds a message to the message history and appends the message
     * to the styled document to be displayed in the chat tab text
     * area
     * @param message Message object to be added
     * @throws BadLocationException
     */
    public synchronized void addMessage(Message message) throws BadLocationException {
        messageHistory.add(message);
        SimpleAttributeSet userStyle = new SimpleAttributeSet();
        StyleConstants.setBold(userStyle, true);
        if (message.getUsername().equals(myUsername)) {
            StyleConstants.setForeground(userStyle, Color.blue);
        }
        displayedMessages.insertString(displayedMessages.getLength(), message.getUsername() + ": ", userStyle);
        displayedMessages.insertString(displayedMessages.getLength(), message.getMessage() + "\n", null);

    }
    
    /**
     * Updates the users in the user list model from an array list
     * of a new set of users by clearing the array list and repopulating
     * it with the new list.
     * @param newUsers
     */
    public synchronized void updateUsers(ArrayList<String> newUsers) {
        //connectedUsers = newUsers;
        userModel.clear();
        for (int i = 0; i < newUsers.size(); i++) {
        	System.out.println("putting   " + newUsers.get(i));
            userModel.addElement(newUsers.get(i));
        }
        
    }
    
    
    @Override
    public String toString() {
        return chatRoomName;
    }

    /**
     * @return The DefaultStyledDocument of the chatroom messages
     */
    public DefaultStyledDocument getDoc() {
        return displayedMessages;
    }
    
    /**
     * @return The name of the chatroom
     */
    public String getChatRoomName() {
        return chatRoomName;
    }

    /**
     * @return The DefaultListModel containing the list of users
     */
    public DefaultListModel getUserListModel() {
        return userModel;
    }

}
