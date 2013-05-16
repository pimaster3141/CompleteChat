package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;

import client.*;


public class MainWindow extends JFrame implements ActionListener{

    private final JTabbedPane tabs;
    private final JMenuBar menuBar;
    private final JMenu file;
    private final JMenuItem getHistory;
    private final JMenuItem logout;
    private final MainTab mainTab;
    private Client client = null;
    
    public MainWindow() {
        menuBar = new JMenuBar();
        file = new JMenu("File");
        getHistory = new JMenuItem("Chat History");
        logout = new JMenuItem("Logout");
        
        menuBar.add(file);
        file.add(getHistory);
        file.add(logout);
        this.setJMenuBar(menuBar);
        
        tabs = new JTabbedPane();
        mainTab = new MainTab();
        tabs.addTab("Main Window", mainTab);
        this.add(tabs);
        
        getHistory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                HistoryTab t = new HistoryTab();
                addCloseableTab("History", t);
            }    
        });
        
        logout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
    }
    
    public void addCloseableTab(String tabName, JPanel tab) {
        tabs.addTab(tabName, tab);
        int i = tabs.indexOfComponent(tab);
        if (i != -1) {
            tabs.setTabComponentAt(i, new ChatTabComponent(tabs));
        }
    }
    
    /**
     * So I want to make a tab that I can close with a button, so I need
     * to make a new Component to represent that tab.
     *
     */
    private class ChatTabComponent extends JPanel {
        private final JTabbedPane pane;
        
        private ChatTabComponent(final JTabbedPane pane) {
            if (pane == null) {
                throw new NullPointerException("Tabbed Pane is null");   
            }
            this.pane = pane;
            setOpaque(false);
            
            JLabel name = new JLabel() {
                public String getText() {
                    int i = pane.indexOfTabComponent(ChatTabComponent.this);
                    if (i != -1) {
                        return pane.getTitleAt(i);
                    }
                    return null;
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
                    int i = pane.indexOfTabComponent(ChatTabComponent.this);
                    if (i != -1) {
                        //TODO: appropriate disconnect room from server stuff
                        pane.remove(i);
                    }
                }
            });
            add(exit);
        }
    }
    
    public void setClient(Client c) {
        client = c;
        mainTab.setClient(c);
        mainTab.setListModels(c.getRoomModel(), c.getUsersModel());
    }
    
    public void addRooms(Object[] ChatRooms) {
        mainTab.addRooms(ChatRooms);
    }
    
    public void actionPerformed(ActionEvent e) {
        
    }
    
    public static void main(final String[] args) {
    	
    	
    	
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainWindow main = new MainWindow();
                Client client = null;
                try {
                    client = new Client("user2", "127.0.0.1", 10000);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ChatTab test1 = new ChatTab("Test1", client);
                main.addCloseableTab("Test1", test1);

                main.pack();
                main.setLocationRelativeTo(null);
                main.setVisible(true);
                ChatTab test2 = new ChatTab("ReallyLongTestNameBecauseYeah2", client);
                main.addCloseableTab("ReallyLongTestNameBecauseYeah2", test2);
            }
        });
    }
}
