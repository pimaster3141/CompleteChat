package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Chat server runner.
 */
public class Server 
{
	private final ServerSocket serverSocket;
	private final RoomList rooms;
	private final UserList users;

	public Server(int port) throws IOException
	{
		serverSocket = new ServerSocket(port);
		users = new UserList();
		rooms = new RoomList(users);
	}

	public void serve()
	{
		while(true)
		{
			Socket socket;
			try 
			{
				socket = serverSocket.accept();
				try
				{
					ConnectionHandler connection = new ConnectionHandler(socket, rooms, users);
					users.add(connection);
					new Thread(connection).start();
				} catch (Exception e)
				{
					socket.close();
				}
			} catch (IOException ignore){}
		}
	}

    /**
     * Start a chat server.
     */
    public static void main(String[] args) throws IOException
    {
    	Server server = new Server(10000);
    	server.serve();
    }
}