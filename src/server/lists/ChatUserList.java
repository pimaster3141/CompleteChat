package server.lists;

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

}
