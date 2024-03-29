package server.lists;

import java.io.IOException;

import server.ConnectionHandler;

/*
 * This class extends the abstract class userList and overrites the default constructor and 
 * getList method
 */
public class ChatUserList extends UserList
{

	private final String name;

	/*
	 * constructor for this field for defining the name of the chat list
	 * 
	 * @param String - name of the list
	 */
	public ChatUserList(String name)
	{
		super();
		this.name = name;
	}

	/*
	 * overrites the default getList method and appends "chatUserList" and the
	 * name to the beginning of the list
	 */
	protected String getList()
	{
		String list = super.getList();
		return "chatUserList " + this.name + " " + list;
	}
	
	/*
	 * overrides the 
	 */
	public void add(ConnectionHandler connection) throws IOException
	{
		try
		{
			super.add(connection);
		}
		catch (IOException e)
		{
			if(e.getMessage().equals("Username Already Exists"))
				throw new IOException("User already in Chatroom");
			else
				throw e;
		}
	}
}
