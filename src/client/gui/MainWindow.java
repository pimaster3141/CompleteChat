package client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.text.BadLocationException;

import client.*;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JTabbedPane tabs;
    private final JMenuBar menuBar;
    private final JMenu file;
    private final JMenuItem getHistory;
    private final JMenuItem logout;
    private final MainTab mainTab;
    private Client client = null;
    
    private DefaultListModel allUsers;
    private final HashMap<String, ChatRoomClient> connectedRoomsHistory;
    private final HashMap<String, ChatRoomClient> connectedRoomsCurrent;
    private DefaultListModel allRooms;
    
    public MainWindow() {
        menuBar = new JMenuBar();
        file = new JMenu("File");
        getHistory = new JMenuItem("Chat History");
        logout = new JMenuItem("Logout");
        allUsers = new DefaultListModel();
        allRooms = new DefaultListModel();
        connectedRoomsHistory = new HashMap<String,ChatRoomClient>();
        connectedRoomsCurrent = new HashMap<String, ChatRoomClient>();
        
        menuBar.add(file);
        file.add(getHistory);
        file.add(logout);
        setJMenuBar(menuBar);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        tabs = new JTabbedPane();
        mainTab = new MainTab(this);
        tabs.addTab("Main Window", mainTab);
        this.add(tabs);
        
        getHistory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                HistoryTab t = new HistoryTab(connectedRoomsHistory);
                addCloseableTab("History", t);
            }    
        });
        
        logout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                client.send("disconnect " + client.getUsername());
                quit();
            }
        });
    }
    
    public void addCloseableTab(String tabName, JPanel tab) {
        tabs.addTab(tabName, tab);
        int i = tabs.indexOfComponent(tab);
        if (i != -1) {
            ChatRoomClient chatroom = connectedRoomsCurrent.get(tabName);
            if (chatroom == null) {
                if (tabName == "History") {
                    tabs.setTabComponentAt(i, new ChatTabComponent(null));
                }
                else {
                    System.err.println("Should never arrive here unless we have concurrency problems");
                }
            }
            tabs.setTabComponentAt(i, new ChatTabComponent(chatroom));
        }
    }
    
    /**
     * So I want to make a tab that I can close with a button, so I need
     * to make a new Component to represent that tab.
     *
     */
    private class ChatTabComponent extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final JLabel name;
        private final ChatRoomClient chatroom;
        private final String tabName;
        
        private ChatTabComponent(ChatRoomClient chatroom) {
            this.chatroom = chatroom;
            if (chatroom == null) {
                tabName = "History";
            }
            else {
                this.tabName = chatroom.getChatRoomName();
            }
            if (tabs == null) {
                throw new NullPointerException("Tabbed Pane is null");   
            }
            setOpaque(false);
            
            name = new JLabel() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public String getText() {
                    return ChatTabComponent.this.tabName;
                }
            };
            name.setPreferredSize(new Dimension(60, 15));
            
            add(name);
            
            JButton exit = new JButton("x");
            exit.setContentAreaFilled(false);
            exit.setPreferredSize(new Dimension(17, 17));
            exit.setFocusable(false);
            exit.setForeground(Color.RED);
            exit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    closeTab(ChatTabComponent.this, ChatTabComponent.this.chatroom);
                }
            });
            add(exit);
        }
    }
    
    public void setClient(Client c) {
        client = c;
        mainTab.setClient(c);
    }
    
    public void addRooms(Object[] ChatRooms) {
        mainTab.addRooms(ChatRooms);
    }
    
    /**
     * Returns the particular tab given the string for the
     * chat name
     * @param chatName
     * @return The JPanel Tab with the given chatroom name
     */
    private JPanel findTab(String chatName) {
        for (int i = 0; i<tabs.getTabCount(); i++) {
            String tabName = tabs.getComponentAt(i).getName();
            if (tabName == chatName) {
                return (JPanel) tabs.getComponentAt(i);
            }
        }
        return null;
    }
    

    
    public void updateConversation(String chatRoomName, String userName, String message) {
        final String c = chatRoomName;
        final String u = userName;
        final String m = message;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(connectedRoomsCurrent.containsKey(c)){
                    ChatRoomClient roomCurrent = connectedRoomsCurrent.get(c);
                    try {
                        roomCurrent.addMessage(new Message(u, m));
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
    
    public void displayErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage.toString(), "Error", JOptionPane.WARNING_MESSAGE);
    }
    
    public void updateMainUserList(String[] users) {
        final String[] list = users;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                allUsers.clear();
                for(int i = 0; i < list.length; i++) {
                    allUsers.addElement(list[i]);
                }
                mainTab.setListModels(allUsers, allRooms);
            }
        });
    }
    
    public void updateMainChatList(String[] chats) {
        final String[] list = chats;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.out.println("Updating serverRoomList");
                allRooms.clear();
                for(int i = 0; i < list.length; i++) {
                    System.out.println("Adding room: " + list[i]);
                    allRooms.addElement(list[i]);
                }
                mainTab.setListModels(allUsers, allRooms);
            }
        });
    }
    
    public void updateChatUserList(String chatname, ArrayList<String> users) {
        final String c = chatname;
        final ArrayList<String> list = users;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (connectedRoomsCurrent.containsKey(c)) {
                    ChatRoomClient roomCurrent = connectedRoomsCurrent.get(c);
                    roomCurrent.updateUsers(list);
                }
            }
        });
    }
    
    public void updateUserChatList(String username, String[] chats) {
        final String u = username;
        final String[] list = chats;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (u.equals(client.getUsername())) {
                    for(int i = 1; i < list.length; i++) {
                        if(!connectedRoomsCurrent.containsKey(list[i])) {
                            client.send("disconnect " + u);
                        }
                    }
                }
            }
        });
    }
    
    public void joinChat(String chatname) {
        final String c = chatname;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(connectedRoomsHistory.containsKey(c)) {
                    if(connectedRoomsCurrent.containsKey(c)) {
                    } else {
                        ChatRoomClient chat = connectedRoomsHistory.get(c);
                        connectedRoomsCurrent.put(c, chat);
                        addCloseableTab(c, new ChatTab(c, MainWindow.this));
                    }
                } else {
                    
                        ChatRoomClient chat = new ChatRoomClient(c, client.getUsername());
                        connectedRoomsCurrent.put(c, chat);
                        connectedRoomsHistory.put(c, chat);
                        addCloseableTab(c, new ChatTab(c, MainWindow.this));
                }
            }
        });
    }
    
    public void leaveChat(String chatname) {
        final String c = chatname;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (connectedRoomsCurrent.containsKey(c)) {
                    JPanel removedRoom = findTab(c);
                    if (removedRoom != null) {
                        int tabIndex = tabs.indexOfComponent(removedRoom);
                        tabs.remove(tabIndex);
                    }
                } 
            }
        });
    }
    
    public void quit() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dispose();
            }
        });
    }
    
    /**
     * Sets the list models to the models we have.
     * @param userList
     * @param chatList
     */
    public void setListModels (JList userList, JList chatList) {
        userList.setModel(allUsers);
        chatList.setModel(allRooms);
    }
    
    /**
     * This method closes a particular tab in the GUI.  It takes
     * in the tab component to close and the ChatRoomClient object
     * that contains the information of the messages of the chatroom.
     * 
     * @param t
     * @param chatroom
     */
    private void closeTab(Component t, ChatRoomClient chatroom) {
        int i = tabs.indexOfTabComponent(t);
        if (i != -1) {
            if (chatroom != null) {
                client.send("exit " + chatroom.getChatRoomName());
                connectedRoomsCurrent.remove(chatroom.getChatRoomName());
                chatroom.getUserListModel().clear();
            }
            tabs.remove(i);
        }
    }

    public DefaultListModel getRoomModel() {
        return allRooms;
    }

    public DefaultListModel getUsersModel() {
        return allUsers;
    }
    
    public ChatRoomClient getCurrentRoom(String name) {
        return connectedRoomsCurrent.get(name);
    }
    
    public ChatRoomClient getHistoryRoom(String name) {
        return connectedRoomsHistory.get(name);
    }
    
    public Client getClient() {
        return client;
    }
}
