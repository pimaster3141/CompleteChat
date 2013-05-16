package client;

import java.awt.CardLayout;
import java.io.PrintWriter;

import javax.swing.*;

import client.gui.*;

public class CompleteChat {

    private final LoginWindow login;
    private final MainWindow main;
    
    public CompleteChat() {
        PrintWriter out = new PrintWriter(System.out);
        main = new MainWindow(out);
        login = new LoginWindow(main);
        
    }
    
    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                main.pack();
                main.setLocationRelativeTo(null);
                main.setVisible(true);
                Client c = login.getClient();
                if (c == null) {
                    System.out.println("closed login window");
                    main.dispose();
                }
            }
        });
    }
    
    public static void main(final String[] args) {
        CompleteChat CC = new CompleteChat();
        CC.start();
    }
}
