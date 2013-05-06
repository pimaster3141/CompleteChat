package lists;

import server.ConnectionHandler;

public class ChatUserList extends UserList {
	
	private String name;
	
	public ChatUserList(String name)
	{
		super();
		this.name = name;
	}

	public void informAll(String message) 
	{
		message = "chatRoomUserList " + this.name + message;
		for(ConnectionHandler user : super.users.values())
			user.updateQueue(message);
	}

}
