package lists;

import server.ConnectionHandler;

public class ServerUserList extends UserList {
	
    public void informAll(String message) {
        message = "serverUserList " + message;
        for (ConnectionHandler user : super.users.values())
            user.updateQueue(message);
    }
}
