package com.project.ui;

import com.project.controller.UserAuthController;
import com.project.util.EmailUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class SignUpForm extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private static final ConcurrentHashMap<String, String> verificationCodes = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> codeExpirationTimes = new ConcurrentHashMap<>();

    public SignUpForm(JFrame parent) {
        super(parent, "Sign Up", true);
        setSize(400, 350);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        panel.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (!isValidEmail(email)) {
                    JOptionPane.showMessageDialog(SignUpForm.this, "Invalid email format.");
                    return;
                }

                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(SignUpForm.this, "Password must be at least 6 characters long.");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(SignUpForm.this, "Passwords do not match.");
                    return;
                }

                if (UserAuthController.getUserByEmail(email) != null) {
                    JOptionPane.showMessageDialog(SignUpForm.this, "Email already in use.");
                    return;
                }

                // Show loading dialog
                LoadingDialog loadingDialog = new LoadingDialog(SignUpForm.this, "Sending verification code...");
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        String verificationCode = generateVerificationCode();
                        verificationCodes.put(email, verificationCode);
                        codeExpirationTimes.put(email, System.currentTimeMillis() + 5 * 60 * 1000); // 5 minutes expiration

                        EmailUtil.sendEmail(email, "Email Verification Code", "Your verification code is: " + verificationCode);
                        return null;
                    }

                    @Override
                    protected void done() {
                        loadingDialog.dispose();
                        JOptionPane.showMessageDialog(SignUpForm.this, "Verification code sent to your email. Please verify.");
                        new VerificationForm(parent, email, password).setVisible(true);
                        dispose();
                    }
                };
                worker.execute();
                loadingDialog.setVisible(true);
            }
        });
        panel.add(signUpButton, gbc);

        add(panel);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@[\\w-\\.]+\\.\\w{2,4}$";
        return Pattern.matches(emailRegex, email);
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