package com.project.ui;

import javax.swing.*;
import java.awt.*;

public class LoadingDialog extends JDialog {
    public LoadingDialog(JDialog parent, String message) {
        super(parent, "Loading", true);
        setLayout(new BorderLayout());
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);
        setSize(300, 100);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    }
    public LoadingDialog(JFrame parent, String message) {
        super(parent, "Loading", true);
        setLayout(new BorderLayout());
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);
        setSize(300, 100);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    }
    public LoadingDialog(Window parent, String message) {
        super(parent, "Loading", ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        panel.add(messageLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        
        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }
}