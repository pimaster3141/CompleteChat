package server.rooms;



import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.ConnectionHandler;
import server.lists.ServerUserList;

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
}
