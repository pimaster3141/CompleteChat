package server.rooms;



import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.ConnectionHandler;
import server.lists.ServerUserList;
import static server.Pause.pause;

/**
 * The test class ChatRoomTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class ChatRoomTest
{
	public ChatRoom room;
	public ChatRoom sameName;
	public RoomList rooms;
	public ConnectionHandler one;
	public ConnectionHandler two;
	public ConnectionHandler three;
	public ServerUserList users;

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
    	one = new ConnectionHandler("one");
    	two = new ConnectionHandler("two");
    	three = new ConnectionHandler("three");
    	users = new ServerUserList();
    	try
		{
			users.add(one);
	    	users.add(two);
	    	users.add(three);
	    	rooms = new RoomList(users);
	    	one.getQueue().clear();
	    	two.getQueue().clear();
	    	three.getQueue().clear();
		}
		catch (IOException e)
		{
			fail();
		}
    }
    
    @Test
    public void testConstructor() throws IOException
    {
    	room = new ChatRoom("testRoom", rooms, one);
    	assertEquals(room.name, "testRoom");
    	assertTrue(room.getList().getMap().containsValue(one));
    	assertTrue(rooms.contains("testRoom"));
    	assertTrue(room.isAlive());
    }
    
    @Test
    public void testSameNameconstructor()
    {
    	try
		{
			room = new ChatRoom("testRoom", rooms, one);
	    	sameName = new ChatRoom("testRoom", rooms, one);
	    	fail();
		}
		catch (IOException e)
		{
		}
    	assertTrue(room.getList().getMap().containsValue(one));
    	assertTrue(rooms.contains("testRoom"));
    	assertTrue(rooms.getMap().containsValue(room));
    	assertFalse(rooms.getMap().containsValue(sameName));
    	assertTrue(room.isAlive());
    }
    
    @Test
    public void testAdd1() throws IOException
    {
    	room = new ChatRoom("testRoom", rooms, one);
    	room.addUser(two);
    	assertTrue(room.getList().getMap().containsValue(two));
    }
    
    @Test
    public void testAddMany() throws IOException
    {
    	room = new ChatRoom("testRoom", rooms, one);
    	room.addUser(two);
    	assertTrue(room.getList().getMap().containsValue(two));
    	room.addUser(three);
    	assertTrue(room.getList().getMap().containsValue(three));
    }
    
    @Test (expected = IOException.class)
    public void testAddSame() throws IOException
    {
    	room = new ChatRoom("testRoom", rooms, one);
    	room.addUser(two);
    	assertTrue(room.getList().getMap().containsValue(two));
    	room.addUser(two);
    	fail();
    }
    
    @Test (expected = IOException.class)
	public void testAddToDead() throws IOException
	{
		room = new ChatRoom("testRoom", rooms, one);
		room.removeUser(one);
		pause(500, true);
		room.addUser(two);
		fail();
	}
    
    @Test
    public void testRemoveUser() throws IOException
    {
    	room = new ChatRoom("testRoom", rooms, one);
    	room.addUser(two);
    	assertTrue(room.getList().getMap().containsValue(two));
    	room.addUser(three);
    	assertTrue(room.getList().getMap().containsValue(three));
    	
    	room.removeUser(three);
    	assertTrue(room.getList().getMap().containsValue(two));
    	assertTrue(room.getList().getMap().containsValue(one));
    	assertFalse(room.getList().getMap().containsValue(three));
    }
    
    @Test
    public void testRemoveAllUser() throws IOException
    {
    	room = new ChatRoom("testRoom", rooms, one);
    	room.addUser(two);
    	assertTrue(room.getList().getMap().containsValue(two));
    	room.addUser(three);
    	assertTrue(room.getList().getMap().containsValue(three));
    	
    	room.removeUser(one);
    	room.removeUser(two);
    	room.removeUser(three);
    	pause(500, true);
    	
    	assertFalse(room.getList().getMap().containsValue(two));
    	assertFalse(room.getList().getMap().containsValue(one));
    	assertFalse(room.getList().getMap().containsValue(three));
    	
    	assertFalse(rooms.contains("testRoom"));
    	assertFalse(rooms.getMap().containsValue(room));
    	assertFalse(room.isAlive());
    }
    
    @Test
    public void testMessageAll() throws IOException
    {
    	room = new ChatRoom("testRoom", rooms, one);
    	room.addUser(two);
    	assertTrue(room.getList().getMap().containsValue(two));
    	room.addUser(three);
    	assertTrue(room.getList().getMap().containsValue(three));

    	one.getQueue().clear();
    	two.getQueue().clear();
    	three.getQueue().clear();
    	
    	room.updateQueue("testMessage");
    	pause(500, true);
    	
    	assertEquals(one.getQueue().poll(), "message testRoom testMessage");
    	assertEquals(one.getQueue().poll(), null);
    	assertEquals(two.getQueue().poll(), "message testRoom testMessage");
    	assertEquals(two.getQueue().poll(), null);
    	assertEquals(three.getQueue().poll(), "message testRoom testMessage");
    	assertEquals(three.getQueue().poll(), null);
    }
}
