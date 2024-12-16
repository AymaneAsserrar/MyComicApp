package com.project.ui;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

public class SignUpForm extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel errorLabel;
    private JButton signUpButton;

    public SignUpForm(JFrame parent) {
        super(parent, "Sign Up", true);
        setSize(400, 450);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        // Confirm Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        panel.add(confirmPasswordField, gbc);

        // Error label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);
        panel.add(errorLabel, gbc);

        // Sign Up button
        gbc.gridy = 4;
        signUpButton = new JButton("Sign Up");
        signUpButton.setBackground(new Color(70, 130, 180));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.addActionListener(e -> validateAndSignUp());
        panel.add(signUpButton, gbc);

        // Back to Login button
        gbc.gridy = 5;
        JButton backButton = new JButton("Back to Login");
        backButton.setForeground(Color.GRAY);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> dispose());
        panel.add(backButton, gbc);

        add(panel);
    }

    private void validateAndSignUp() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (!isValidEmail(email)) {
            showError("Invalid email format");
            return;
        }

        if (!isValidPassword(password)) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        // TODO: Call your registration service here
        // UserAuthController.registerUser(email, password);
        
        JOptionPane.showMessageDialog(this, "Account created successfully!");
        dispose();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@[\\w-\\.]+\\.\\w{2,4}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}