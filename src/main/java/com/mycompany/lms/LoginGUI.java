package com.mycompany.lms;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginGUI extends JFrame {
    private JTextField usernameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JButton loginButton = new JButton("Login");
    private LibraryManager libraryManager;

    public LoginGUI(LibraryManager libraryManager) {
        this.libraryManager = libraryManager;
        setTitle("Login Screen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel()); // Placeholder for layout adjustment
        panel.add(loginButton);

        loginButton.addActionListener(e -> performLogin());
    }
 

    private void performLogin() {
    
    	    String username = usernameField.getText();
    	    String password = new String(passwordField.getPassword());

    	    if (libraryManager.validateLogin(username, password)) {
    	        JOptionPane.showMessageDialog(this, "Login successful!");

    	        // Close the login window
    	        this.dispose();

    	        // Open the LibraryGUI
    	        SwingUtilities.invokeLater(() -> {
    	            LibraryGUI libraryFrame = new LibraryGUI();
    	            libraryFrame.setVisible(true);
    	        });
    	    } else {
    	        JOptionPane.showMessageDialog(this, "Login failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
    	    }
    	
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryManager libraryManager = new LibraryManager();
            LoginGUI loginFrame = new LoginGUI(libraryManager);
            loginFrame.setVisible(true);
        });
    }
}
