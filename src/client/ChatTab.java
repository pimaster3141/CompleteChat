package client;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;

public class ChatTab extends JPanel{

    private final JLabel chatName;
    private final JTextArea conversation;
    private final JList currentUsers;
    private final JTextField myMessage;
    private final JButton send;
    
    public ChatTab(String chatname) {
        Font TitleFont = new Font("SANS_SERIF", Font.BOLD, 18);
        chatName = new JLabel(chatname);
        chatName.setFont(TitleFont);
        conversation = new JTextArea();
        currentUsers = new JList();
        myMessage = new JTextField();
        send = new JButton("Submit");
        
        JScrollPane chatScroll = new JScrollPane (conversation);
        chatScroll.setPreferredSize(new Dimension(700, 550));
        JScrollPane userScroll = new JScrollPane (currentUsers);
        userScroll.setPreferredSize(new Dimension(250, 550));
        
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
    
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame main = new JFrame();
                main.add(new ChatTab("Testing"));

                main.pack();
                main.setLocationRelativeTo(null);
                main.setVisible(true);
            }
        });
    }
}
