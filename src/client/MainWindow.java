package client;

import javax.swing.*;

public class MainWindow extends JFrame{

    private final JTabbedPane tabs;
    private final JMenuBar menuBar;
    private final JMenu file;
    private final JMenuItem getHistory;
    private final JMenuItem logout;
    
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
        JPanel mainTab = new MainTab();
        tabs.addTab("Main Window", mainTab);
        this.add(tabs);
    }
    
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainWindow main = new MainWindow();

                main.pack();
                main.setLocationRelativeTo(null);
                main.setVisible(true);
            }
        });
    }
}
