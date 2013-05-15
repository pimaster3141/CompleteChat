package client.gui;

import javax.swing.*;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginWindow extends JFrame {

    private final JLabel welcome;
    private final JTextField username;
    private final JLabel usernameLabel;
    private final JTextField ipAddress;
    private final JLabel ipLabel;
    private final JTextField port;
    private final JLabel portLabel;
    private final JButton submitInfo;
    private final JLabel errorMessage;
    private final JTextArea allUsers;

    public LoginWindow () {
        welcome = new JLabel("Welcome to GuiChat!");
        username = new JTextField(20);
        usernameLabel = new JLabel("Username");
        ipAddress = new JTextField(20);
        ipLabel = new JLabel("IP Address");
        port = new JTextField("10000", 20);
        portLabel = new JLabel("Port Number");
        submitInfo = new JButton("Submit");
        Font errorFont = new Font("SANS_SERIF", Font.BOLD, 12);
        errorMessage = new JLabel();
        errorMessage.setFont(errorFont);
        allUsers = new JTextArea(0, 30);
        allUsers.setEditable(false);

        JScrollPane userScroll = new JScrollPane(allUsers);

        submitInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                System.out.println(username.getText());
//                if ((username.getText() == null) || username.getText().length() < 1) {
//                    errorMessage.setText("<html>Error: Valid username required to login</html>");
//                }
//                else if ((ipAddress.getText() == null) || ipAddress.getText().length() < 1) {
//                    errorMessage.setText("<html>Error: IP Address required to connect to server</html>");
//                }
//                else if ((port.getText() == null) || port.getText().length() < 1) {
//                    errorMessage.setText("<html>Error: Port number required to connect</html>");
//                }
//                else {
//                    if (username.getText().equals("Test")) {
//                        //should actually be a check for correct formatting
//                        System.out.println("Hi");
//                        errorMessage.setText("<html>Error: Username must be less than 20 characters<br> " +
//                                "and contain no whitespace</html>");
//                    }
//                }
//                LoginWindow.this.pack();
                LoginWindow.this.dispose();
            }
        });

        // defining the layout
        Container cp = this.getContentPane();
        GroupLayout layout = new GroupLayout(cp);
        cp.setLayout(layout);

        //setting some margins around our components
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        //organizing components in this view
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(welcome)
                .addGroup(layout.createParallelGroup()
                        .addComponent(usernameLabel)
                        .addComponent(username, GroupLayout.PREFERRED_SIZE, 
                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup()
                        .addComponent(ipLabel)
                        .addComponent(ipAddress, GroupLayout.PREFERRED_SIZE, 
                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup()
                        .addComponent(portLabel)
                        .addComponent(port, GroupLayout.PREFERRED_SIZE, 
                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addComponent(errorMessage, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(userScroll, GroupLayout.PREFERRED_SIZE, 
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(submitInfo)
                );
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(welcome)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(usernameLabel)
                                .addComponent(ipLabel)
                                .addComponent(portLabel))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(username, GroupLayout.PREFERRED_SIZE, 
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(ipAddress, GroupLayout.PREFERRED_SIZE, 
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(port, GroupLayout.PREFERRED_SIZE, 
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                 .addComponent(errorMessage, 0, 10, 400)
                 .addComponent(userScroll, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE, 
                         GroupLayout.PREFERRED_SIZE)
                 .addComponent(submitInfo)
                );
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginWindow main = new LoginWindow();

                main.pack();
                main.setLocationRelativeTo(null);
                main.setResizable(false);
                main.setVisible(false);
            }
        });
    }
}
