package server.lists;

import static org.junit.Assert.*;
import static server.TestHelpers.pause;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.junit.Test;

import server.ConnectionHandler;

/**
 * This class tests all public methods of UserList with single, and multiple
 * users as well as before and after removing users
 */
public class UserListTest
{
	private UserList list;
	private ConnectionHandler one;
	private ConnectionHandler two;
	private ConnectionHandler three;
	private ConnectionHandler sameName;

	/**
	 * Sets up the test fixture.
	 * 
	 * Called before every test case method.
	 * 
	 * creates a list and three client stubs to test wtih.
	 */
	@Before
	public void setUp()
	{
		list = new UserList()
		{
		};
		one = new ConnectionHandler("one");
		two = new ConnectionHandler("two");
		three = new ConnectionHandler("three");
		sameName = new ConnectionHandler("one");
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
		assertEquals(output, "one");
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
		assertEquals(output, "one");
		output = buffer.poll();
		assertEquals(output, "one two");
		output = buffer.poll();
		assertEquals(output, "one three two");
		output = buffer.poll();
		assertEquals(output, null);

		buffer = two.getQueue();
		output = buffer.poll();
		assertEquals(output, "one two");
		output = buffer.poll();
		assertEquals(output, "one three two");
		output = buffer.poll();
		assertEquals(output, null);

		buffer = three.getQueue();
		output = buffer.poll();
		assertEquals(output, "one three two");
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
			assertEquals(e.getMessage(), "Username Already Exists");
			assertFalse(list.getMap().containsValue(sameName));
			assertTrue(list.getMap().containsValue(one));
		}
	}

	// tests removing a user from the list, also checks if all clients are
	// notified
	@Test
	public void testRemove1() throws IOException
	{
		String output;
		LinkedBlockingQueue<String> buffer;
		list.add(one);
		list.add(two);
		list.add(three);
		Map<String, ConnectionHandler> map = list.getMap();
		for (ConnectionHandler c : map.values())
			c.getQueue().clear();

		list.remove(three);
		assertEquals(map.size(), 2);

		buffer = one.getQueue();
		output = buffer.poll();
		assertEquals(output, "one two");
		output = buffer.poll();
		assertEquals(output, null);

		buffer = two.getQueue();
		output = buffer.poll();
		assertEquals(output, "one two");
		output = buffer.poll();
		assertEquals(output, null);

		buffer = three.getQueue();
		output = buffer.poll();
		assertEquals(output, null);
	}

	// tests if we removee all clients
	@Test
	public void testRemoveAll() throws IOException
	{
		list.add(one);
		list.add(two);
		list.add(three);

		list.remove(three);
		list.remove(two);
		list.remove(one);
		Map<String, ConnectionHandler> map = list.getMap();

		assertEquals(map.size(), 0);
	}

	// tests the getList method after adding clients
	@Test
	public void testGetList() throws IOException
	{
		list.add(one);
		list.add(two);
		list.add(three);

		String output = list.getList();
		assertEquals(output, "one three two");
	}

	// tests the getList method on an emtpy list
	@Test
	public void testGetListEmpty() throws IOException
	{
		String output = list.getList();
		assertEquals(output, "");
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
		assertEquals(output, "");
	}

	// tests the size method
	@Test
	public void testSize() throws IOException
	{
		list.add(one);
		list.add(two);
		list.add(three);

		Map<String, ConnectionHandler> map = list.getMap();
		assertEquals(map.size(), list.size());
	}

	// tests the size method on an emtpy list
	@Test
	public void testSizeEmpty() throws IOException
	{
		Map<String, ConnectionHandler> map = list.getMap();
		assertEquals(map.size(), list.size());
	}

	// tests the size method on an emtpy list that was once filled
	@Test
	public void testSizeEmptyAfterFull() throws IOException
	{
		list.add(one);
		list.add(two);
		list.add(three);

		list.remove(three);
		list.remove(two);
		list.remove(one);

		Map<String, ConnectionHandler> map = list.getMap();
		assertEquals(map.size(), list.size());
	}

	// tests the inform all method to see if messages are sent to all parites
	@Test
	public void testInformAll() throws IOException
	{
		list.add(one);
		list.add(two);
		list.add(three);
		Map<String, ConnectionHandler> map = list.getMap();
		for (ConnectionHandler c : map.values())
			c.getQueue().clear();

		list.informAll("test message");
		for (ConnectionHandler c : map.values())
		{
			assertEquals(c.getQueue().poll(), "test message");
			assertEquals(c.getQueue().poll(), null);
		}
	}

	// tests to see if inform all sens to all users but not one connected.
	@Test
	public void testInformAllNonUser() throws IOException
	{

		list.add(one);
		list.add(two);
		Map<String, ConnectionHandler> map = list.getMap();
		for (ConnectionHandler c : map.values())
			c.getQueue().clear();

		list.informAll("test message");
		for (ConnectionHandler c : map.values())
		{
			assertEquals(c.getQueue().poll(), "test message");
			assertEquals(c.getQueue().poll(), null);
		}

		assertEquals(three.getQueue().poll(), null);
	}

	// tests to see if inform all does not send messages to a newly disconnected
	// client
	@Test
	public void testInformAllNonUserAfterDisconnect() throws IOException
	{

		list.add(one);
		list.add(two);
		list.add(three);
		Map<String, ConnectionHandler> map = list.getMap();
		for (ConnectionHandler c : map.values())
			c.getQueue().clear();
		list.remove(three);

		list.informAll("test message");
		for (ConnectionHandler c : map.values())
		{
			c.getQueue().poll();
			assertEquals(c.getQueue().poll(), "test message");
			assertEquals(c.getQueue().poll(), null);
		}

		assertEquals(three.getQueue().poll(), null);
	}

	// sees if add blocks removal of client properly
	@Test
	public void testAddRemoveConcurrently() throws IOException, InterruptedException
	{
		list = new UserList(true)
		{
		};
		Thread adder = new Thread()
		{
			public void run()
			{
				try
				{
					list.add(one);
				}
				catch (IOException e)
				{
					fail();
				}
			}
		};

		Thread remover = new Thread()
		{
			public void run()
			{
				pause(500, true);
				list.remove(one);
			}
		};

		adder.start();
		remover.start();
		adder.join();
		remover.join();

		assertEquals(list.size(), 0);
	}

	// sees if remove blocks add user concurrently correctly.
	@Test
	public void testTemoveAddConcurrently() throws IOException, InterruptedException
	{
		list = new UserList(true)
		{
		};
		list.add(one);
		Thread remover = new Thread()
		{
			public void run()
			{
				list.remove(one);
			}
		};

		Thread adder = new Thread()
		{
			public void run()
			{
				pause(500, true);
				try
				{
					list.add(one);
				}
				catch (IOException e)
				{
					fail();
				}
			}
		};

		remover.start();
		adder.start();
		remover.join();
		adder.join();

		assertEquals(list.size(), 1);
	}
}
