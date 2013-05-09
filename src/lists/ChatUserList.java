package lists;

public class ChatUserList extends UserList {

    private final String name;

    public ChatUserList(String name) {
        super();
        this.name = name;
    }
    
    protected String getList() {
        String list = super.getList();
        return "chatUserList " + this.name + " " + list;
    }

}
