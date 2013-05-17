package server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static server.Pause.pause;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

/**
 * this tests the server
 * just tests to see how well the server accepts connections
 */
public class ServerTest
{
	/*
	 * @category no_didit - didit wont let me change the ioStreams for System.... so this test wont work :(
	 */
	BufferedReader in;
	Server server;
	Thread serverRunner;
	Socket one;
	Socket two;

	/*
	 * helper method to see if two arrays have the same contents
	 * 	used because the server threads new connections so the output could come out of order
	 * @param
	 * 	ArrayList<String> - lists contaiing data to compare
	 */
	public boolean sameContents(ArrayList<String> one, ArrayList<String> two)
	{
		if (one.size() != two.size())
			return false;
		Collections.sort(one);
		Collections.sort(two);
		for (int i = 0; i < one.size(); i++)
			if (!one.get(i).equals(two.get(i)))
				return false;
		return true;
	}

	/**
	 * Sets up the test fixture.
	 * 
	 * Called before every test case method.
	 */
	@Before
	public void setUp()
	{
		PipedOutputStream pipeOut = new PipedOutputStream();
		PipedInputStream pipeIn;
		try
		{
			pipeIn = new PipedInputStream(pipeOut);
			System.setErr(new PrintStream(pipeOut));
			in = new BufferedReader(new InputStreamReader(pipeIn));
		}
		catch (IOException e)
		{
			fail();
		}

		serverRunner = new Thread()
		{
			public void run()
			{
				server.serve();
			}
		};
	}

	//tests to see if the server can be constructed properly
	@Test
	public void testConstructor() throws IOException, InterruptedException
	{
		server = new Server(10000);
		assertFalse(in.ready());

		serverRunner.start();

		pause(200, true);
		assertEquals(in.readLine(), "SERVER INIT");
		assertEquals(in.readLine(), "Server waiting");
		assertFalse(in.ready());
		server.getServer().close();
		serverRunner.join();
	}

	//test to see if sockets can connect properly
	@Test
	public void testConnecting() throws IOException, InterruptedException
	{
		server = new Server(10000);
		serverRunner.start();
		pause(200, true);
		assertEquals(in.readLine(), "SERVER INIT");
		assertEquals(in.readLine(), "Server waiting");

		one = new Socket("localhost", 10000);

		ArrayList<String> expected = new ArrayList<String>();
		expected.add("Creating User");
		expected.add("Server waiting");
		ArrayList<String> actual = new ArrayList<String>();
		actual.add(in.readLine());
		actual.add(in.readLine());
		assertFalse(in.ready());
		new PrintWriter(one.getOutputStream(), true).println("connect one");
		expected.add("Adding User");
		expected.add("Starting User");
		actual.add(in.readLine());
		actual.add(in.readLine());
		assertFalse(in.ready());
		server.getServer().close();
		serverRunner.join();
	}

	//test to see if multiple sockets can connect at the same time without blocking each other while making a username.
	@Test
	public void testSimotaneousConnect() throws IOException, InterruptedException
	{
		server = new Server(10000);
		serverRunner.start();
		pause(200, true);
		assertEquals(in.readLine(), "SERVER INIT");
		assertEquals(in.readLine(), "Server waiting");

		one = new Socket("localhost", 10000);
		two = new Socket("localhost", 10000);

		ArrayList<String> expected = new ArrayList<String>();
		expected.add("Creating User");
		expected.add("Server waiting");
		expected.add("Creating User");
		expected.add("Server waiting");
		ArrayList<String> actual = new ArrayList<String>();
		actual.add(in.readLine());
		actual.add(in.readLine());
		actual.add(in.readLine());
		actual.add(in.readLine());
		assertFalse(in.ready());
		new PrintWriter(one.getOutputStream(), true).println("connect one");
		new PrintWriter(two.getOutputStream(), true).println("connect two");
		expected.add("Adding User");
		expected.add("Starting User");
		expected.add("Adding User");
		expected.add("Starting User");
		actual.add(in.readLine());
		actual.add(in.readLine());
		actual.add(in.readLine());
		actual.add(in.readLine());
		assertFalse(in.ready());
		server.getServer().close();
		serverRunner.join();
	}
}
