package lists;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import server.ChatRoom;

public class RoomList {
    private ServerUserList users;
    private final Map<String, ChatRoom> rooms;
    private final String command;

    public RoomList(ServerUserList users) {
        this.users = users;
        this.rooms = new HashMap<String, ChatRoom>();
        this.command = "serverRoomList ";
    }

    public void add(ChatRoom room) throws IOException {
        // TODO: check if room exists, concurrency, add room
        if(this.contains(room.name))
        	throw new IOException();
        rooms.put(room.name, room);
        users.informAll(getRooms());
        return;
    }

    public void remove(ChatRoom room) {
        // TODO: remove room, concurrency
        rooms.remove(room.name);
        users.informAll(getRooms());
        return;
    }

    public boolean contains(String name) {
        return rooms.containsKey(name);
    }

    private String getRooms() {
        // TODO
        StringBuilder roomList = new StringBuilder(command);
        for (String roomsString : rooms.keySet()) {
            roomList.append(roomsString + ' ');
        }
        roomList.deleteCharAt(roomList.length() - 1);
        return roomList.toString();
    }
}