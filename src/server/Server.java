package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import lists.RoomList;
import lists.ServerUserList;

/**
 * Chat server runner.
 */
public class Server {
    private final ServerSocket serverSocket;
    private final RoomList rooms;
    private final ServerUserList users;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        users = new ServerUserList();
        rooms = new RoomList(users);
    }

    public void serve() {
    	System.err.println("SERVER INIT");
        while (true) {
            Socket socket;
            try {
                System.err.println("Server waiting");
                socket = serverSocket.accept();
                try {
                    System.err.println("Creating User");
                    ConnectionHandler connection = new ConnectionHandler(socket, rooms, users);
                    System.err.println("Adding User");
                    users.add(connection);
                    System.err.println("Starting User");
                    new Thread(connection).start();
                } catch (Exception e) {
                    new PrintWriter(socket.getOutputStream(), true).println(e.getMessage());
                    System.err.println("Error: could not run user ~ " + e.getMessage());
                    socket.close();
                }
            } catch (IOException ignore) {
                System.err.println("Something really fucked up just happened, but we're just going to pretend it didnt happen.");
            }
        }
    }

    /**
     * Start a chat server.
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server(10000);
        server.serve();
    }
}