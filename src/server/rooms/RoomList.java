package server.rooms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import server.rooms.ChatRoom;
import server.lists.*;
import server.ConnectionHandler;

public class RoomList
{
	private ServerUserList users;
	private final Map<String, ChatRoom> rooms;

	public RoomList(ServerUserList users)
	{
		this.users = users;
		this.rooms = new HashMap<String, ChatRoom>();
	}

	public synchronized void add(ChatRoom room) throws IOException
	{
		// TODO: check if room exists, concurrency, add room
		if (this.contains(room.name))
			throw new IOException("room already exists");
		rooms.put(room.name, room);
		users.informAll(getRooms());
		return;
	}

	public synchronized void remove(ChatRoom room)
	{
		// TODO: remove room, concurrency
		rooms.remove(room.name);
		users.informAll(getRooms());
		return;
	}

	public synchronized boolean contains(String name)
	{
		return rooms.containsKey(name);
	}

	public ChatRoom getRoomFromName(String roomName)
	{
		return rooms.get(roomName);
	}

	private String getRooms()
	{
		// TODO
		StringBuilder roomList = new StringBuilder("RoomList ");
		for (String roomsString : rooms.keySet())
			roomList.append(roomsString + ' ');
		roomList.deleteCharAt(roomList.length() - 1);
		return roomList.toString();
	}

	public synchronized void updateUser(ConnectionHandler user)
	{
		user.updateQueue(getRooms());
	}
}