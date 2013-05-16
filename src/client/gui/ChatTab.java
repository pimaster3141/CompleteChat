package client.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

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

    private final JLabel chatName;
    private final JTextPane conversation;
    private final JList currentUsers;
    private final JTextField myMessage;
    private final JButton send;
    private final DefaultListModel userModel;
    private Client client;
    
    public ChatTab(String chatname, Client client) {
        Font TitleFont = new Font("SANS_SERIF", Font.BOLD, 18);
        chatName = new JLabel(chatname);
        chatName.setFont(TitleFont);
        conversation = new JTextPane();
        conversation.setDocument(client.getCurrentRoom(chatname).getDoc());
        userModel = new DefaultListModel();
        currentUsers = new JList(userModel);
        myMessage = new JTextField();
        send = new JButton("Submit");
        this.client = client;
        
        conversation.setEditable(false);
        JScrollPane chatScroll = new JScrollPane (conversation);
        chatScroll.setPreferredSize(new Dimension(700, 550));
        JScrollPane userScroll = new JScrollPane (currentUsers);
        userScroll.setPreferredSize(new Dimension(250, 550));
        
        send.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                String m = myMessage.getText();
                if (m != null && m.length() > 0) {
                    ChatTab.this.client.send("message " + m);
                    ChatTab.this.myMessage.setText("");
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
    
    /**
     * Checks to see if we are in the client's list of current rooms. If we are, we update
     * the model inside of the ChatRoomClient object, which should result in the textArea being
     * updated.
     * @param message the Message object containing the message to be added to the convo.
     * @throws BadLocationException
     */
    private void updateConvo(Message message) throws BadLocationException {
        ChatRoomClient here = client.getCurrentRoom(chatName.getText());
        if (here == null) {
            //panic since i guess we're not synched up
        }
        here.addMessage(message);
    }
    
    private void updateUsers() {
        ChatRoomClient here = client.getCurrentRoom(chatName.getText());
        if (here == null) {
            //panic since i guess we're not synched up
        }
        ArrayList<String> users = here.getConnectedUsers();
        userModel.clear();
        for (int i = 0; i < users.size(); i++) {
            userModel.setElementAt(users.get(i), i);
        }
        
    }
    
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame main = new JFrame();
                Client c = null;
                try {
                    c = new Client("user2", "127.0.0.1", 10000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                main.add(new ChatTab("Testing", c));

                main.pack();
                main.setLocationRelativeTo(null);
                main.setVisible(true);
            }
        });
    }
}
