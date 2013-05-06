package server;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lists.*;

public class ChatRoom implements Runnable {
    public final String name;
    private RoomList rooms;
    private final ChatUserList connectedClients;
    private Queue<String> messageBuffer = new ConcurrentLinkedQueue<String>();

    public ChatRoom(String name, RoomList rooms, ConnectionHandler connection) throws IOException {
        this.name = name;
        this.rooms = rooms;
        this.connectedClients = new ChatUserList(name);
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
