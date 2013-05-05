package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserList
{
	public Map<String, ConnectionHandler> users;
	
	public UserList()
	{
		users = new HashMap<String, ConnectionHandler>();
	}
	
	public void add(ConnectionHandler connection) throws IOException
	{
		//TODO: no need for concurrency - single threaded here.
		return;
	}
	
	public void remove(String userName)
	{
		//TODO: handle atomicity condition, inform all of change
		return;
	}
	
	public boolean contains(String userName)
	{
		//TODO
		return false;
	}
}