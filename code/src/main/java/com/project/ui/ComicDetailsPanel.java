package com.project.ui;

import com.project.model.Comic;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ComicDetailsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JLabel titleLabel;
    private JLabel coverLabel; 
    private JTextArea descriptionArea;
    private JLabel authorsLabel;
    private JTextArea charactersArea;
    private JLabel ratingLabel;
    private JButton backButton;
    private JPanel mainContent;
    private String previousPanel;

    public ComicDetailsPanel() {
        setLayout(new BorderLayout());
        
        // Initialize components
        mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        
        // Title section
        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleSection.add(titleLabel);
        titleSection.add(Box.createVerticalStrut(20));

        // Content section with image and details
        JPanel contentSection = new JPanel(new BorderLayout(20, 0));
        
        // Left side - Cover image
        coverLabel = new JLabel();
        coverLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        contentSection.add(coverLabel, BorderLayout.WEST);
        
        // Right side - Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        
        // Authors section
        JLabel authorsHeader = new JLabel("Authors");
        authorsHeader.setFont(new Font("Arial", Font.BOLD, 16));
        authorsLabel = new JLabel();
        authorsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Rating section
        JLabel ratingHeader = new JLabel("Rating/Popularity");
        ratingHeader.setFont(new Font("Arial", Font.BOLD, 16));
        ratingLabel = new JLabel();
        ratingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Description section
        JLabel descriptionHeader = new JLabel("Description");
        descriptionHeader.setFont(new Font("Arial", Font.BOLD, 16));
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(400, 150));
        
        // Characters section
        JLabel charactersHeader = new JLabel("Characters");
        charactersHeader.setFont(new Font("Arial", Font.BOLD, 16));
        charactersArea = new JTextArea();
        charactersArea.setEditable(false);
        charactersArea.setLineWrap(true);
        charactersArea.setWrapStyleWord(true);
        charactersArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane charScrollPane = new JScrollPane(charactersArea);
        charScrollPane.setPreferredSize(new Dimension(400, 100));

        // Add components to details panel
        detailsPanel.add(authorsHeader);
        detailsPanel.add(authorsLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(ratingHeader);
        detailsPanel.add(ratingLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(descriptionHeader);
        detailsPanel.add(descScrollPane);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(charactersHeader);
        detailsPanel.add(charScrollPane);
        
        contentSection.add(detailsPanel, BorderLayout.CENTER);
        
        // Back button
        backButton = new JButton("Back");
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add all sections to main panel
        add(backButton, BorderLayout.NORTH);
        mainContent.add(titleSection);
        mainContent.add(contentSection);
        
        add(new JScrollPane(mainContent), BorderLayout.CENTER);
    }

    public void displayComicDetails(Comic comic) {
        titleLabel.setText(comic.getName());
        
        try {
            URL imageURL = new URL(comic.getCoverImageUrl());
            ImageIcon icon = new ImageIcon(imageURL);
            Image img = icon.getImage().getScaledInstance(300, 450, Image.SCALE_SMOOTH);
            coverLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            coverLabel.setIcon(null);
            coverLabel.setText("Image unavailable");
        }
        
        authorsLabel.setText(comic.getAuthors() != null ? comic.getAuthors() : "Unknown");
        ratingLabel.setText(comic.getRating() != null ? comic.getRating() : "N/A");
        descriptionArea.setText(comic.getDescription());
        charactersArea.setText(comic.getCharactersAsString());
        
        revalidate();
        repaint();
    }
    
    public void addBackButtonListener(java.awt.event.ActionListener listener) {
        backButton.addActionListener(listener);
    }

    public void setPreviousPanel(String panel) {
        this.previousPanel = panel;
    }

    public String getPreviousPanel() {
        return previousPanel;
    }
}
