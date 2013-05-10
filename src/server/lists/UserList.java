package server.lists;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import server.ConnectionHandler;

public abstract class UserList
{
	// Contains a Map of usernames to the appropriate ConnectionHandler
	// For the server to use in general
	private Map<String, ConnectionHandler> users;

	public UserList()
	{
		users = new HashMap<String, ConnectionHandler>();
	}

	public void add(ConnectionHandler connection) throws IOException
	{
		synchronized (users)
		{
			if (this.contains(connection.username))
				throw new IOException("Username Already Exists");
			users.put(connection.username, connection);
			informAll(getList());
			return;
		}
	}

	public void remove(ConnectionHandler connection)
	{
		synchronized (users)
		{
			users.remove(connection.username);
			informAll(getList());
			return;
		}
	}

	private boolean contains(String userName)
	{
		return users.containsKey(userName);
	}

	protected String getList()
	{
		if (size() <= 0)
			return "";
		StringBuilder output = new StringBuilder("");
		for (String usersString : users.keySet())
			output.append(usersString + " ");
		return output.substring(0, output.length() - 1);
	}

	public int size()
	{
		return users.size();
	}

	public void informAll(String message)
	{
		ConnectionHandler[] usersCopy;
		synchronized (users)
		{
			usersCopy = users.values().toArray(new ConnectionHandler[0]);
		}
		for (ConnectionHandler user : usersCopy)
			user.updateQueue(message);
	}
}