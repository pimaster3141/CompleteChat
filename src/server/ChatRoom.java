package server;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import lists.*;

public class ChatRoom implements Runnable {
    public final String name;
    private final RoomList rooms;
    private final ChatUserList connectedClients;
    private LinkedBlockingQueue<String> messageBuffer = new LinkedBlockingQueue<String>();
    private Thread self;

    public ChatRoom(String name, RoomList rooms, ConnectionHandler connection) throws IOException {
        this.name = name;
        this.rooms = rooms;
        this.connectedClients = new ChatUserList(name);
        rooms.add(this);
        connectedClients.add(connection);
        self = new Thread(this);
        System.out.println("  Room: " + name + " - " + "Created");
        self.start();
    }

    public void run() {
        System.out.println("  Room: " + name + " - " + "Input Thread Started");
        while (connectedClients.size() > 0) {
            try {
                connectedClients.informAll(messageBuffer.take());
                System.out.println("  Room: " + name + " - " + "Message Sent");
            } catch (InterruptedException e) {
                System.out.println("  Room: " + name + " - " + "Stopping Input Thread");
                break;
            }
        }

        System.out.println("  Room: " + name + " - " + "Stopping Input Thread");
        cleanup();
        System.out.println("  Room: " + name + " - " + "Cleanup complete");
    }

    public void addUser(ConnectionHandler connection) throws IOException {
        connectedClients.add(connection);
    }

    public void removeUser(ConnectionHandler connection) {
        connectedClients.remove(connection);
        if(connectedClients.size() <= 0)
            self.interrupt();
    }

    public void cleanup() {
        System.out.println("  Room: " + name + " - " + "Removing from server listing");
        rooms.remove(this);
    }

    public synchronized void updateQueue(String info) {
        messageBuffer.add(info);
    }
}
