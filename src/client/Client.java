package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client
{
	public final String username;
	public final String IPAddress;
	public final int port;
	private final Socket socket;
	private final PrintWriter out;
	private final BufferedReader in;
	
	public Client(String username, String IPAddress, int port) throws IOException
	{
		this.username = username;
		this.IPAddress = IPAddress;
		this.port = port;
		this.socket = new Socket(IPAddress, port);
		System.err.println("Connected to server");
		
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new PrintWriter(socket.getOutputStream());
		System.err.println("IO Streams enabled");
		
		System.err.println("Waiting for Handshake");
		String prompt = in.readLine();
		
		System.err.println("Verifying Handshake");
		if(!prompt.equals("To connect type: \"connect [username]\""))
			throw new IOException("Server returned invalid handshake");
		System.err.println("Handshake Passed");

		System.err.println("Sending Username");
		out.println("connect " + this.username);
		out.flush();

		System.err.println("Verifying Username");
		prompt = in.readLine();
		if(!prompt.matches("Connected"))
			throw new IOException(prompt);

		System.err.println("Client connected");
	}
	
	public String readBuffer() throws IOException
	{
		try
		{
			return in.readLine();
		}
		catch (IOException e)
		{
			throw new IOException("Disconnected from Server");
		}
	}
	
	public void send(String output)
	{
		out.println(output);
		out.flush();
		return;
	}
	
	public void parseInput(String input)
	{
		//TODO: Sayeed - write stuff that interprets the string and updates the proper jcomponent. this method will be called by the gui
		// in the swing worker's 'done' command. this way this method will be called from the EDT instead of the secondary thread. 
	}
	
	
	//just a method to test this rig... you shouldnt use it in your gui.
	public static void main(String[] args)
	{
		try
		{
			Client c = new Client("user", "localhost", 10000);
			
			while(true)
				System.out.println(c.readBuffer());
		}
		catch (IOException e)
		{
			System.err.println(e.getMessage()+"\n");
			e.printStackTrace();
		}
	}
}
	