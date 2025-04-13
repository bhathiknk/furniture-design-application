package com.myfurniture.designapp;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Designer Login");
        setSize(400, 220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        // Using a JPanel with GridBagLayout for better alignment.
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");

        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        btnLogin = new JButton("Login");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblUsername, gbc);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblPassword, gbc);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> handleLogin());

        add(panel);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        // Use hardcoded credentials for demonstration.
        if ("designer".equals(username) && "password".equals(password)) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
            this.dispose();
            new DashboardFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
