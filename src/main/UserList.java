package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

<<<<<<< HEAD
public class UserList {
    public Map<String, ConnectionHandler> users;

    public UserList() {
        users = new HashMap<String, ConnectionHandler>();
    }

    public void add(ConnectionHandler connection) throws IOException {
        // TODO: no need for concurrency - single threaded here.
        return;
    }

    public void remove(String userName) {
        // TODO: handle atomicity condition, inform all of change
        return;
    }

    public boolean contains(String userName) {
        // TODO
        return false;
=======
public class UserList
{
    //Contains a Map of usernames to the appropriate ConnectionHandler
    public Map<String, ConnectionHandler> users;
	
	public UserList()
	{
		users = new HashMap<String, ConnectionHandler>();
	}
	
	public void add(ConnectionHandler connection) throws IOException
	{
		//TODO: no need for concurrency - single threaded here.
	    users.put(connection.username, connection);
		return;
	}
	
	public void remove(ConnectionHandler connection)
	{
		//TODO: handle atomicity condition, inform all of change
	    users.remove(connection.username);
		return;
	}
	
	public boolean contains(String userName)
	{
		//TODO
		return users.containsKey(userName);
	}
	
	public String getUsers()
    {
        //TODO
	    StringBuilder userList = new StringBuilder();
	    for (String usersString : users.keySet()) {
	        userList.append(usersString + ' ');
	    }
	    userList.deleteCharAt(userList.length()-1);
        return userList.toString();
>>>>>>> 3ea3b9774b8724aa0a44672047707c6a302d1c25
    }
}