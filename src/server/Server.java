package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import server.lists.ServerUserList;
import server.rooms.RoomList;

/**
 * This class starts a server to listen and accept connections from clients over
 * a socket connection.
 */
public class Server
{
	private final ServerSocket serverSocket;
	private final RoomList rooms;
	private final ServerUserList users;

	/*
	 * Constructor for server binds a server to a port on the local address and
	 * initialilzes lists
	 * 
	 * @param int - port -- port to bind to (0 <= int <= 65535)
	 * 
	 * @throws IOException - if socket cannot be bound to port
	 */
	public Server(int port) throws IOException
	{
		serverSocket = new ServerSocket(port);
		users = new ServerUserList();
		rooms = new RoomList(users);
	}

	/*
	 * Main loop for server listens for a connection then creates a new thread
	 * to construct a handler for that connection as well as updates all the
	 * lists
	 */
	public void serve()
	{
		System.err.println("SERVER INIT");
		// start main loop
		while (true)
			try
			{
				System.err.println("Server waiting");
				// accepts a new socket connection
				final Socket socket = serverSocket.accept();
				// create a new thread to create a connection (so the server is
				// free to accept another connection)
				new Thread()
				{
					public void run()
					{
						try
						{
							System.err.println("Creating User");
							// create a new connection handler
							ConnectionHandler connection = new ConnectionHandler(socket, rooms, users);
							System.err.println("Adding User");
							// attempt to add the connection (client) to the
							// list of connected clients
							users.add(connection);
							// update the client with all the available rooms
							rooms.updateUser(connection);
							System.err.println("Starting User");
							// start the connection thread to start IO
							new Thread(connection).start();
							// If there is an error (like the user already
							// exists, or user disconnects at startup sequence)
						}
						catch (Exception e)
						{
							try
							{
								// try to tell the client what happened
								new PrintWriter(socket.getOutputStream(), true).println(e.getMessage());
								System.err.println("Error: could not run user ~ " + e.getMessage());
								// close the connection
								socket.close();
							}
							catch (IOException wtf)
							{
								System.err.println("I dont even know right now...");
							}
						}
					}
				}.start(); // start the thread to create connections

			}
			catch (IOException kill)
			{
				// really... this shouldnt ever be ran... really... unless
				// something FUBAR happened.
				System.err.println("Something really fucked up just happened, but we're just going to pretend it didnt happen... but now im dead.");
				break;
			}
	}

	/*
	 * returns the serverSocket instance - for teting only
	 * 
	 * @return SeverSocket - the socket that everytihng is connected to
	 */
	public ServerSocket getServer()
	{
		return serverSocket;
	}
	
	/*
	 * determines if a string is an integer
	 */
	private static boolean isInteger(String s) 
	{
	    try 
	    { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) 
	    { 
	        return false; 
	    }
	    return true;
	}

	/**
	 * Start a chat server.
	 */
	public static void main(String[] args) throws IOException
	{
		System.out.println();
		int port = -1;
		if(args.length == 0)
			port = 10000;
		else if (args.length == 2 &&  args[0].equals("-p") && isInteger(args[1]))
			port = Integer.parseInt(args[1]);
		else
			System.out.println("USAGE: [-p PORT]");
		
		if(port >= 0 && port <= 65535)
		{
			System.out.println("Starting Server on port " + port);
			Server server = new Server(10000);
			server.serve();
		}
		else
			System.out.println("   PORT should be betweeen [0, 65535]");
		return;
	}
}