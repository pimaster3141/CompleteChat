package lists;

public class ChatUserList extends UserList {

    private final String name;

    public ChatUserList(String name) {
        super();
        this.name = name;
    }

    public void informAll(String message) {
        message = "chatRoomUserList " + this.name + " " + message;
        super.informAll(message);
    }

}
