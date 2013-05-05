package main;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

<<<<<<< HEAD
public class RoomList {
    private static UserList users;
    private final Map<String, ChatRoom> rooms;

    public RoomList(UserList users) {
        this.users = users;
        this.rooms = new HashMap<String, ChatRoom>();
    }

    public void add(ChatRoom room) throws IOException {
        // TODO: check if room exists, concurrency, add room
        return;
    }

    public void remove(String room) {
        // TODO: remove room, concurrency
        return;
    }

    public boolean contains(String room) {
        return true;
    }

=======
public class RoomList
{
	//private static UserList users;
	private final Map<String,ChatRoom> rooms;
	
	public RoomList(UserList users)
	{
		//this.users = users;
		this.rooms = new HashMap<String, ChatRoom>();
	}
	
	public void add(ChatRoom room) throws IOException
	{
		//TODO: check if room exists, concurrency, add room
	    rooms.put(room.name, room);
		return;
	}
	
	public void remove(ChatRoom room)
	{
		//TODO: remove room, concurrency
	    rooms.remove(room.name);
		return;
	}
	
	public boolean contains(String room)
	{
		return rooms.containsKey(room);
	}
	
	public String getRooms()
    {
        //TODO
        StringBuilder roomList = new StringBuilder();
        for (String roomsString : rooms.keySet()) {
            roomList.append(roomsString + ' ');
        }
        roomList.deleteCharAt(roomList.length()-1);
        return roomList.toString();
    }
>>>>>>> 3ea3b9774b8724aa0a44672047707c6a302d1c25
}