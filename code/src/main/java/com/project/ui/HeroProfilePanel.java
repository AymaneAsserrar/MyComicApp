package com.project.ui;

import javax.swing.*;
import java.awt.*;

public class HeroProfilePanel extends JPanel {
    private JLabel nameLabel;
    private JEditorPane descriptionPane;
    private JLabel imageLabel;
    private JList<String> titlesList;

    public HeroProfilePanel() {
        setLayout(new GridLayout(1, 2, 20, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Left Panel (Image + Name + Comics)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // Image panel (smaller size)
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        imagePanel.setMaximumSize(new Dimension(150, 200));
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(130, 180));
        imagePanel.add(imageLabel);

        // Name label
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Increased font size and bold
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(Color.BLACK); // Ensure text color is black

        // Comics list section
        JLabel comicsLabel = new JLabel("Appears In Comics");
        comicsLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Increased font size and bold
        comicsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        comicsLabel.setForeground(Color.BLACK); // Ensure text color is black

        titlesList = new JList<>();
        titlesList.setFont(new Font("Arial", Font.BOLD, 16)); // Increased font size
        titlesList.setForeground(Color.BLUE); // Ensure text color is black
        titlesList.setBackground(Color.WHITE);
        JScrollPane comicsScrollPane = new JScrollPane(titlesList);
        comicsScrollPane.setPreferredSize(new Dimension(200, 200));
        comicsScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to left panel
        leftPanel.add(imagePanel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(nameLabel);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(comicsLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(comicsScrollPane);

        // Right Panel (Description only)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout(0, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel descriptionLabel = new JLabel("Description");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Increased font size and bold
        descriptionLabel.setForeground(Color.BLACK); // Ensure text color is black

        descriptionPane = new JEditorPane();
        descriptionPane.setContentType("text/html");
        descriptionPane.setEditable(false);
        descriptionPane.setFont(new Font("Arial", Font.BOLD, 16)); // Increased font size
        descriptionPane.setForeground(Color.BLUE); // Ensure text color is black
        descriptionPane.setBackground(Color.WHITE);

        JScrollPane descScrollPane = new JScrollPane(descriptionPane);

        rightPanel.add(descriptionLabel, BorderLayout.NORTH);
        rightPanel.add(descScrollPane, BorderLayout.CENTER);

        add(leftPanel);
        add(rightPanel);
    }

    public void updateProfile(String name, String description, Icon image, String[] titles) {
        nameLabel.setText(name);
        descriptionPane.setText(description != null ? description : "<p style='color:gray;'>No description available.</p>");

        // Handle image loading
        if (image != null) {
            try {
                if (image instanceof ImageIcon) {
                    Image img = ((ImageIcon) image).getImage();
                    // Scale to new smaller size
                    Image scaled = img.getScaledInstance(130, 180, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));
                } else {
                    imageLabel.setIcon(image);
                }
            } catch (Exception e) {
                imageLabel.setIcon(null);
                imageLabel.setText("No Image");
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText("No Image Available");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setFont(new Font("Arial", Font.BOLD, 12));
        }

        titlesList.setListData(titles != null ? titles : new String[]{"No appearances found"});

        revalidate();
        repaint();
    }
}