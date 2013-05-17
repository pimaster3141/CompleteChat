package client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.text.BadLocationException;

import client.*;

public class MainWindow extends JFrame implements ActionListener{
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
                MainWindow.this.dispose();
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
    
    public void setLoginWindow(LoginWindow l) {
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
    
    /**
     * This method parses the input taken in from the Client and
     * creates SwingUtilities to invokeLater and set them onto the
     * event dispatch thread.  The respective action is performed
     * based on the input from the server.
     */
    public void actionPerformed(ActionEvent e) {
        String input = e.getActionCommand();
        System.out.println(input);
        
        int firstSpaceIndex = input.indexOf(' ');
        String command;
        if (firstSpaceIndex ==  -1) {
        	command = input;
        	firstSpaceIndex = input.length()-1;
        }
        else
        	command = input.substring(0, firstSpaceIndex);
        
        if(command.equals("disconnectedServerSent")) {
            System.out.println("Actually we'll never get here because my logout was too beautiful " +
            		"for this world.");
        } else if(command.equals("message")) {
            int secondSpaceIndex = input.indexOf(' ', firstSpaceIndex+1);
            int thirdSpaceIndex = input.indexOf(' ', secondSpaceIndex+1);
            final String chatRoomName = input.substring(firstSpaceIndex + 1, secondSpaceIndex);
            final String userName = input.substring(secondSpaceIndex + 1, thirdSpaceIndex);
            final String message = input.substring(thirdSpaceIndex + 1);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if(connectedRoomsCurrent.containsKey(chatRoomName)){
                        ChatRoomClient roomCurrent = connectedRoomsCurrent.get(chatRoomName);
                        try {
                            roomCurrent.addMessage(new Message(userName, message));
                        } catch (BadLocationException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        } else {
            final String[] list = input.substring(firstSpaceIndex+1).split(" ");
            if(command.equals("serverUserList")) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        allUsers.clear();
                        for(int i = 0; i < list.length; i++) {
                            allUsers.addElement(list[i]);
                        }
                        mainTab.setListModels(allUsers, allRooms);
                    }
                });
            } else if(command.equals("serverRoomList")) {
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

            } else if(command.equals("chatUserList")) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String chatName = list[0];
                        if (connectedRoomsCurrent.containsKey(chatName)) {
                            ArrayList<String> newChatList = new ArrayList<String>();
                            for (int i = 1; i < list.length; i++) {
                                System.err.println("adding: " + list[i]);
                                newChatList.add(list[i]);
                            }
                            ChatRoomClient roomCurrent = connectedRoomsCurrent.get(list[0]);
                            roomCurrent.updateUsers(newChatList);
                        }
                    }
                });

            } else if(command.equals("clientRoomList")) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String user = list[0];
                        for(int i = 1; i < list.length; i++) {
                            if(!connectedRoomsCurrent.containsKey(list[i])) {
                                client.send("disconnect " + user);
                            }
                        }
                    }
                });
                

            } else if(command.equals("connectedRoom")) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String roomName = list[0];
                        if(connectedRoomsHistory.containsKey(roomName)) {
                            if(connectedRoomsCurrent.containsKey(roomName)) {
                            } else {
                                ChatRoomClient chat = connectedRoomsHistory.get(roomName);
                                connectedRoomsCurrent.put(roomName, chat);
                                addCloseableTab(roomName, new ChatTab(roomName, client, MainWindow.this));
                            }
                        } else {
                            
                                ChatRoomClient chat = new ChatRoomClient(roomName, client.getUsername());
                                connectedRoomsCurrent.put(roomName, chat);
                                connectedRoomsHistory.put(roomName, chat);
                                addCloseableTab(roomName, new ChatTab(roomName, client, MainWindow.this));
                        }
                    }
                });
                

            } else if(command.equals("disconnectedRoom")) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String roomName = list[0];
                        if (connectedRoomsCurrent.containsKey(roomName)) {
                            JPanel removedRoom = findTab(roomName);
                            if (removedRoom != null) {
                                int tabIndex = tabs.indexOfComponent(removedRoom);
                                tabs.remove(tabIndex);
                            }
                        } 
                    }
                });
                

            } else if (command.equals("invalidRoom")) {
                final StringBuilder errorMessage = new StringBuilder();
                for (int i = 1; i < list.length; i++) {
                    errorMessage.append(list[i] + " ");
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        MainWindow.this.displayErrorMessage(errorMessage.toString());
                    }
                });
            } else {
                System.err.println("Derp we seem to have ended up in dead code");
            }
        }
    }

    
    private void displayErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage.toString(), "Error", JOptionPane.WARNING_MESSAGE);
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
