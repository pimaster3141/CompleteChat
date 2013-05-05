package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatRoom implements Runnable {
    public final String name;
    private static RoomList rooms;
    private Map<String, ConnectionHandler> connectedClients;
    private Queue<String> messageBuffer = new ConcurrentLinkedQueue<String>();

    public ChatRoom(String name, RoomList rooms, ConnectionHandler connection)
            throws IOException {
        this.name = name;
        this.rooms = rooms;
        this.connectedClients = new HashMap<String, ConnectionHandler>();
        connectedClients.put(connection.username, connection);
        rooms.add(this);
    }

    public void run() {
        while (connectedClients.size() > 0) {
            if (messageBuffer.peek() != null) {
                // TODO: do stuff
            }
        }
        cleanup();
    }

    public void addUser(ConnectionHandler connection) {
        // TODO
    }

    public void removeUser(String User) {
        // TODO
    }

    public void cleanup() {
        rooms.remove(name);
        // TODO
    }
}
