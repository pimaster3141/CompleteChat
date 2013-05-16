package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import server.rooms.*;
import server.lists.ServerUserList;

/**
 * This class represents a connection to the server and handles communicationg with a single client
 *
 */
public class ConnectionHandler implements Runnable {
    public final String username;
    private final Socket socket;
    private final RoomList rooms; // List of rooms in server
    private final ServerUserList users; // List of users in server
    private final HashMap<String, ChatRoom> connectedRooms = new HashMap<String, ChatRoom>();
    private final BufferedReader in;
    private final PrintWriter out;
    private final LinkedBlockingQueue<String> outputBuffer = new LinkedBlockingQueue<String>();
    private final Thread outputConsumer;
    private boolean alive = true;

    /*
     * Constructor for connection
     * @param
     * 	socket - connection to client
     * 	RoomList - reference to master list of all rooms
     * 	ServerUserList - reference to master list of all clients
     * 
     * @throws
     * 	IOException - if the socket is somehow closed during this process
     */
    public ConnectionHandler(Socket socket, RoomList rooms, ServerUserList users) throws IOException {
    	//fill in fields
        this.socket = socket;
        this.rooms = rooms;
        this.users = users;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        //prompt for username
        out.println("To connect type: \"connect [username]\"");

        //parse username
        String input = in.readLine();
        Pattern p = Pattern.compile("connect \\p{Graph}+");
        Matcher m = p.matcher(input);
        //if the usernme is invalid, disconnect the client
        if (!m.matches())
            throw new IOException(
                    "Client input not in the format 'connect [username]'");
        this.username = input.substring(input.indexOf(' ') + 1);

        //thread object that will monitor and consume the output buffer and send it to the client
        outputConsumer = new Thread() {
            public void run() {
                System.out.println("Client: " + username + " - " + "Started Output Thread");
                //keep looping this thread until the connection dies
                while (alive)
                    try {
                    	//send data to client
                        parseOutput(outputBuffer.take());
                    } catch (InterruptedException e) {
                        System.out.println("Client: " + username + " - " + "Stopping Output Thread");
                        break;
                    }
            }
        };
    }

    /*
     * Starts the main thread for this connection 
     */
    public void run() {
    	//echo to client that you're connected
        out.println("Connected");

        try {
        	//start the output consumer thread to relay data back to user
            outputConsumer.start();

            System.out.println("Client: " + username + " - " + "Starting Input Thread");
            
            //main loop for parsing responses from client
            for (String line = in.readLine(); (line != null && alive); line = in.readLine()) {
                //out.println();
            	//parse the input
                String parsedInput = parseInput(line);
                System.out.println("Client: " + username + " - " + parsedInput);
                //echo the parsed output to the client
                updateQueue(parsedInput);
                //if the client is not alive anymore, shutdown
                if (!alive) {
                    System.out.println("Client: " + username + " - " + "Stopping Input Thread");
                    break;
                }
            }
            System.out.println("Client: " + username + " - " + "Input Thread Stopped");

        } catch (IOException e) {
            System.out.println("Client: " + username + " - Connection Lost");

        } finally {
        	//stop the output thread
            outputConsumer.interrupt();
            System.out.println("Client: " + username + " - " + "Output Thread Stopped");
            //remove client from all chat rooms and room listings
            removeAllConnections();
            //close the socket
            try {
                socket.close();
            } catch (IOException ignore) {
            }
            System.out.println("Client: " + username + " - " + "Cleanup Complete");
        }
    }

    /*
     * parses the input string and performs the appropriate action such as joining a room or saying a message
     * @param
     * 	String - the string to be parsed
     */
    private String parseInput(String input) {
    	//sets up regex
        String regex = "(((disconnect)|(make)|(join)|(exit)) "
                + "\\p{Graph}+)|" + "(message \\p{Graph}+ \\p{Print}+)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);

        //if there is no match for the input string
        if (!m.matches())
            return "Unrecognized Command " + input; // Should not occur assuming
                                                    // client input is correct
        
        //find the first space in the strign
        int spaceIndex = input.indexOf(' ');
        String command = input.substring(0, spaceIndex);

        //if the string contains "disconnect", set alive to false and kill connection
        if (command.trim().equals("disconnect")) {
            // removeAllConnections();
            this.alive = false;
            return "disconnect";

            //if the command is to make, join or exit (it is a room command)
        } else if (command.equals("make") || command.equals("join")
                || command.equals("exit")) {
        	
        	//find the next word in string and parse it as the room name
            String roomName = input.substring(spaceIndex + 1);

            //if making a new room
            if (command.equals("make"))
                try {
                	//make a new room
                    ChatRoom newChatRoom = new ChatRoom(roomName, rooms, this);
                    // Constructor above automatically adds the ChatRoom to the
                    // list of chat rooms of the server
                    connectedRooms.put(newChatRoom.name, newChatRoom);
                    informConnectedRooms();
                    return "make room success";
                } catch (IOException e) {
                	//if we cant make a room there will be an error message
                    return e.getMessage();
                }
            
            //if joining a new room
            else if (command.equals("join")) {
                if (rooms.contains(roomName))
                    try {
                        ChatRoom roomToJoin = rooms.getRoomFromName(roomName);
                        roomToJoin.addUser(this);
                        this.connectedRooms.put(roomToJoin.name, roomToJoin);
                        return "user added";
                    } catch (IOException e) {
                        return e.getMessage();
                    }
                else
                    return "Room name does not exist";

            } else if (command.equals("exit")) {
                ChatRoom roomToExit = connectedRooms.remove(roomName);
                if (roomToExit != null) {
                    roomToExit.removeUser(this);
                    return "user removed from room";
                }
                return "user not connected to room";

                // if (rooms.(roomName)) {
                // ChatRoom roomToExit = rooms.getRoomFromName(roomName);
                // roomToExit.removeUser(this);
                // while(connectedRooms.contains(roomToExit)) {
                // connectedRooms.remove(roomToExit);
                // }
                // return "user removed from room";
                // } else {
                // return "Room name does not exist";
                // }
            }

        } else if (command.equals("message")) {
            int secondSpaceIndex = input.indexOf(' ', spaceIndex + 1);
            String chatroom = input.substring(spaceIndex + 1, secondSpaceIndex);
            String message = input.substring(secondSpaceIndex + 1);

            ChatRoom roomToMessage = connectedRooms.get(chatroom);
            if (roomToMessage != null) {
                roomToMessage.updateQueue(username + ": " + message);
                return "messaged " + chatroom;
            }
            return "user not connected to room";
            // TODO Need to add implementation to add messages into the chat
            // room
            // buffer.
        }

        return "Unrecongnized Command " + input;
    }
    
    private void informConnectedRooms()
    {
    	StringBuilder output = new StringBuilder("ConnectedRooms");
        for (String room : connectedRooms.keySet())
            output.append(room + " ");
        updateQueue( output.substring(0, output.length() - 1));
    }

    private void parseOutput(String input) {
        // TODO I think pretty much left to do in other places
        // since it will just already be the grammar that we're
        // sending
        out.println(input);
        out.flush();
        return;
    }

    private void removeAllConnections() {
        System.out.println("Client: " + username + " - "
                + "Removing from all connected rooms");

        for (ChatRoom c : connectedRooms.values())
            c.removeUser(this);
        System.out.println("Client: " + username + " - "
                + "Removing from server listing");
        users.remove(this);
        return;
    }

    public void updateQueue(String info) {
        outputBuffer.add(info);
    }
}
