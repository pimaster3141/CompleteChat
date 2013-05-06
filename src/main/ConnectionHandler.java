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
import java.util.regex.*;

public class ConnectionHandler implements Runnable {
    public final String username;
    private final Socket socket;
    private static RoomList rooms;
    private static UserList users;
    private List<ChatRoom> connectedRooms;
    private BufferedReader in;
    private PrintWriter out;
    private Queue<String> outputBuffer = new ConcurrentLinkedQueue<String>();
    private boolean alive = true;

    public ConnectionHandler(Socket socket, RoomList rooms, UserList users)
            throws IOException {
        this.socket = socket;
        this.rooms = rooms;
        this.users = users;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        connectedRooms = new ArrayList<ChatRoom>();

        String input = in.readLine();
        Pattern p = Pattern.compile("connect \\p{Graph}+");
        Matcher m = p.matcher(input);
        if (!m.matches())
            throw new IOException(
                    "Client input not in the format 'connect [username]'");
        this.username = input.substring(input.indexOf(' ') + 1);

        if (this.users.contains(username)) {
            out.println("username already taken");
            throw new IOException();
        }
        out.println("Connected");
    }

    public void run() {
        while (alive) {
            try {
                if (in.ready())
                    parseInput(in.readLine());
                if (outputBuffer.peek() != null)
                    parseOutput(outputBuffer.poll());
            } catch (IOException e) {
                alive = false;
                break;
            }
        }

        removeAllConnections();

        try {
            socket.close();
        } catch (IOException ignore) {
        }
    }

    public void parseInput(String input) {
        String regex = "(((disconnect)|(make)|(join)|(exit)) "
                + "\\p{Graph}*)|" + "(message \\p{Graph}* \\p{Print}*)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        if (!m.matches())
            return; // Should not occur
        int spaceIndex = input.indexOf(' ');
        String command = input.substring(0, spaceIndex);
        if (command.equals("disconnect")) {

        } else if (command.equals("make")) {

        } else if (command.equals("join")) {

        } else if (command.equals("exit")) {

        } else if (command.equals("message")) {
            int secondSpaceIndex = input.indexOf(' ', spaceIndex + 1);
            String chatroom = input.substring(spaceIndex + 1, secondSpaceIndex);
            String message = input.substring(secondSpaceIndex + 1);
        }
    }

    public void parseOutput(String input) {
        // TODO I think pretty much left to do in other places
        // since it will just already be the grammar that we're
        // sending
        out.println(input);
        return;
    }

    public void removeAllConnections() {
        for (ChatRoom c : connectedRooms)
            c.removeUser(this);
        users.remove(this);
        return;
    }

    public void updateQueue(String info) {
        outputBuffer.add(info);
    }
}
