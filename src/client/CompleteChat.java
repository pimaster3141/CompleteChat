package client;

import java.awt.CardLayout;
import java.io.PrintWriter;

import javax.swing.*;

import client.gui.*;

public class CompleteChat {

    private final LoginWindow login;
    private final MainWindow main;
    private final JFrame cards;
    private final CardLayout layout;
    
    public CompleteChat() {
        PrintWriter out = new PrintWriter(System.out);
        login = new LoginWindow();
        main = new MainWindow(out);
        this.cards = new JFrame();
        this.layout = new CardLayout();
        cards.setLayout(layout);
        cards.add(login, "Login Screen");
        cards.add(main, "Main Window");
    }
    
    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                

                cards.pack();
                cards.setLocationRelativeTo(null);
                cards.setVisible(true);
            }
        });
    }
    
    public static void main(final String[] args) {
        CompleteChat CC = new CompleteChat();
        CC.start();
    }
}
