package server.lists;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.junit.Test;

import server.ConnectionHandler;

/*
 * Test class for ChatUserList
 * tests all the methods in this class.
 */
public class ChatUserListTest
{
	private ChatUserList list;
	private ConnectionHandler one;
	private ConnectionHandler two;
	private ConnectionHandler three;
	private ConnectionHandler sameName;

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
		sameName = new ConnectionHandler("one");
	}

	// tests the getList method after adding clients
	@Test
	public void testGetList() throws IOException
	{
		list.add(one);
		list.add(two);
		list.add(three);

		String output = list.getList();
		assertEquals(output, "chatUserList myList two one three");
	}

	// tests the getList method on an emtpy list
	@Test
	public void testGetListEmpty() throws IOException
	{
		String output = list.getList();
		assertEquals(output, "chatUserList myList ");
	}

	// tests the getList method on an emtpy list that was once filled
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

	// test to see if we can add a user properly
	// tests both local map as well as messages passed to the client
	@Test
	public void add1() throws IOException
	{
		list.add(one);
		Map<String, ConnectionHandler> map = list.getMap();
		assertEquals(map.size(), 1);
		assertEquals(map.get("one"), one);
		LinkedBlockingQueue<String> buffer = one.getQueue();
		String output = buffer.poll();
		assertEquals(output, "chatUserList myList one");
		output = buffer.poll();
		assertEquals(output, null);
	}

	// similar to above, tests with multiple user additions
	@Test
	public void addMany() throws IOException
	{
		String output;
		LinkedBlockingQueue<String> buffer;
		list.add(one);
		list.add(two);
		list.add(three);
		Map<String, ConnectionHandler> map = list.getMap();
		assertEquals(map.size(), 3);
		assertEquals(map.get("one"), one);
		assertEquals(map.get("two"), two);
		assertEquals(map.get("three"), three);

		buffer = one.getQueue();
		output = buffer.poll();
		assertEquals(output, "chatUserList myList one");
		output = buffer.poll();
		assertEquals(output, "chatUserList myList two one");
		output = buffer.poll();
		assertEquals(output, "chatUserList myList two one three");
		output = buffer.poll();
		assertEquals(output, null);

		buffer = two.getQueue();
		output = buffer.poll();
		assertEquals(output, "chatUserList myList two one");
		output = buffer.poll();
		assertEquals(output, "chatUserList myList two one three");
		output = buffer.poll();
		assertEquals(output, null);

		buffer = three.getQueue();
		output = buffer.poll();
		assertEquals(output, "chatUserList myList two one three");
		output = buffer.poll();
		assertEquals(output, null);
	}

	// tests to see if multiple additions of same user fails
	@Test
	public void addFailure()
	{
		try
		{
			list.add(one);
			list.add(sameName);
			fail();
		}
		catch (IOException e)
		{
			assertEquals(e.getMessage(), "User already in Chatroom");
			assertFalse(list.getMap().containsValue(sameName));
			assertTrue(list.getMap().containsValue(one));
		}
	}
}
