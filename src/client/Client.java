package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.event.ActionEvent;

public class Client {
    private final String username;
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;    

    public Client(String username, String IPAddress, int port) throws IOException {
        this.username = username;
        try
		{
			this.socket = new Socket(IPAddress, port);
		}
		catch (Exception e)
		{
			throw new IOException("Could not resolve host");
		}
        System.err.println("Connected to server");

        this.in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream());
        System.err.println("IO Streams enabled");

        System.err.println("Waiting for Handshake");
        String prompt = in.readLine();

        System.err.println("Verifying Handshake");
        if (!prompt.equals("To connect type: \"connect [username]\""))
            throw new IOException("Server returned invalid handshake");
        System.err.println("Handshake Passed");

        System.err.println("Sending Username");
        out.println("connect " + this.username);
        out.flush();

        System.err.println("Verifying Username");
        prompt = in.readLine();
        if (!prompt.matches("Connected"))
            throw new IOException(prompt);

        System.err.println("Client connected");
    }

    public String readBuffer() throws IOException {
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new IOException("Disconnected from Server");
        }
    }

    public void send(String output) {
        out.println(output);
        out.flush();
        System.err.println(output);
        return;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void start(client.gui.MainWindow main) {
        try {
            System.out.println("About to start loop");
            for(String input = in.readLine(); input!=null; input = in.readLine()) {
                System.out.println("Looping");
                ActionEvent e = new ActionEvent(input, 0, input);
                main.actionPerformed(e);
                
                if(input.equals("disconnectedServerSent"))
                	break;
            }
        } catch(IOException e) {
            String input = "disconnectedServerSent";
            ActionEvent event = new ActionEvent(input, 0, input);
            main.actionPerformed(event);
        } finally {
            // TODO Maybe do stuff to cleanly close the in and out?
        }
        System.err.println("clinet consumer terminated....");
    }

    // just a method to test this rig out; isn't used in the gui
    public static void main(String[] args) {
        try {
            Client c = new Client("user2", "127.0.0.1", 10000);

            while (true)
                System.out.println(c.readBuffer());
        } catch (IOException e) {
            System.err.println(e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
}
