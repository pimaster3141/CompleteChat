package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.PrintWriter;

import javax.swing.*;


public class MainWindow extends JFrame{

    private final JTabbedPane tabs;
    private final JMenuBar menuBar;
    private final JMenu file;
    private final JMenuItem getHistory;
    private final JMenuItem logout;
    private final PrintWriter out;
    
    public MainWindow(PrintWriter out) {
        menuBar = new JMenuBar();
        file = new JMenu("File");
        getHistory = new JMenuItem("Chat History");
        logout = new JMenuItem("Logout");
        this.out = out;
        
        menuBar.add(file);
        file.add(getHistory);
        file.add(logout);
        this.setJMenuBar(menuBar);
        
        tabs = new JTabbedPane();
        JPanel mainTab = new MainTab(this, out);
        tabs.addTab("Main Window", mainTab);
        this.add(tabs);
    }
    
    public void addChatTab(String Chatname) {
        ChatTab newChat = new ChatTab(Chatname, this.out);
        tabs.addTab(Chatname, newChat);
        int i = tabs.indexOfComponent(newChat);
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
    
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PrintWriter testOut = new PrintWriter(System.out);
                MainWindow main = new MainWindow(testOut);
                main.addChatTab("Test1");

                main.pack();
                main.setLocationRelativeTo(null);
                main.setVisible(true);
                main.addChatTab("ReallyLongTestNameBecauseYeah2");
            }
        });
    }
}