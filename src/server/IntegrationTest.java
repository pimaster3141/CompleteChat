package server;

import static org.junit.Assert.*;
import static server.TestHelpers.pause;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.lists.ServerUserList;
import server.rooms.RoomList;

/*
 * This tests the connection handler fixture by simulating a server and passing in all possible commands 
 */
public class IntegrationTest
{
	/*
	 * @category no_didit - didit wont let me start a server ... stupid .... so this test wont work :(
	 */
	
	private Server server;
	private ServerUserList users;
	private RoomList rooms;

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
		server = new Server(20000);
		new Thread()
		{
			public void run()
			{
				server.serve();
			}
		}.start();
		users = server.getUsers();
		rooms = server.getRooms();
		pause(200, true);
	}
	/**
	 * shuts down the server for the next test
	 */
	@After
	public void tearDown() throws IOException
	{
		server.getServer().close();
		pause(75, true);
	}
	
	@Test
	public void testSingleUserLogin() throws UnknownHostException, IOException
	{
		Socket one = new Socket("localhost", 20000);
		BufferedReader clientIn = new BufferedReader(new InputStreamReader(one.getInputStream()));
		PrintWriter clientOut = new PrintWriter(one.getOutputStream());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertFalse(clientIn.ready());
		clientOut.println("connect one");
		clientOut.flush();
		pause(200,true);
		assertTrue(users.contains("one"));
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList one");
		assertEquals(clientIn.readLine(), "serverRoomList");
		one.close();
	}
	
	@Test
	public void testManyUserLogin() throws UnknownHostException, IOException
	{
		Socket one = new Socket("localhost", 20000);
		Socket two = new Socket("localhost", 20000);
		pause(200, true);
		
		BufferedReader clientIn = new BufferedReader(new InputStreamReader(one.getInputStream()));
		PrintWriter clientOut = new PrintWriter(one.getOutputStream());
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertFalse(clientIn.ready());
		
		BufferedReader clientIn2 = new BufferedReader(new InputStreamReader(two.getInputStream()));
		PrintWriter clientOut2 = new PrintWriter(two.getOutputStream());
		assertTrue(clientIn2.ready());
		assertEquals(clientIn2.readLine(), "To connect type: \"connect [username]\"");
		assertFalse(clientIn2.ready());
		
		clientOut.println("connect one");
		clientOut.flush();
		pause(200,true);
		assertTrue(users.contains("one"));
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList one");
		assertEquals(clientIn.readLine(), "serverRoomList");
		
		clientOut2.println("connect two");
		clientOut2.flush();
		pause(200,true);
		assertTrue(clientIn2.ready());
		assertTrue(users.contains("two"));
		assertEquals(clientIn2.readLine(), "Connected");
		assertEquals(clientIn2.readLine(), "serverUserList one two");
		assertEquals(clientIn2.readLine(), "serverRoomList");
		one.close();
		two.close();
	}
	
	@Test
	public void testMakeRoom() throws UnknownHostException, IOException
	{
		Socket one = new Socket("localhost", 20000);
		Socket two = new Socket("localhost", 20000);
		pause(200, true);
		
		BufferedReader clientIn = new BufferedReader(new InputStreamReader(one.getInputStream()));
		PrintWriter clientOut = new PrintWriter(one.getOutputStream());
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertFalse(clientIn.ready());
		
		BufferedReader clientIn2 = new BufferedReader(new InputStreamReader(two.getInputStream()));
		PrintWriter clientOut2 = new PrintWriter(two.getOutputStream());
		assertTrue(clientIn2.ready());
		assertEquals(clientIn2.readLine(), "To connect type: \"connect [username]\"");
		assertFalse(clientIn2.ready());
		
		clientOut.println("connect one");
		clientOut.flush();
		pause(200,true);
		assertTrue(users.contains("one"));
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList one");
		assertEquals(clientIn.readLine(), "serverRoomList");
		
		clientOut2.println("connect two");
		clientOut2.flush();
		pause(200,true);
		assertTrue(clientIn2.ready());
		assertTrue(users.contains("two"));
		assertEquals(clientIn2.readLine(), "Connected");
		assertEquals(clientIn2.readLine(), "serverUserList one two");
		assertEquals(clientIn2.readLine(), "serverRoomList");
		assertEquals(clientIn.readLine(), "serverUserList one two");
		assertFalse(clientIn.ready());
		assertFalse(clientIn2.ready());
		

		clientOut.println("make room1");
		clientOut.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1");
		assertEquals(clientIn.readLine(), "connectedRoom room1");
		assertEquals(clientIn.readLine(), "chatUserList room1 one");
		assertFalse(clientIn.ready());
		
		assertTrue(clientIn2.ready());
		assertEquals(clientIn2.readLine(), "serverRoomList room1");
		assertFalse(clientIn2.ready());
		
		assertTrue(rooms.contains("room1"));
		one.close();
		two.close();
	}
	
	@Test
	public void testMakeRoomMany() throws UnknownHostException, IOException
	{
		Socket one = new Socket("localhost", 20000);
		Socket two = new Socket("localhost", 20000);
		pause(200, true);
		
		BufferedReader clientIn = new BufferedReader(new InputStreamReader(one.getInputStream()));
		PrintWriter clientOut = new PrintWriter(one.getOutputStream());
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertFalse(clientIn.ready());
		
		BufferedReader clientIn2 = new BufferedReader(new InputStreamReader(two.getInputStream()));
		PrintWriter clientOut2 = new PrintWriter(two.getOutputStream());
		assertTrue(clientIn2.ready());
		assertEquals(clientIn2.readLine(), "To connect type: \"connect [username]\"");
		assertFalse(clientIn2.ready());
		
		clientOut.println("connect one");
		clientOut.flush();
		pause(200,true);
		assertTrue(users.contains("one"));
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList one");
		assertEquals(clientIn.readLine(), "serverRoomList");
		
		clientOut2.println("connect two");
		clientOut2.flush();
		pause(200,true);
		assertTrue(clientIn2.ready());
		assertTrue(users.contains("two"));
		assertEquals(clientIn2.readLine(), "Connected");
		assertEquals(clientIn2.readLine(), "serverUserList one two");
		assertEquals(clientIn2.readLine(), "serverRoomList");
		assertEquals(clientIn.readLine(), "serverUserList one two");
		assertFalse(clientIn.ready());
		assertFalse(clientIn2.ready());
		

		clientOut.println("make room1");
		clientOut.flush();
		pause(75, true);
		clientOut.println("make room2");
		clientOut.flush();
		pause(75, true);
		clientOut2.println("make room3");
		clientOut2.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1");
		assertEquals(clientIn.readLine(), "connectedRoom room1");
		assertEquals(clientIn.readLine(), "chatUserList room1 one");
		assertEquals(clientIn.readLine(), "serverRoomList room1 room2");
		assertEquals(clientIn.readLine(), "connectedRoom room2");
		assertEquals(clientIn.readLine(), "chatUserList room2 one");
		assertEquals(clientIn.readLine(), "serverRoomList room1 room2 room3");
		assertFalse(clientIn.ready());
		
		assertTrue(rooms.contains("room1"));
		assertTrue(rooms.contains("room2"));
		one.close();
		two.close();
	}
	
	@Test
	public void MultiTest() throws UnknownHostException, IOException
	{
		Socket one = new Socket("localhost", 20000);
		Socket two = new Socket("localhost", 20000);
		pause(200, true);
		
		BufferedReader clientIn = new BufferedReader(new InputStreamReader(one.getInputStream()));
		PrintWriter clientOut = new PrintWriter(one.getOutputStream());
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "To connect type: \"connect [username]\"");
		assertFalse(clientIn.ready());
		
		BufferedReader clientIn2 = new BufferedReader(new InputStreamReader(two.getInputStream()));
		PrintWriter clientOut2 = new PrintWriter(two.getOutputStream());
		assertTrue(clientIn2.ready());
		assertEquals(clientIn2.readLine(), "To connect type: \"connect [username]\"");
		assertFalse(clientIn2.ready());
		
		clientOut.println("connect one");
		clientOut.flush();
		pause(200,true);
		assertTrue(users.contains("one"));
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "Connected");
		assertEquals(clientIn.readLine(), "serverUserList one");
		assertEquals(clientIn.readLine(), "serverRoomList");
		
		clientOut2.println("connect two");
		clientOut2.flush();
		pause(200,true);
		assertTrue(clientIn2.ready());
		assertTrue(users.contains("two"));
		assertEquals(clientIn2.readLine(), "Connected");
		assertEquals(clientIn2.readLine(), "serverUserList one two");
		assertEquals(clientIn2.readLine(), "serverRoomList");
		assertEquals(clientIn.readLine(), "serverUserList one two");
		assertFalse(clientIn.ready());
		assertFalse(clientIn2.ready());
		
		//make a room
		clientOut.println("make room1");
		clientOut.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1");
		assertEquals(clientIn.readLine(), "connectedRoom room1");
		assertEquals(clientIn.readLine(), "chatUserList room1 one");
		assertFalse(clientIn.ready());
		
		assertTrue(clientIn2.ready());
		assertEquals(clientIn2.readLine(), "serverRoomList room1");
		assertFalse(clientIn2.ready());
		
		//make another room
		clientOut.println("make room2");
		clientOut.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1 room2");
		assertEquals(clientIn.readLine(), "connectedRoom room2");
		assertEquals(clientIn.readLine(), "chatUserList room2 one");
		assertFalse(clientIn.ready());
		
		assertTrue(clientIn2.ready());
		assertEquals(clientIn2.readLine(), "serverRoomList room1 room2");
		assertFalse(clientIn2.ready());
		
		assertTrue(rooms.contains("room1"));
		assertTrue(rooms.contains("room2"));
		
		//make another room
		clientOut2.println("make room3");
		clientOut2.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "serverRoomList room1 room2 room3");
		assertFalse(clientIn.ready());
		
		assertTrue(clientIn2.ready());
		assertEquals(clientIn2.readLine(), "serverRoomList room1 room2 room3");
		assertEquals(clientIn2.readLine(), "connectedRoom room3");
		assertEquals(clientIn2.readLine(), "chatUserList room3 two");
		assertFalse(clientIn2.ready());
		
		assertTrue(rooms.contains("room1"));
		assertTrue(rooms.contains("room2"));
		assertTrue(rooms.contains("room3"));
		
		//join room
		clientOut2.println("join room2");
		clientOut2.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "chatUserList room2 one two");
		assertFalse(clientIn.ready());
		
		assertTrue(clientIn2.ready());
		assertEquals(clientIn2.readLine(), "connectedRoom room2");
		assertEquals(clientIn2.readLine(), "chatUserList room2 one two");
		assertFalse(clientIn2.ready());
		
		assertTrue(rooms.contains("room1"));
		assertTrue(rooms.contains("room2"));
		assertTrue(rooms.contains("room3"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("one"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("two"));
		
		//message room
		clientOut2.println("message room2 test");
		clientOut2.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "message room2 two test");
		assertFalse(clientIn.ready());
		
		assertTrue(clientIn2.ready());
		assertEquals(clientIn2.readLine(), "message room2 two test");
		assertFalse(clientIn2.ready());
		
		assertTrue(rooms.contains("room1"));
		assertTrue(rooms.contains("room2"));
		assertTrue(rooms.contains("room3"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("one"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("two"));
		
		//message room again
		clientOut.println("message room2 test");
		clientOut.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "message room2 one test");
		assertFalse(clientIn.ready());
		
		assertTrue(clientIn2.ready());
		assertEquals(clientIn2.readLine(), "message room2 one test");
		assertFalse(clientIn2.ready());
		
		assertTrue(rooms.contains("room1"));
		assertTrue(rooms.contains("room2"));
		assertTrue(rooms.contains("room3"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("one"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("two"));
		
		//message room again
		clientOut.println("message room2 test");
		clientOut.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "message room2 one test");
		assertFalse(clientIn.ready());
		
		assertTrue(clientIn2.ready());
		assertEquals(clientIn2.readLine(), "message room2 one test");
		assertFalse(clientIn2.ready());
		
		assertTrue(rooms.contains("room1"));
		assertTrue(rooms.contains("room2"));
		assertTrue(rooms.contains("room3"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("one"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("two"));
		
		//add old room
		clientOut.println("make room2");
		clientOut.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "invalidRoom room2 room already exists");
		assertFalse(clientIn.ready());
		assertFalse(clientIn2.ready());
		
		assertTrue(rooms.contains("room1"));
		assertTrue(rooms.contains("room2"));
		assertTrue(rooms.contains("room3"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("one"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("two"));
		
		//add old room
		clientOut.println("join room2");
		clientOut.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "invalidRoom room2 User already in Chatroom");
		assertFalse(clientIn.ready());
		assertFalse(clientIn2.ready());
		
		assertTrue(rooms.contains("room1"));
		assertTrue(rooms.contains("room2"));
		assertTrue(rooms.contains("room3"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("one"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("two"));
		
		//disconnect rooms
		clientOut.println("exit room1");
		clientOut.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "disconnectedRoom room1");
		assertEquals(clientIn.readLine(), "serverRoomList room2 room3");
		assertFalse(clientIn.ready());
		
		assertTrue(clientIn2.ready());
		assertEquals(clientIn2.readLine(), "serverRoomList room2 room3");
		assertFalse(clientIn2.ready());

		assertFalse(rooms.contains("room1"));
		assertTrue(rooms.contains("room2"));
		assertTrue(rooms.contains("room3"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("one"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("two"));
		
		//disconnect  old rooms
		clientOut.println("exit room1");
		clientOut.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "invalidRoom room1 user not connected to room");
		
		assertTrue(rooms.contains("room2"));
		assertTrue(rooms.contains("room3"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("one"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("two"));
		
		//message  old rooms
		clientOut.println("message room1 bad sheep");
		clientOut.flush();
		pause(75, true);
		assertFalse(clientIn.ready());
		
		assertTrue(rooms.contains("room2"));
		assertTrue(rooms.contains("room3"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("one"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("two"));
		
		//disconnect 
		clientOut.println("disconnect one");
		clientOut.flush();
		pause(75, true);
		assertTrue(clientIn.ready());
		assertEquals(clientIn.readLine(), "disconnectedServerSent");
		assertFalse(clientIn.ready());
		
		assertFalse(users.contains("one"));
		assertTrue(users.contains("two"));
		assertTrue(rooms.contains("room2"));
		assertTrue(rooms.contains("room3"));
		assertFalse(rooms.getRoomFromName("room2").getList().contains("one"));
		assertTrue(rooms.getRoomFromName("room2").getList().contains("two"));
		
		//disconnect badly
		two.close();
		pause(100, true);
		assertFalse(users.contains("one"));
		assertFalse(users.contains("two"));
		assertFalse(rooms.contains("room2"));
		assertFalse(rooms.contains("room3"));

		one.close();
		two.close();
	}
}