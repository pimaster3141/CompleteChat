package client;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;

public class MainTab extends JPanel{

    private final JLabel uiChat;
    private final JButton makeChat;
    private final JList chatRoomList;
    private final JList userList;
    
    public MainTab() {
        Font TitleFont = new Font("SANS_SERIF", Font.BOLD, 24);
        uiChat = new JLabel("UIChat");
        uiChat.setFont(TitleFont);
        makeChat = new JButton("New ChatRoom");
        chatRoomList = new JList();
        userList = new JList();
        
        JScrollPane chatScroll = new JScrollPane (chatRoomList);
        chatScroll.setPreferredSize(new Dimension(700, 600));
        JScrollPane userScroll = new JScrollPane (userList);
        userScroll.setPreferredSize(new Dimension(250, 600));
        
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
    
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame main = new JFrame();
                main.add(new MainTab());

                main.pack();
                main.setLocationRelativeTo(null);
                main.setVisible(true);
            }
        });
    }
}
