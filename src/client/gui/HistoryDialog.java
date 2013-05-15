package client.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.io.PrintWriter;

import javax.swing.*;
import javax.swing.event.*;

import client.ChatRoomClient;

public class HistoryDialog extends JPanel{

    private final JLabel history;
    private final JTextPane convoHistory;
    private final JList pastChats;
    private static PrintWriter out;
    
    public HistoryDialog(PrintWriter out) {
        Font TitleFont = new Font("SANS_SERIF", Font.BOLD, 18);
        history = new JLabel("History");
        history.setFont(TitleFont);
        convoHistory = new JTextPane();
        pastChats = new JList();
        this.out = out;
        
        convoHistory.setEditable(false);
        JScrollPane convoScroll = new JScrollPane (convoHistory);
        convoScroll.setPreferredSize(new Dimension(700, 550));
        JScrollPane chatScroll = new JScrollPane (pastChats);
        chatScroll.setPreferredSize(new Dimension(250, 550));
        pastChats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        pastChats.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ChatRoomClient chatName = (ChatRoomClient) pastChats.getSelectedValue();
                    //convoHistory.setStyledDocument(chatName.getRoomModel());
                    //or just whatever in order to grab the right model
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
                .addComponent(history)
                .addGroup(layout.createParallelGroup()
                        .addComponent(convoScroll)
                        .addComponent(chatScroll)));
        
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap(15, 22)
                        .addComponent(history))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(convoScroll)
                        .addComponent(chatScroll)));
    }
}
