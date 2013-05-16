package server.lists;



import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import server.ConnectionHandler;

/*
 * Test class for ChatUserList
 * tests all the methods in this class.... so only one >.>
 */
public class ChatUserListTest
{
	private ChatUserList list;
	private ConnectionHandler one;
	private ConnectionHandler two;
	private ConnectionHandler three;
	

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     * 
     * creates a list and three client stubs to test with.
     */
    @Before
    public void setUp()
    {
    	list = new ChatUserList("myList");
    	one = new ConnectionHandler("one");
    	two = new ConnectionHandler("two");
    	three = new ConnectionHandler("three");
    }
    
    //tests the getList method after adding clients
    @Test
    public void testGetList() throws IOException
    {
    	list.add(one);
    	list.add(two);
    	list.add(three);
    	
    	String output = list.getList();
    	assertEquals(output, "chatUserList myList two one three");
    }
    
    //tests the getList method on an emtpy list
    @Test
    public void testGetListEmpty() throws IOException
    {
    	String output = list.getList();
    	assertEquals(output, "chatUserList myList ");
    }
    
    //tests the getList method on an emtpy list that was once filled
    @Test
    public void testGetListEmptyAfterFull() throws IOException
    {
    	list.add(one);
    	list.add(two);
    	list.add(three);
    	
    	list.remove(three);
    	list.remove(two);
    	list.remove(one);
    	
    	String output = list.getList();
    	assertEquals(output, "chatUserList myList ");
    }
}
