package client.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.*;

import client.ChatRoomClient;

public class HistoryTab extends JPanel{
	private static final long serialVersionUID = 1L;
	private final JLabel history;
    private final JTextPane convoHistory;
    private final JList pastChats;
    private final HashMap<String, ChatRoomClient> roomMapping;
    
    public HistoryTab(HashMap<String, ChatRoomClient> connectedRoomsHistory) {
        Font TitleFont = new Font("SANS_SERIF", Font.BOLD, 18);
        history = new JLabel("History");
        history.setFont(TitleFont);
        convoHistory = new JTextPane();
        roomMapping = connectedRoomsHistory;
        DefaultListModel pastChatModel = new DefaultListModel();
        String[] roomNames = connectedRoomsHistory.keySet().toArray(new String[0]);
        for (int i = 0; i<roomNames.length; i++) {
            pastChatModel.add(i, roomNames[i]);
        }
        pastChats = new JList(pastChatModel);
        setName("History");
        
        
        convoHistory.setEditable(false);
        JScrollPane convoScroll = new JScrollPane (convoHistory);
        convoScroll.setPreferredSize(new Dimension(700, 550));
        JScrollPane chatScroll = new JScrollPane (pastChats);
        chatScroll.setPreferredSize(new Dimension(250, 550));
        pastChats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        pastChats.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && pastChats.getSelectedValue() != null) {
                    ChatRoomClient chatroom = roomMapping.get(pastChats.getSelectedValue());
                    convoHistory.setStyledDocument(chatroom.getDoc());
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
