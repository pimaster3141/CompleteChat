package main;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class RoomList
{
	private static UserList users;
	private final Map<String,ChatRoom> rooms;
	
	public RoomList(UserList users)
	{
		this.users = users;
		this.rooms = new HashMap<String, ChatRoom>();
	}
	
	public void add(ChatRoom room) throws IOException
	{
		//TODO: check if room exists, concurrency, add room
		return;
	}
	
	public void remove(String room)
	{
		//TODO: remove room, concurrency
		return;
	}
	
	public boolean contains(String room)
	{
		return true;
	}
	
}