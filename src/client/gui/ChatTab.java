package client.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.*;

import client.*;

/**
 * Probably needs to take in the actual object representing a chat, but I don't
 * know what that looks like yet. I would very much like that object to have the
 * chat's name, a DefaultListModel representing the list of users (read up what it
 * does on the java docs) and a StyledDocument (model for a Swing Text Component
 * go read up the API for that).
 *
 */
public class ChatTab extends JPanel{
	private static final long serialVersionUID = 1L;
	private final JLabel chatName;
    private final JTextPane conversation;
    private final JList currentUsers;
    private final JTextField myMessage;
    private final JButton send;
    private Client client;
    private final String roomname;
    
    public ChatTab(String chatname, Client client, MainWindow main) {
    	this.roomname = chatname;
        Font TitleFont = new Font("SANS_SERIF", Font.BOLD, 18);
        chatName = new JLabel(chatname);
        chatName.setFont(TitleFont);
        conversation = new JTextPane();
        conversation.setDocument(main.getCurrentRoom(chatname).getDoc());
        currentUsers = new JList(main.getCurrentRoom(chatname).getUserListModel());
        myMessage = new JTextField();
        send = new JButton("Submit");
        this.client = client;
        setName(chatname);
        
        conversation.setEditable(false);
        JScrollPane chatScroll = new JScrollPane (conversation);
        chatScroll.setPreferredSize(new Dimension(700, 550));
        JScrollPane userScroll = new JScrollPane (currentUsers);
        userScroll.setPreferredSize(new Dimension(250, 550));
        
        send.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                sendMessage();
            }
        });

        myMessage.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER)
                    sendMessage();
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
                .addComponent(chatName)
                .addGroup(layout.createParallelGroup()
                        .addComponent(chatScroll)
                        .addComponent(userScroll))
                .addGroup(layout.createParallelGroup()
                        .addComponent(myMessage, GroupLayout.PREFERRED_SIZE, 
                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(send)));
        
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap(15, 22)
                        .addComponent(chatName))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(chatScroll)
                        .addComponent(userScroll))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(myMessage)
                        .addComponent(send)));
    }
    
    private void sendMessage() {
        String m = myMessage.getText();
        if (m != null && m.length() > 0) {
            client.send("message " + roomname + " " + m);
            myMessage.setText("");
        }
    }
}
