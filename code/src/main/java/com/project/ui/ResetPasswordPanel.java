package com.project.ui;

import com.project.controller.UserAuthController;
import com.project.model.User;
import com.project.util.EmailUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ResetPasswordPanel extends JDialog {
    private JTextField emailField;
    private JTextField tokenField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmNewPasswordField;
    private JLabel feedbackLabel;
    private JButton sendEmailButton;
    private JButton verifyTokenButton;
    private JButton resetPasswordButton;
    private static final ConcurrentHashMap<String, String> verificationCodes = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> codeExpirationTimes = new ConcurrentHashMap<>();
    private String email;

    public ResetPasswordPanel(JFrame parentFrame) {
        super(parentFrame, "Reset Password", true); // Make it a modal dialog
        setSize(400, 350);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Reset Password", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, gbc);

        // Email Label and Field
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JLabel emailLabel = new JLabel("Email:");
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        // Feedback Label
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        feedbackLabel = new JLabel("", SwingConstants.CENTER);
        feedbackLabel.setForeground(Color.RED);
        feedbackLabel.setVisible(false);
        panel.add(feedbackLabel, gbc);

        // Send Reset Email Button
        gbc.gridy = 3;
        sendEmailButton = new JButton("Send Reset Email");
        sendEmailButton.addActionListener(e -> handleResetRequest(panel, gbc, emailLabel));
        panel.add(sendEmailButton, gbc);

        // Add panel to dialog
        add(panel);
    }

    private void handleResetRequest(JPanel panel, GridBagConstraints gbc, JLabel emailLabel) {
        email = emailField.getText().trim();
        feedbackLabel.setVisible(false);

        if (!isValidEmail(email)) {
            showFeedback("Invalid email format", true);
            return;
        }

        // Fetch the user object from the database
        User user = UserAuthController.getUserByEmail(email);

        if (user != null) {
            String verificationCode = generateVerificationCode();
            verificationCodes.put(email, verificationCode);
            codeExpirationTimes.put(email, System.currentTimeMillis() + 5 * 60 * 1000); // 5 minutes expiration

            EmailUtil.sendEmail(user.getEmail(), "Password Reset", "Your verification code is: " + verificationCode);
            showFeedback("Reset email sent successfully!", false);

            // Hide email input and button
            emailLabel.setVisible(false);
            emailField.setVisible(false);
            sendEmailButton.setVisible(false);

            // Add token input field and verification button
            addTokenInputField(panel, gbc);
        } else {
            showFeedback("No account found with email: " + email, true);
        }
    }

    private void addTokenInputField(JPanel panel, GridBagConstraints gbc) {
        // Token Field
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JLabel tokenLabel = new JLabel("Enter Token:");
        panel.add(tokenLabel, gbc);

        gbc.gridx = 1;
        tokenField = new JTextField(20);
        panel.add(tokenField, gbc);

        // Verify Token Button
        gbc.gridy = 5;
        verifyTokenButton = new JButton("Verify Token");
        verifyTokenButton.addActionListener(e -> handleTokenVerification(panel, gbc, tokenLabel));
        panel.add(verifyTokenButton, gbc);

        revalidate();
        repaint();
    }

    private void handleTokenVerification(JPanel panel, GridBagConstraints gbc, JLabel tokenLabel) {
        String token = tokenField.getText().trim();

        if (!isCodeValid(email, token)) {
            showFeedback("Invalid or expired token.", true);
            return;
        }

        showFeedback("Token verified successfully!", false);

        // Hide token input and button
        tokenLabel.setVisible(false);
        tokenField.setVisible(false);
        verifyTokenButton.setVisible(false);

        // Add fields for new password and confirmation
        addPasswordFields(panel, gbc);
    }

    private void addPasswordFields(JPanel panel, GridBagConstraints gbc) {
        // New Password Field
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("New Password:"), gbc);

        gbc.gridx = 1;
        newPasswordField = new JPasswordField(20);
        panel.add(newPasswordField, gbc);

        // Confirm Password Field
        gbc.gridy = 7;
        gbc.gridx = 0;
        panel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        confirmNewPasswordField = new JPasswordField(20);
        panel.add(confirmNewPasswordField, gbc);

        // Reset Password Button
        gbc.gridy = 8;
        resetPasswordButton = new JButton("Reset Password");
        resetPasswordButton.addActionListener(e -> handlePasswordReset());
        panel.add(resetPasswordButton, gbc);

        revalidate();
        repaint();
    }

    private void handlePasswordReset() {
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmNewPasswordField.getPassword());

        if (newPassword.length() < 6) {
            showFeedback("Password must be at least 6 characters long.", true);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showFeedback("Passwords do not match.", true);
            return;
        }

        // Update user's password in the database
        UserAuthController.updateUserPassword(email, newPassword);
        showFeedback("Password reset successfully!", false);

        // Close the dialog after success
        Timer timer = new Timer(2000, e -> dispose());
        timer.setRepeats(false);
        timer.start();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@[\\w-\\.]+\\.\\w{2,4}$";
        return Pattern.matches(emailRegex, email);
    }

    private void showFeedback(String message, boolean isError) {
        feedbackLabel.setText(message);
        feedbackLabel.setForeground(isError ? Color.RED : Color.GREEN);
        feedbackLabel.setVisible(true);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public static boolean isCodeValid(String email, String code) {
        String storedCode = verificationCodes.get(email);
        Long expirationTime = codeExpirationTimes.get(email);
        if (storedCode != null && storedCode.equals(code) && expirationTime != null && System.currentTimeMillis() < expirationTime) {
            verificationCodes.remove(email);
            codeExpirationTimes.remove(email);
            return true;
        }
        return false;
    }
}
