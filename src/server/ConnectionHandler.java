package server; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.*;

import lists.RoomList;
import lists.ServerUserList;

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

    public ConnectionHandler(Socket socket, RoomList rooms, ServerUserList users) throws IOException {
        this.socket = socket;
        this.rooms = rooms;
        this.users = users;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        out.println("To connect type: \"connect [username]\"");

        String input = in.readLine();
        Pattern p = Pattern.compile("connect \\p{Graph}+");
        Matcher m = p.matcher(input);
        if (!m.matches())
            throw new IOException("Client input not in the format 'connect [username]'");
        this.username = input.substring(input.indexOf(' ') + 1);

        // if (this.users.contains(username)) {
        //     out.println("username already taken");
        //     throw new IOException();
        // }
        // out.println("Connected");

        outputConsumer = new Thread(){
            public void run()
            {
            	System.out.println("Client: " + username + " - " + "Started Output Thread");
                while(alive)
                {
                    try{
                        parseOutput(outputBuffer.take());
                    }catch(InterruptedException e){
                    	System.out.println("Client: " + username + " - " + "Stopping Output Thread");
                        break;
                    }
                }
            }
        };
    }

    public void run() {
        out.println("Connected");
        
        try {
            outputConsumer.start();
            
            System.out.println("Client: " + username + " - " + "Starting Input Thread");
            for (String line =in.readLine(); (line!=null && alive); line=in.readLine())  {
            	out.println();
            	String parsedInput = parseInput(line);
            	System.out.println("Client: " + username + " - " + parsedInput);
            	updateQueue(parsedInput);
            	if(!alive){
            		System.out.println("Client: " + username + " - " + "Stopping Input Thread");
            		break;
            	}
            }
            System.out.println("Client: " + username + " - " + "Input Thread Stopped");
            
        } catch (IOException e) {
        	System.out.println("Client: " + username + " - Connection Lost");
        	
        }finally {
            outputConsumer.interrupt();
            System.out.println("Client: " + username + " - " + "Output Thread Stopped");
            removeAllConnections();
            try {
                socket.close();
            } catch (IOException ignore) {
            }
            System.out.println("Client: " + username + " - " + "Cleanup Complete");
        }
    }

    public String parseInput(String input) {
        String regex = "(((disconnect)|(make)|(join)|(exit)) "
                + "\\p{Graph}+)|" + "(message \\p{Graph}+ \\p{Print}+)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        
        if (!m.matches())
            return "Unrecognized Command " + input; // Should not occur assuming client input is correct
        
        int spaceIndex = input.indexOf(' ');
        String command = input.substring(0, spaceIndex);
        
        if (command.equals("disconnect")) {
            //removeAllConnections();
        	this.alive = false;
            return "disconnect";
            
        } else if (command.equals("make") || command.equals("join")
                || command.equals("exit")) {
            String roomName = input.substring(spaceIndex + 1);
            
            if (command.equals("make")) {
                try {
                    ChatRoom newChatRoom = new ChatRoom(roomName, rooms, this);
                    // Constructor above automatically adds the ChatRoom to the
                    // list of chat rooms of the server
                    connectedRooms.put(newChatRoom.name, newChatRoom);
                    //newChatRoom.addUser(this);
                    return "make room success";
                } catch (IOException roomAlreadyTaken) {
                    return "Room name already taken";
                }
                
            } else if (command.equals("join")) {
                if (rooms.contains(roomName)) {
                    try {
                        ChatRoom roomToJoin = rooms.getRoomFromName(roomName);
                        roomToJoin.addUser(this);
                        this.connectedRooms.put(roomToJoin.name, roomToJoin);
                        return "user added";
                    } catch (IOException e) {
                        return "User already in user list";
                    }
                } else {
                    return "Room name does not exist";
                }
                
            } else if (command.equals("exit")) {
            	ChatRoom roomToExit = connectedRooms.remove(roomName);
            	if(roomToExit != null){
            		roomToExit.removeUser(this);
            		return "user removed from room";
            	}
            	return "user not connected to room";
            	
//                if (rooms.(roomName)) {
//                    ChatRoom roomToExit = rooms.getRoomFromName(roomName);
//                    roomToExit.removeUser(this);
//                    while(connectedRooms.contains(roomToExit)) {
//                        connectedRooms.remove(roomToExit);
//                    }
//                    return "user removed from room";
//                } else {
//                    return "Room name does not exist";
//                }
            }
            
        } else if (command.equals("message")) {
            int secondSpaceIndex = input.indexOf(' ', spaceIndex + 1);
            String chatroom = input.substring(spaceIndex + 1, secondSpaceIndex);
            String message = input.substring(secondSpaceIndex + 1);
            
            ChatRoom roomToMessage = connectedRooms.get(chatroom);
            if(roomToMessage != null){
            	roomToMessage.updateQueue(username + ": " + message);
            	return "messaged " + chatroom;
            }
            return "user not connected to room";
            // TODO Need to add implementation to add messages into the chat room
            // buffer.
        }
        
        return "Unrecongnized Command " + input;
    }

    public synchronized void parseOutput(String input) {
        // TODO I think pretty much left to do in other places
        // since it will just already be the grammar that we're
        // sending
        out.println(input);
        return;
    }

    public void removeAllConnections() {
    	System.out.println("Client: " + username + " - " + "Removing from all connected rooms");
    	
        for (String c : connectedRooms.keySet())
            this.connectedRooms.remove(c).removeUser(this);
        System.out.println("Client: " + username + " - " + "Removing from server listing");
        users.remove(this);
        return;
    }

    public synchronized void updateQueue(String info) {
        outputBuffer.add(info);
    }
}
