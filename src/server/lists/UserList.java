package server.lists;

import static server.Pause.pause;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import server.ConnectionHandler;

/**
 * this class is a generic class that constructs a list of connection handlers
 * (clients) it defines methods to add, remove, and other list stuff as well as
 * the ability to message the entire list.
 */

public abstract class UserList
{
	// Contains a Map of usernames to the appropriate ConnectionHandler
	// For the server to use in general
	private Map<String, ConnectionHandler> users;
	// boolean for if this list is being tested for concurrency
	// this boolean will delay some methods from executing.
	private boolean testing = false; // should only be true for testing.

	/*
	 * constroctor for a list of users - just initializes the mapping of users
	 */
	public UserList()
	{
		users = new HashMap<String, ConnectionHandler>();
	}

	public UserList(boolean testing)
	{
		users = new HashMap<String, ConnectionHandler>();
		this.testing = testing;
	}

	/*
	 * adds a user to this list (connection Handler) if the user already exists,
	 * throw an exception if any change is made to the list, inform everyone on
	 * the list of the change.
	 * 
	 * @param Connection Handler - the client to be added
	 * 
	 * @throws IOException - if the user already exists in this list
	 */
	public void add(ConnectionHandler connection) throws IOException
	{
		synchronized (users)
		{
			pause(1000, testing);
			if (this.contains(connection.username))
				throw new IOException("Username Already Exists");
			users.put(connection.username, connection);
			informAll(getList());
			return;
		}
	}

	/*
	 * removes a connection from this list and informs everone
	 * 
	 * @param Connection Handler - user to remove
	 */
	public void remove(ConnectionHandler connection)
	{
		synchronized (users)
		{
			users.remove(connection.username);
			informAll(getList());
			pause(1000, testing);
			return;
		}
	}

	/*
	 * returns true if the user is in this list
	 * 
	 * @param String - username in question
	 * 
	 * @return boolean - if the user is in this list
	 */
	public boolean contains(String userName)
	{
		return users.containsKey(userName);
	}

	/*
	 * returns a string representation of everyone in this list
	 * 
	 * @return String - list of all users in list
	 */
	protected String getList()
	{
		if (size() <= 0)
			return "";
		StringBuilder output = new StringBuilder("");
		for (String usersString : users.keySet())
			output.append(usersString + " ");
		return output.substring(0, output.length() - 1);
	}

	/*
	 * accessor for how big the list is
	 * 
	 * @return int - size of list
	 */
	public int size()
	{
		return users.size();
	}

	/*
	 * method to inform everyone on this list with a message creates a copy of
	 * the users to prevent concurrency/locking
	 * 
	 * @param String - message to be sent to everyone
	 */
	public void informAll(String message)
	{
		ConnectionHandler[] usersCopy;
		// make a copy of the list to work with, this way it frees up the lock
		// sooner
		synchronized (users)
		{
			usersCopy = users.values().toArray(new ConnectionHandler[0]);
		}
		// send the message to every connection
		for (ConnectionHandler user : usersCopy)
			user.updateQueue(message);
	}

	public Map<String, ConnectionHandler> getMap()
	{
		return this.users;
	}
}