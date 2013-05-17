package client.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.util.regex.Pattern;

import javax.swing.*;

import client.Client;

/**
 * The Main tab of our GuiChat. It's where all the chatrooms and all the users are displayed.
 * Also the location of the add a new chat button. Can only be added to a MainWindow and not any
 * other JFrames. Takes in the MainWindow it is a part of as an argument.
 *
 * NOTE: Test to perform includes creating a new chat and clicking okay (makes a chat tab), and 
 * creating a new chat and clicking cancel (does not make a new chat tab).
 */
public class MainTab extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private final JLabel uiChat;
    private final JButton makeChat;
    private final JList chatRoomList;
    private final JList userList;
    private Client client = null;
    public MainTab(MainWindow main) {
        Font TitleFont = new Font("SANS_SERIF", Font.BOLD, 24);
        uiChat = new JLabel("UIChat");
        uiChat.setFont(TitleFont);
        makeChat = new JButton("New ChatRoom");
        chatRoomList = new JList(new DefaultListModel());
        userList = new JList(new DefaultListModel());
        setName("Main Window");
        main.setListModels(userList, chatRoomList);
        
        chatRoomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane chatScroll = new JScrollPane (chatRoomList);
        chatScroll.setPreferredSize(new Dimension(700, 600));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane userScroll = new JScrollPane (userList);
        userScroll.setPreferredSize(new Dimension(250, 600));
        
        makeChat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String newChat = JOptionPane.showInputDialog(MainTab.this, "Specify a name for your new chatroom:", 
                        "Create New Chatroom", JOptionPane.PLAIN_MESSAGE);
                if (newChat.length() > 0) {
                    if (isValidChatname(newChat)) {
                        client.send("make " + newChat);
                    }
                    else {
                        JOptionPane.showMessageDialog(MainTab.this, "Error: Chatroom name cannot exceed 40 characters " +
                                "or contain any whitespace", "Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
                else {
                    makeChat.doClick();
                }
            }
        });
        
        chatRoomList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String chatName = (String)chatRoomList.getSelectedValue();
                    client.send("join " + chatName);
                }
            }
        });
        
        //defining the layout
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        
        //setting some margins around our components
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        //organizing components
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(uiChat)
                .addComponent(makeChat)
                .addGroup(layout.createParallelGroup()
                        .addComponent(userScroll)
                        .addComponent(chatScroll))
                );
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(uiChat)
                .addComponent(makeChat)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(chatScroll)
                        .addComponent(userScroll))
                );
        
    }
    
    private boolean isValidChatname(String Chatname) {
        String regex = "\\p{Graph}+";
        if (Pattern.matches(regex, Chatname) && Chatname.length() < 40) {
            return true;
        }
        return false;
    }
    
    public void setClient(Client c) {
        client = c;
    }
    
    public void setListModels(DefaultListModel users, DefaultListModel rooms) {
        userList.setModel(users);
        chatRoomList.setModel(rooms);
    }
    
    public void addRooms(Object[] chatRooms) {
        DefaultListModel chatRoomModel = (DefaultListModel) this.chatRoomList.getModel();
        for (int i = 0; i < chatRooms.length; i++) {
            chatRoomModel.add(i, chatRooms[i]);
        }
    }
    
}
