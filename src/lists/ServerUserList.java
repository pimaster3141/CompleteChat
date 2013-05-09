package lists;

public class ServerUserList extends UserList {
	
    public void informAll(String message) {
        message = "serverUserList " + message;
        super.informAll(message);
    }
}
