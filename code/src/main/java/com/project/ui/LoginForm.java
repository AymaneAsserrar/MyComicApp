package com.project.ui;

import javax.swing.*;
import javax.swing.event.DocumentListener;

import com.project.controller.UserAuthController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class LoginForm extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public LoginForm(JFrame parent) {
        super(parent, "Login", true);
        setSize(400, 400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email Label and Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        // Password Label and Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        // Error Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);
        panel.add(errorLabel, gbc);

        // Login Button
        gbc.gridy = 3;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateAndLogin(parent);
            }
        });
        panel.add(loginButton, gbc);

        // Forgot Password Button
        gbc.gridy = 4;
        JButton forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setForeground(Color.GRAY);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(LoginForm.this, "Redirecting to Forgot Password");
            }
        });
        panel.add(forgotPasswordButton, gbc);

        // Sign Up Button
        gbc.gridy = 5;
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setBackground(new Color(70, 130, 180)); // Steel Blue color
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFocusPainted(false);
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpButton.addActionListener(e -> {
            SignUpForm signUpForm = new SignUpForm(parent);
            signUpForm.setVisible(true);
            dispose(); // Optional: close login form when opening signup
        });
        panel.add(signUpButton, gbc);

        add(panel);
        addFieldListeners();
    }

    private void addFieldListeners() {
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                toggleLoginButton();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                toggleLoginButton();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                toggleLoginButton();
            }
        };

        emailField.getDocument().addDocumentListener(documentListener);
        passwordField.getDocument().addDocumentListener(documentListener);
    }

    private void toggleLoginButton() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        errorLabel.setVisible(false);

        if (!isValidEmail(email)) {
            showError("Invalid email format");
        }
    }

    private void validateAndLogin(JFrame parent) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validate email format
        if (!isValidEmail(email)) {
            showError("Invalid email format");
            return;
        }
        if (!isValidPassword(password)) {
            showError("Password cannot be empty");
            return;
        }
        
        // Get the result from UserAuthController
        String validationMessage = UserAuthController.validateCredentials(email, password);

        if ("SUCCESS".equals(validationMessage)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
        } else {
            // Display the specific error message from the controller
            showError(validationMessage);
        }
    }


    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@[\\w-\\.]+\\.\\w{2,4}$";
        return Pattern.matches(emailRegex, email);
    }
    
    private boolean isValidPassword(String password) {
        return password != null && !password.isEmpty();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}