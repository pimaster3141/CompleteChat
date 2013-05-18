package server;

import static org.junit.Assert.*;
import static server.TestHelpers.pause;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.lists.ServerUserList;
import server.rooms.ChatRoom;
import server.rooms.RoomList;

/*
 * This tests the connection handler fixture by simulating a server and passing in all possible commands 
 */
public class ConnectionHandlerTest
{
	/*
	 * @category no_didit - didit wont let me start a server ... stupid .... so this test wont work :(
	 */
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
	 * 
	 * sets up the socket connections to be used
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	@Before
	public void setUp() throws IOException 
	{
		server = new ServerSocket(5000);
		pause(50, true);
		clientSide = new Socket("localhost", 5000);
		serverSide = server.accept();
		clientIn = new BufferedReader(new InputStreamReader(clientSide.getInputStream()));
		clientOut = new PrintWriter(clientSide.getOutputStream());
		handlerIn = new BufferedReader(new InputStreamReader(serverSide.getInputStream()));
		handlerOut = new PrintWriter(clientSide.getOutputStream());
		users = new ServerUserList();
		rooms = new RoomList(users);
	}
	/**
	 * shuts down the server for the next test
	 */
	@After
	public void tearDown() throws IOException
	{
		server.close();
		pause(50, true);
	}
	
	//tests if i actually got the sockets working on the same thread
	@Test
	public void testSetup()
	{
		assertFalse(serverSide == null);
		assertFalse(clientOut == null);
		assertFalse(clientIn == null);
		assertFalse(handlerOut == null);
		assertFalse(handlerIn == null);
	}
	
	//tests the constructor for the connection handler with a single thread
	@Test
	public void testConstructor() throws IOException, InterruptedException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
	}
	
	//tests the constructor using a seperate thread
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
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		clientOut.println("connect test1");
		clientOut.flush();
		pause(50, true);
		connector.join();
		assertEquals(c.username, "test1");
	}
	
	//tests to see if the connection handler starts up properly after construction
	@Test
	public void testThreadInit() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
	}
	
	//tests if the constructor fails if a bad username is passed
	@Test (expected = IOException.class)
	public void testBadUsernameInput() throws IOException
	{
		clientOut.println("asdjfadf test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		fail();
	}
	
	//tests if the constructor fails if the user disconnects
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
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		clientSide.close();
		connector.join();
		assertEquals(c, null);
	}
	
	//tests if the consumer thread starts properly
	@Test
	public void testConsumerThread() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		
		c.updateQueue("someTestData");

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "someTestData");
		assertFalse(handlerIn.ready());
	}
		
	//tests if the connection works if spammed
	@Test
	public void testConsumerThreadSpam() throws IOException, InterruptedException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
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
		
		pause(500, true);
		for(int i = 0; i<8000; i++)
		{
			assertTrue(clientIn.ready());
			assertEquals(clientIn.readLine(), "hello");
		}
		assertFalse(clientIn.ready());
	}
	
	//tests if the output filter works properly
	@Test
	public void testOutputFilter() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		
		c.updateQueue("");
		
		assertFalse(handlerIn.ready());
	}
	
	//tests if the parser rejects bad input
	@Test
	public void testInputAndParserUnrecognized() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertFalse(clientIn.ready());
		
		clientOut.println("junk data");
		clientOut.flush();
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Unrecognized Command junk data");
		assertFalse(clientIn.ready());
	}
	
	//tests if the parser interperets disconnects properly
	@Test
	public void testInputAndParserDisconnect() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		
		//send test data and check response
		clientOut.println("disconnect test1");
		clientOut.flush();
		pause(50, true);
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
	
	//tests if the parser makes a room properly
	@Test
	public void testInputAndParserMakeRoom() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		
		//send test data and check response
		clientOut.println("make room1");
		clientOut.flush();
		pause(50, true);
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
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "should be sent");
		assertFalse(clientIn.ready());
		assertTrue(c.getConnectedRooms().containsKey("room1"));
		
		assertTrue(rooms.contains("room1"));
	}
	
	//tests if the parser makes a room properly with existing rooms
	@Test
	public void testInputAndParserMakeRoomWithOldRoom() throws IOException
	{
		rooms.add(new ChatRoom("oldRoom"));
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		
		//send test data and check response
		clientOut.println("make room1");
		clientOut.flush();
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList oldRoom room1");
		assertEquals(clientIn.readLine(), "connectedRoom room1");
		assertEquals(clientIn.readLine(), "chatUserList room1 test1");
		assertFalse(clientIn.ready());
		
		//check state
		assertTrue(runner.isAlive());
		assertTrue(users.contains("test1"));
		assertFalse(serverSide.isClosed());
		assertTrue(c.getConsumer().isAlive());
		c.updateQueue("should be sent");
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "should be sent");
		assertFalse(clientIn.ready());
		assertTrue(c.getConnectedRooms().containsKey("room1"));
		
		assertTrue(rooms.contains("oldRoom") && rooms.contains("room1"));
	}
	
	//tests if parser fails to make a room with the same name
	@Test
	public void testInputAndParserMakeRoomWithOldRoomSameName() throws IOException
	{
		rooms.add(new ChatRoom("room1"));
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		
		//send test data and check response
		clientOut.println("make room1");
		clientOut.flush();
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "invalidRoom room1 room already exists");
		assertFalse(clientIn.ready());
		
		//check state
		assertTrue(runner.isAlive());
		assertTrue(users.contains("test1"));
		assertFalse(serverSide.isClosed());
		assertTrue(c.getConsumer().isAlive());
		c.updateQueue("should be sent");
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "should be sent");
		assertFalse(clientIn.ready());
		assertFalse(c.getConnectedRooms().containsKey("room1"));
		
		assertTrue(rooms.contains("room1"));
	}
	
	//tests if the parser works in joining a room
	@Test
	public void testInputAndParserJoinRoom() throws IOException
	{
		new ChatRoom("room1", rooms, new ConnectionHandler("dude"));
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		
		//send test data and check response
		clientOut.println("join room1");
		clientOut.flush();
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "connectedRoom room1");
		assertEquals(clientIn.readLine(), "chatUserList room1 dude test1");
		assertFalse(clientIn.ready());
		
		//check state
		assertTrue(runner.isAlive());
		assertTrue(users.contains("test1"));
		assertFalse(serverSide.isClosed());
		assertTrue(c.getConsumer().isAlive());
		c.updateQueue("should be sent");
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "should be sent");
		assertFalse(clientIn.ready());
		assertTrue(c.getConnectedRooms().containsKey("room1"));
		
		assertTrue(rooms.contains("room1"));
	}
	
	//tests if the parser fails to join a room already in
	@Test
	public void testInputAndParserJoinRoomAlreadyIn() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		new ChatRoom("room1", rooms, c);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1");
		assertEquals(clientIn.readLine(), "connectedRoom room1");
		assertEquals(clientIn.readLine(), "chatUserList room1 test1");
		
		//send test data and check response
		clientOut.println("join room1");
		clientOut.flush();
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "invalidRoom room1 User already in Chatroom");
		assertFalse(clientIn.ready());
		
		//check state
		assertTrue(runner.isAlive());
		assertTrue(users.contains("test1"));
		assertFalse(serverSide.isClosed());
		assertTrue(c.getConsumer().isAlive());
		c.updateQueue("should be sent");
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "should be sent");
		assertFalse(clientIn.ready());
		
		assertTrue(rooms.contains("room1"));
	}
	 
	//tests if the parser fails to join a nonexistant room
	@Test
	public void testInputAndParserJoinNonRoom() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		new ChatRoom("room1", rooms, c);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1");
		assertEquals(clientIn.readLine(), "connectedRoom room1");
		assertEquals(clientIn.readLine(), "chatUserList room1 test1");
		
		//send test data and check response
		clientOut.println("join room2");
		clientOut.flush();
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "invalidRoom room2 Room name does not exist");
		assertFalse(clientIn.ready());
		
		//check state
		assertTrue(runner.isAlive());
		assertTrue(users.contains("test1"));
		assertFalse(serverSide.isClosed());
		assertTrue(c.getConsumer().isAlive());
		c.updateQueue("should be sent");
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "should be sent");
		assertFalse(clientIn.ready());
		
		assertTrue(rooms.contains("room1"));
	}
	
	//tests if the parser exits rooms properly
	@Test
	public void testInputAndParserExitRoom() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		ChatRoom room1 = new ChatRoom("room1", rooms, new ConnectionHandler("dude"));
		room1.addUser(c);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1");
		assertEquals(clientIn.readLine(), "connectedRoom room1");
		assertEquals(clientIn.readLine(), "chatUserList room1 dude test1");
		c.getConnectedRooms().put("room1", room1);
		
		//send test data and check response
		clientOut.println("exit room1");
		clientOut.flush();
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "disconnectedRoom room1");
		assertFalse(clientIn.ready());
		
		//check state
		assertTrue(runner.isAlive());
		assertTrue(users.contains("test1"));
		assertFalse(serverSide.isClosed());
		assertTrue(c.getConsumer().isAlive());
		c.updateQueue("should be sent");
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "should be sent");
		assertFalse(clientIn.ready());
		assertFalse(c.getConnectedRooms().containsKey("room1"));
		
		assertTrue(rooms.contains("room1"));
	}
	
	//test if parer does not exit rooms it is not in.
	@Test
	public void testInputAndParserExitRoomNotConnected() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		new ChatRoom("room1", rooms, new ConnectionHandler("dude"));
		//room1.addUser(c);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1");
		//assertEquals(clientIn.readLine(), "connectedRoom room1");
		//assertEquals(clientIn.readLine(), "chatUserList room1 test1 dude");
		//c.getConnectedRooms().put("room1", room1);
		
		//send test data and check response
		clientOut.println("exit room1");
		clientOut.flush();
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "invalidRoom room1 user not connected to room");
		assertFalse(clientIn.ready());
		
		//check state
		assertTrue(runner.isAlive());
		assertTrue(users.contains("test1"));
		assertFalse(serverSide.isClosed());
		assertTrue(c.getConsumer().isAlive());
		c.updateQueue("should be sent");
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "should be sent");
		assertFalse(clientIn.ready());
		assertFalse(c.getConnectedRooms().containsKey("room1"));
		
		assertTrue(rooms.contains("room1"));
	}
	
	//test if parser sends messages properly to rooms
	@Test
	public void testInputAndParserMessage() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		ConnectionHandler dude = new ConnectionHandler("dude");
		ChatRoom room1 = new ChatRoom("room1", rooms, dude);
		room1.addUser(c);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1");
		assertEquals(clientIn.readLine(), "connectedRoom room1");
		assertEquals(clientIn.readLine(), "chatUserList room1 dude test1");
		c.getConnectedRooms().put("room1", room1);
		dude.getQueue().clear();
		
		//send test data and check response
		clientOut.println("message room1 test message");
		clientOut.flush();
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "message room1 test1 test message");
		assertFalse(clientIn.ready());
		
		//check state
		assertTrue(runner.isAlive());
		assertTrue(users.contains("test1"));
		assertFalse(serverSide.isClosed());
		assertTrue(c.getConsumer().isAlive());
		c.updateQueue("should be sent");
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "should be sent");
		assertFalse(clientIn.ready());
		assertTrue(c.getConnectedRooms().containsKey("room1"));
		
		assertTrue(rooms.contains("room1"));
		assertEquals(dude.getQueue().poll(), "message room1 test1 test message");
		assertEquals(dude.getQueue().poll(), null);
	}
	
	//tests if connection cleans up if cleanly disconnected
	@Test
	public void testDisconnectAndCleanup() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		ConnectionHandler dude = new ConnectionHandler("dude");
		ChatRoom room1 = new ChatRoom("room1", rooms, dude);
		room1.addUser(c);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1");
		assertEquals(clientIn.readLine(), "connectedRoom room1");
		assertEquals(clientIn.readLine(), "chatUserList room1 dude test1");
		c.getConnectedRooms().put("room1", room1);
		dude.getQueue().clear();
		
		//send test data and check response
		clientOut.println("disconnect test1");
		clientOut.flush();
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "disconnectedServerSent");
		assertFalse(clientIn.ready());
		
		//check state
		assertFalse(runner.isAlive());
		assertFalse(users.contains("test1"));
		assertTrue(serverSide.isClosed());
		assertFalse(c.getConsumer().isAlive());
		c.updateQueue("should not be sent");
		pause(50, true);
		assertFalse(clientIn.ready());
		//assertEquals(clientIn.readLine(), "should be sent");
		//assertFalse(clientIn.ready());
		assertTrue(c.getConnectedRooms().containsKey("room1"));
		
		assertFalse(room1.getList().contains("test1"));
		assertTrue(rooms.contains("room1"));
		assertEquals(dude.getQueue().poll(), "chatUserList room1 dude");
		assertEquals(dude.getQueue().poll(), null);
	}
	
	//tests if connection cleans up if uncleanly disconnected
	@Test
	public void testForceDisconnectAndCleanup() throws IOException
	{
		clientOut.println("connect test1");
		clientOut.flush();
		c = new ConnectionHandler(serverSide, rooms, users);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertEquals(c.username, "test1");
		users.add(c);
		
		Thread runner = new Thread(c);
		runner.start();

		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList test1");
		assertFalse(clientIn.ready());
		ConnectionHandler dude = new ConnectionHandler("dude");
		ChatRoom room1 = new ChatRoom("room1", rooms, dude);
		room1.addUser(c);
		pause(50, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1");
		assertEquals(clientIn.readLine(), "connectedRoom room1");
		assertEquals(clientIn.readLine(), "chatUserList room1 dude test1");
		c.getConnectedRooms().put("room1", room1);
		dude.getQueue().clear();
		
		//send test data and check response
		clientSide.close();
		pause(50, true);
		assertFalse(clientIn.ready());
		
		//check state
		assertFalse(runner.isAlive());
		assertFalse(users.contains("test1"));
		assertTrue(serverSide.isClosed());
		assertFalse(c.getConsumer().isAlive());
		c.updateQueue("should not be sent");
		pause(50, true);
		assertFalse(clientIn.ready());
		//assertEquals(clientIn.readLine(), "should be sent");
		//assertFalse(clientIn.ready());
		assertTrue(c.getConnectedRooms().containsKey("room1"));
		
		assertFalse(room1.getList().contains("test1"));
		assertTrue(rooms.contains("room1"));
		assertEquals(dude.getQueue().poll(), "chatUserList room1 dude");
		assertEquals(dude.getQueue().poll(), null);
	}
	
}
