package server.rooms;

/**
 * This class implement a fixture representing a 'chat room' that allows connection handlers(clients) to communicate with each other
 * This room will remain active as long as there exists a user in the room. upon the last user leaving, the room will deregister itself from the listings and 
 * disable itself. 
 * 
 */

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import server.lists.*;
import server.*;

public class ChatRoom implements Runnable {
    public final String name;
    private final RoomList rooms;
    private final ChatUserList connectedClients;
    //input buffer into this chat room for concurrency
    private LinkedBlockingQueue<String> messageBuffer = new LinkedBlockingQueue<String>();
    //a consumer thread for the above buffer
    private Thread self;

    /*
     * constructor for Chat room - will attempt to add self to the room listing, and will throw an excpetion 
     * if it cant(like if the room exists)
     * @param
     * 	String - name of the room
     * 	RoomList - pointer to the master list of all the rooms
     * 	ConnectionHandler - user creating this room in order to have at least one user.
     * 
     * @throws
     * 	IOException - if the room cannot be created (or added to the master list)
     */
    public ChatRoom(String name, RoomList rooms, ConnectionHandler connection) throws IOException {
        this.name = name;
        this.rooms = rooms;
        //create a new list to hold all of the connected users to this room
        this.connectedClients = new ChatUserList(name);
        //add this room to the listing of the rooms
        rooms.add(this);
        //connect the creator to the room
        connectedClients.add(connection);
        //construct a new thread based on itself
        self = new Thread(this);
        System.out.println("  Room: " + name + " - " + "Created");
        //start this chatroom!!!!
        self.start();
    }

    /*
     * Method that controls the main loop for the chatroom
     * will keep looping while this chatroom is 'alive' - at least one person is connected
     * the loop will block until it consumes an element from the buffer and relay the message
     * to all the connected buffers
     */
    public void run() {
        System.out.println("  Room: " + name + " - " + "Input Thread Started");
        //main loop
        while (true)
        	//read an element and inform all of the connected users using the roomList method
            try {
                connectedClients.informAll("message " + messageBuffer.take());
                System.out.println("  Room: " + name + " - " + "Message Sent");
            //if the thread is interrupted (on shutdown)
            } catch (InterruptedException e) {
                System.out.println("  Room: " + name + " - "
                        + "Stopping Input Thread");
                //stop this loop
                break;
            }

        System.out.println("  Room: " + name + " - " + "Stopped Input Thread");
        //remove this room from all llistings.
        cleanup();
        System.out.println("  Room: " + name + " - " + "Cleanup complete");
    }

    /*
     * Adds a user to this chat room
     * @param
     * 	ConnectionHandler - client attempting connect
     * @throws
     * 	IOException - if the client cannot be added
     * 		this could happen if the client already exists or if the room is dead
     */
    public synchronized void addUser(ConnectionHandler connection) throws IOException {
        if (self.isAlive())
        	//try to add the connection
            connectedClients.add(connection);
        else
        	//throw an IOException if the room is dead
            throw new IOException("Room no longer exists");
    }

    /*
     * removes a user from this chatroom
     * @param
     * 	ConnectionHandler - client to remove
     */
    public synchronized void removeUser(ConnectionHandler connection) {
        connectedClients.remove(connection);
        //if there are no more connections to this room - stop the room
        if (connectedClients.size() <= 0)
            self.interrupt();
    }

    /*
     * cleans up this room from all the listings
     */
    private void cleanup() {
        System.out.println("  Room: " + name + " - " + "Removing from server listing");
        //remove this room from the master list
        rooms.remove(this);
    }

    /*
     * pushes a message into this room's message buffer
     */
    public void updateQueue(String info) {
        messageBuffer.add(info);
    }
}
