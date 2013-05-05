package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionHandler implements Runnable
{ 
    public final String username;
    private final Socket socket;
    private static RoomList rooms;
    private static UserList users;
    private List<ChatRoom> connectedRooms;
    private BufferedReader in;
    private PrintWriter out;
    private Queue<String> outputBuffer = new ConcurrentLinkedQueue<String>();
    private boolean alive = true;

    public ConnectionHandler(Socket socket, RoomList rooms, UserList users) throws IOException
    {
        this.socket = socket;
        this.rooms = rooms;
        this.users = users;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        connectedRooms = new ArrayList<ChatRoom>();
    
        this.username = in.readLine();

        if(this.users.contains(username))
        {
            out.println("username already taken");
            throw new IOException();
        }
        out.println("Connected");
    }

    public void run()
    {
        while(alive)
        {
        	try
        	{
        		if(in.ready())
        			parseInput(in.readLine());
        		if(outputBuffer.peek() != null)
        			parseOutput(outputBuffer.poll());
        	} catch(IOException e)
        	{
        		alive = false;
        		break;
        	}
        }  
        
        removeAllConnections();
        
        try 
        {
			socket.close();
		} catch (IOException ignore){}
    }

    public void parseInput(String input)
    {
    	//TODO
        return;
    }

    public void parseOutput(String input)
    {
    	//TODO
        out.println(input);
        return;
    }
    
    public void removeAllConnections()
    {
    	for(ChatRoom c : connectedRooms)
    		c.removeUser(this.username);
    	users.remove(this);
    	return;
    }
    
    public void updateQueue(String info) {
        outputBuffer.add(info);
    }
}
