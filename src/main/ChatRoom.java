package main;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatRoom implements Runnable {
    public final String name;
    private static RoomList rooms;
    private final UserList connectedClients;
    private Queue<String> messageBuffer = new ConcurrentLinkedQueue<String>();

    public ChatRoom(String name, RoomList rooms, ConnectionHandler connection)
            throws IOException {
        this.name = name;
        this.rooms = rooms;
        this.connectedClients = new UserList(name);
        connectedClients.add(connection);
        rooms.add(this);
    }

    public void run() {
        while (connectedClients.size() > 0) {
            if (messageBuffer.peek() != null) {
                connectedClients.informAll(messageBuffer.poll());
            }
        }
        cleanup();
    }

    public void addUser(ConnectionHandler connection) throws IOException {
        connectedClients.add(connection);
    }

    public void removeUser(ConnectionHandler connection) {
        connectedClients.remove(connection);
    }

    public void cleanup() {
        rooms.remove(this);
    }
}
