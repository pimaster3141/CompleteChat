package server.lists;

/*
 * this class extends the abstract class UserList and redefines the getList method 
 */

public class ServerUserList extends UserList
{

	/*
	 * overrites the get list method to append "serverUserList" to the begining
	 * of the string
	 */
	protected String getList()
	{
		String list = super.getList();
		return "serverUserList " + list;
	}
}
