package server.lists;

public class ServerUserList extends UserList {

    protected String getList() {
        String list = super.getList();
        return "serverUserList " + list;
    }
}
