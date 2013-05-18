package client;

import javax.swing.*;

import client.gui.*;

/**
 * Complete chat organizes the LoginWindow, the MainWindow,
 * and the Client all together.  The complete chat begins
 * the entire client workable from the GUI.
 *
 */
public class CompleteChat {

    private final LoginWindow login;
    private final MainWindow main;
    private Client c = null;
    private Thread consumer;
    
    /**
     * Constructor
     */
    public CompleteChat() {
        main = new MainWindow();
        login = new LoginWindow(main);
    }
    
    /**
     * The start method starts the main window which opens
     * the login window dialog which does not return until
     * a valid client is found.  Attempts to make a client
     * will continue until one is found.  Then the main
     * window will begin and the input/output stream between
     * client and server will continue properly.
     */
    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                main.pack();
                main.setLocationRelativeTo(null);
                main.setVisible(true);
                c = login.getClient();
                if (c == null) {
                    System.out.println("closed login window");
                    main.dispose();
                }
                else {
                    main.setClient(c);
                    consumer = new Thread()
                    {
                    	public void run()
                    	{
                    		c.start(main);
                    	}
                    };
                    consumer.start();
                }
            }
        });
    }
    
    public static void main(final String[] args) {
        CompleteChat CC = new CompleteChat();
        CC.start();
    }
}
