package lists;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import server.ConnectionHandler;

public abstract class UserList {
    // Contains a Map of usernames to the appropriate ConnectionHandler
    // For the server to use in general
    protected Map<String, ConnectionHandler> users;

    public UserList() {
        users = new HashMap<String, ConnectionHandler>();
    }

    public void add(ConnectionHandler connection) throws IOException {
        // TODO: no need for concurrency - single threaded here.
        if (this.contains(connection.username))
            throw new IOException("Username Already Exists");
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
    	if(size() <= 0)
    		return "";
        StringBuilder output = new StringBuilder("");
        for (String usersString : users.keySet())
            output.append(usersString + " ");
        return output.substring(0, output.length() - 1);
    }

    public int size() {
        return users.size();
    }

    public abstract void informAll(String message);
}