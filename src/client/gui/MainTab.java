package client.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.io.PrintWriter;

import javax.swing.*;

/**
 * The Main tab of our GuiChat. It's where all the chatrooms and all the users are displayed.
 * Also the location of the add a new chat button. Can only be added to a MainWindow and not any
 * other JFrames. Takes in the MainWindow it is a part of as an argument.
 *
 * NOTE: Test to perform includes creating a new chat and clicking okay (makes a chat tab), and 
 * creating a new chat and clicking cancel (does not make a new chat tab).
 */
public class MainTab extends JPanel{

    private final JLabel uiChat;
    private final JButton makeChat;
    private final JList chatRoomList;
    private final JList userList;
    private final MainWindow myWindow;
    private final PrintWriter out;
    
    public MainTab(MainWindow myWindow, PrintWriter out) {
        Font TitleFont = new Font("SANS_SERIF", Font.BOLD, 24);
        uiChat = new JLabel("UIChat");
        uiChat.setFont(TitleFont);
        makeChat = new JButton("New ChatRoom");
        chatRoomList = new JList(new DefaultListModel());
        userList = new JList(new DefaultListModel());
        this.myWindow = myWindow;
        this.out = out;
        
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
                //TODO Check that chatname is valid, create appropriate chatroom object from name
                if ((newChat != null) && (newChat.length() > 0)) {
                    MainTab.this.myWindow.addChatTab(newChat);
                    System.out.println(newChat);
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
}
