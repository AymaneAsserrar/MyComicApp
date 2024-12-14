package com.project.ui;
import javax.swing.*;
import java.awt.*;
public class HeroProfilePanel extends JPanel {
    private JLabel nameLabel;
    private JTextArea descriptionArea;
    private JLabel imageLabel;
    private JList<String> titlesList;
    private JButton backButton;
    private JButton closeButton;

    public HeroProfilePanel() {
        setLayout(new BorderLayout());

        // Character Profile Section
        JPanel profilePanel = new JPanel(new BorderLayout());
        nameLabel = new JLabel("Name", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        descriptionArea = new JTextArea("Description");
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        imageLabel = new JLabel();

        profilePanel.add(nameLabel, BorderLayout.NORTH);
        profilePanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        profilePanel.add(imageLabel, BorderLayout.WEST);

        // Titles List Section
        titlesList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(titlesList);

        // Buttons Section
        JPanel buttonPanel = new JPanel();
        backButton = new JButton("Back");
        closeButton = new JButton("Close");
        buttonPanel.add(backButton);
        buttonPanel.add(closeButton);

        // Add sections to the main panel
        add(profilePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateProfile(String string, String string2, ImageIcon imageIcon, String[] strings) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateProfile'");
    }
}
