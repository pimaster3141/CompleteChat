package server.rooms;

import static server.TestHelpers.pause;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import server.ConnectionHandler;
import server.lists.ServerUserList;

/**
 * This class implements a list that holds a number of chat rooms this class
 * will define methods for adding and removing rooms from the server
 */
public class RoomList
{
	private ServerUserList users;
	private final Map<String, ChatRoom> rooms;
	private boolean testing = false;

	/*
	 * constructor for this list just initializes the fields
	 * 
	 * @param ServerUserList - master list of all of the people on the server
	 * (used to inform everyone on the server)
	 */
	public RoomList(ServerUserList users)
	{
		this.users = users;
		this.rooms = new HashMap<String, ChatRoom>();
	}

	/*
	 * Constructor for testing enables pauses that will be used for concurrency
	 * testing only
	 * 
	 * @param ServeruserList -master list of all the people on the server
	 * boolean - enable pauses
	 */
	public RoomList(ServerUserList users, boolean testing)
	{
		this.testing = testing;
		this.users = users;
		this.rooms = new HashMap<String, ChatRoom>();
	}

	/*
	 * method to add a chatRoom to this list inform everyone of the change
	 * 
	 * @param ChatRoom - room to add
	 * 
	 * @throws IOException - if the room already exists in this list
	 */
	public synchronized void add(ChatRoom room) throws IOException
	{
		pause(1000, testing);
		// throw an ioException if the room exisits
		if (this.contains(room.name))
			throw new IOException("room already exists");
		// put the room in the list
		rooms.put(room.name, room);
		// infrom everyone of the new rooms
		users.informAll(getRooms());
		return;
	}

	/*
	 * method to remove a ChatRoom from this list informs everyone of the change
	 * 
	 * @param ChatRoom - room to remove
	 */
	public synchronized void remove(ChatRoom room)
	{
		rooms.remove(room.name);
		// inform everyone of change
		users.informAll(getRooms());
		pause(1000, testing);
		return;
	}

	/*
	 * returns if a room name is in the list
	 * 
	 * @param String - name of room
	 * 
	 * @return boolean - if the room exists in this list
	 */
	public synchronized boolean contains(String name)
	{
		return rooms.containsKey(name);
	}

	/*
	 * method for getting a ChatRoom object by name
	 * 
	 * @param String - name of room to get
	 * 
	 * @return ChatRoom - room that matches this name (null if no room)
	 */
	public ChatRoom getRoomFromName(String roomName)
	{
		return rooms.get(roomName);
	}

	/*
	 * returns a string representation of all the rooms in this list
	 * 
	 * @return String - string list of all the names of the rooms in this list
	 */
	private String getRooms()
	{
		StringBuilder roomList = new StringBuilder("serverRoomList ");
		for (String roomsString : rooms.keySet())
			roomList.append(roomsString + ' ');
		roomList.deleteCharAt(roomList.length() - 1);
		return roomList.toString();
	}

	/*
	 * method to update a specific user of all the rooms in this list
	 * 
	 * @param ConnectionHandler - user to be updated
	 */
	public synchronized void updateUser(ConnectionHandler user)
	{
		user.updateQueue(getRooms());
	}

	/*
	 * method to return the map of all the rooms only used for testing
	 * 
	 * @return Map<String, ChatRoom> - map that holds all of the rooms
	 */
	public Map<String, ChatRoom> getMap()
	{
		return this.rooms;
	}
}