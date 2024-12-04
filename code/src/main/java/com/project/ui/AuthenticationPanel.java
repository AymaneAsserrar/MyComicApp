package com.project.ui;

import javax.swing.*;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class AuthenticationPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;

    public AuthenticationPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Label
        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Input Panel (changed to FlowLayout to respect preferred sizes)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); 
        emailField = new JTextField("Enter your email", 20); // Set placeholder text as default text
        emailField.setPreferredSize(new Dimension(200, 30)); // Set smaller width and height
        emailField.setBackground(new Color(0, 0, 0, 0)); // Make background transparent
        emailField.setHorizontalAlignment(SwingConstants.CENTER); // Center text
        emailField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Add border

        passwordField = new JPasswordField("Enter your password", 20); // Set placeholder text as default text
        passwordField.setPreferredSize(new Dimension(200, 30)); // Set smaller width and height
        passwordField.setBackground(new Color(0, 0, 0, 0)); // Make background transparent
        passwordField.setHorizontalAlignment(SwingConstants.CENTER); // Center text
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Add border

        // Add focus listeners to clear the placeholder text
        emailField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (emailField.getText().equals("Enter your email")) {
                    emailField.setText(""); // Clear placeholder text
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (emailField.getText().isEmpty()) {
                    emailField.setText("Enter your email"); // Restore placeholder text
                }
            }
        });

        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                char[] password = passwordField.getPassword();
                if (new String(password).equals("Enter your password")) {
                    passwordField.setText(""); // Clear placeholder text
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                char[] password = passwordField.getPassword();
                if (new String(password).isEmpty()) {
                    passwordField.setText("Enter your password"); // Restore placeholder text
                }
            }
        });

        // Error Label
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);

        inputPanel.add(emailField);
        inputPanel.add(passwordField);
        inputPanel.add(errorLabel);

        add(inputPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        loginButton = new JButton("Login");
        loginButton.setEnabled(false);

        JButton forgotPasswordButton = new JButton("Forgot Password?");
        JButton signUpButton = new JButton("Sign Up");

        buttonPanel.add(loginButton);
        buttonPanel.add(forgotPasswordButton);
        buttonPanel.add(signUpButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Add Listeners
        addFieldListeners();
        addButtonListeners();
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
        loginButton.setEnabled(!email.isEmpty() && !password.isEmpty());
    }

    private void addButtonListeners() {
        loginButton.addActionListener(e -> validateAndLogin());
        passwordField.addActionListener(e -> validateAndLogin()); // Pressing Enter triggers login

        // Forgot Password action
        JButton forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Redirecting to Forgot Password"));

        // Sign Up action
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Redirecting to Sign Up"));
    }

    private void validateAndLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Email validation
        if (!isValidEmail(email)) {
            showError("Invalid email format.");
            return;
        }

        // Password validation
        if (password.isEmpty()) {
            showError("Password cannot be empty.");
            return;
        }

        // Validate against database (US.5.1)
        if (authenticateUser(email, password)) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
        } else {
            showError("Invalid email or password.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@[\\w-\\.]+\\.\\w{2,4}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean authenticateUser(String email, String password) {
        // Mock database validation (replace with real database query)
        return email.equals("test@example.com") && password.equals("password123");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
