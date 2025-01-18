package com.project.ui;

import com.project.model.UserAuthController;
import com.project.util.Hashing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VerificationForm extends JDialog {
    private JTextField codeField;
    private String email;
    private String password;

    public VerificationForm(JFrame parent, String email, String password) {
        super(parent, "Email Verification", true);
        this.email = email;
        this.password = password;
        setSize(400, 200);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Verification Code:"), gbc);

        gbc.gridx = 1;
        codeField = new JTextField(20);
        panel.add(codeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton verifyButton = new JButton("Verify");
        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = codeField.getText();
                if (SignUpForm.isCodeValid(email, code)) {
                    UserAuthController.createUser(email, Hashing.hashPassword(password));
                    JOptionPane.showMessageDialog(VerificationForm.this, "Account created successfully!");
                    ((UiMain) parent).updateProfile(email);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(VerificationForm.this, "Invalid or expired verification code.");
                }
            }
        });
        panel.add(verifyButton, gbc);

        add(panel);
    }
}