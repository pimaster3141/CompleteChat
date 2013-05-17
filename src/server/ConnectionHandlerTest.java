package server;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static server.TestHelpers.pause;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.lists.ServerUserList;
import server.rooms.ChatRoom;
import server.rooms.RoomList;



/**
 * The test class ConnectionHandlerTest.
 * 
 * @author (your name)
 * @version (a version number or a date)
 */
public class ConnectionHandlerTest
{
	private ServerSocket server;
	private Socket serverSide;
	private Socket clientSide;
	private BufferedReader clientIn;
	private PrintWriter clientOut;
	private BufferedReader handlerIn;
	private PrintWriter handlerOut;
	private ServerUserList users;
	private RoomList rooms;
	private ConnectionHandler c;

	/**
	 * Sets up the test fixture.
	 * 
	 * Called before every test case method.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	@Before
	public void setUp() throws IOException 
	{
		server = new ServerSocket(10000);
		pause(100, true);
		clientSide = new Socket("localhost", 10000);
		serverSide = server.accept();
		clientIn = new BufferedReader(new InputStreamReader(clientSide.getInputStream()));
		clientOut = new PrintWriter(clientSide.getOutputStream());
		handlerIn = new BufferedReader(new InputStreamReader(serverSide.getInputStream()));
		handlerOut = new PrintWriter(clientSide.getOutputStream());
		users = new ServerUserList();
		rooms = new RoomList(users);
	}
	
	@After
	public void tearDown() throws IOException
	{
		server.close();
		pause(100, true);
	}
	
	@Test
	public void testSetup()
	{
		assertFalse(serverSide == null);
		assertFalse(clientOut == null);
		assertFalse(clientIn == null);
		assertFalse(handlerOut == null);
		assertFalse(handlerIn == null);
	}
	
	@Test
	public void testConstructor() throws IOException, InterruptedException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
	}
	
	@Test
	public void testConstructerThreaded() throws IOException, InterruptedException
	{
		Thread connector = new Thread()
		{
			public void run()
			{
				try
				{
					c = new ConnectionHandler(serverSide, rooms, users);
				}
				catch (IOException e)
				{
					fail();
				}
			}
		};
		connector.start();
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		clientOut.println("connect test1");
		clientOut.flush();
		pause(100, true);
		connector.join();
		assertEquals(c.username, "test1");
	}
	
	@Test
	public void testThreadInit() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		
		Thread runner = new Thread(c);
		runner.start();

		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
	}
	
	@Test (expected = IOException.class)
	public void testBadUsernameInput() throws IOException
	{
		clientOut.println("asdjfadf test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		fail();
	}
	
	@Test
	public void testSpontaneiousDisconnectAtLogin() throws IOException, InterruptedException
	{
		Thread connector = new Thread()
		{
			public void run()
			{
				try
				{
					c = new ConnectionHandler(serverSide, rooms, users);
				}
				catch (IOException e)
				{
					c = null;
				}
			}
		};
		connector.start();
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		clientSide.close();
		connector.join();
		assertEquals(c, null);
	}
	@Test
	public void testConsumerThread() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		
		Thread runner = new Thread(c);
		runner.start();

		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		
		c.updateQueue("someTestData");

		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "someTestData");
		assertFalse(handlerIn.ready());
	}
		
	@Test
	public void testConsumerThreadSpam() throws IOException, InterruptedException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		
		Thread runner = new Thread(c);
		runner.start();

		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		
		Thread[] threadList = new Thread[800];
		for(int i = 0; i < 800; i++)
		{
			threadList[i] = new Thread()
			{
				public void run()
				{
					for(int x = 0; x < 10; x++)
						c.updateQueue("hello");
				}
			};
		}
		
		for(Thread t : threadList)
			t.start();
		for(Thread t : threadList)
			t.join();
		
		pause(1000, true);
		for(int i = 0; i<8000; i++)
		{
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "hello");
		}
		assertFalse(clientIn.ready());
	}
	
	@Test
	public void testOutputFilter() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		
		Thread runner = new Thread(c);
		runner.start();

		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		
		c.updateQueue("");
		
		assertFalse(handlerIn.ready());
	}
	
	@Test
	public void testInputAndParserUnrecognized() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		
		Thread runner = new Thread(c);
		runner.start();

		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertFalse(clientIn.ready());
		
		clientOut.println("junk data");
		clientOut.flush();
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Unrecognized Command junk data");
		assertFalse(clientIn.ready());
	}
	
	@Test
	public void testInputAndParserDisconnect() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		
		//send test data and check response
		clientOut.println("disconnect test1");
		clientOut.flush();
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "disconnectedServerSent");
		assertFalse(clientIn.ready());
		
		//check state
		assertFalse(runner.isAlive());
		assertFalse(users.contains("test1"));
		assertTrue(serverSide.isClosed());
		assertFalse(c.getConsumer().isAlive());
		c.updateQueue("should not be sent");
		assertFalse(clientIn.ready());
	}
	
	@Test
	public void testInputAndParserMakeRoom() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		
		//send test data and check response
		clientOut.println("make room1");
		clientOut.flush();
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1");
		assertEquals(clientIn.readLine(), "connectedRoom room1");
		assertEquals(clientIn.readLine(), "chatUserList room1 test1");
		assertFalse(clientIn.ready());
		
		//check state
		assertTrue(runner.isAlive());
		assertTrue(users.contains("test1"));
		assertFalse(serverSide.isClosed());
		assertTrue(c.getConsumer().isAlive());
		c.updateQueue("should be sent");
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "should be sent");
		assertFalse(clientIn.ready());
		
		assertTrue(rooms.contains("room1"));
	}
	
	@Test
	public void testInputAndParserMakeRoomWithOldRoom() throws IOException
	{
		rooms.add(new ChatRoom("oldRoom"));
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		
		//send test data and check response
		clientOut.println("make room1");
		clientOut.flush();
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1 oldRoom");
		assertEquals(clientIn.readLine(), "connectedRoom room1");
		assertEquals(clientIn.readLine(), "chatUserList room1 test1");
		assertFalse(clientIn.ready());
		
		//check state
		assertTrue(runner.isAlive());
		assertTrue(users.contains("test1"));
		assertFalse(serverSide.isClosed());
		assertTrue(c.getConsumer().isAlive());
		c.updateQueue("should be sent");
		pause(100, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "should be sent");
		assertFalse(clientIn.ready());
		
		assertTrue(rooms.contains("oldRoom") && rooms.contains("room1"));
	}
	
}
