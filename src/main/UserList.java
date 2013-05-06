package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserList {
    // Contains a Map of usernames to the appropriate ConnectionHandler
    // For the server to use in general
    public Map<String, ConnectionHandler> users;
    private final String command;

    public UserList() {
        users = new HashMap<String, ConnectionHandler>();
        command = "serverUserList ";
    }
    
    public UserList(String chatroom) {
        users = new HashMap<String, ConnectionHandler>();
        command = "chatroomUserList " + chatroom + " ";
    }

    public void add(ConnectionHandler connection) throws IOException {
        // TODO: no need for concurrency - single threaded here.
        users.put(connection.username, connection);
        informAll(getList());
        return;
    }

    public void remove(ConnectionHandler connection) {
        // TODO: handle atomicity condition, inform all of change
        users.remove(connection.username);
        informAll(getList());
        return;
    }

    public boolean contains(String userName) {
        // TODO
        return users.containsKey(userName);
    }

    private String getList() {
        // TODO
        StringBuilder userList = new StringBuilder(command);
        for (String usersString : users.keySet()) {
            userList.append(usersString + ' ');
        }
        userList.deleteCharAt(userList.length() - 1);
        return userList.toString();
    }
    
    public void informAll(String message) {
        for (ConnectionHandler user : users.values()) {
            user.updateQueue(message);
        }
    }
}