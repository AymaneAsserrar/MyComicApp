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
    private JTextArea authorsArea;
    private JTextArea charactersArea;
    private JLabel ratingLabel;
    private JButton backButton;
    private JPanel mainContent;
    private String previousPanel;
    private JTextArea deckArea;

    public ComicDetailsPanel() {
        setLayout(new BorderLayout());
        
        // Initialize main scrollable container
        mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        
        // Title section with responsive margins
        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Back button with fixed position
        backButton = new JButton("Back");
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Content section with responsive layout
        JPanel contentSection = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Cover image panel (left side)
        coverLabel = new JLabel();
        coverLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        Dimension coverSize = new Dimension(300, 450);
        coverLabel.setPreferredSize(coverSize);
        coverLabel.setMinimumSize(coverSize);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(10, 10, 10, 20);
        contentSection.add(coverLabel, gbc);

        // Details panel (right side)
        JPanel detailsPanel = createDetailsPanel();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentSection.add(detailsPanel, gbc);
        
        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        
        // Wrap content in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentSection);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        
        // Configure text areas
        deckArea = createTextArea(true);
        authorsArea = createTextArea(false);
        descriptionArea = createTextArea(true);
        charactersArea = createTextArea(false);
        ratingLabel = new JLabel();
        ratingLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add sections with headers
        addSection(detailsPanel, "Quick Summary", deckArea);
        addSection(detailsPanel, "Authors", authorsArea);
        addSection(detailsPanel, "Rating", ratingLabel);
        addSection(detailsPanel, "Description", descriptionArea);
        addSection(detailsPanel, "Characters", charactersArea);

        return detailsPanel;
    }

    private JTextArea createTextArea(boolean multiline) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setBackground(getBackground());
        
        // Set preferred size for multiline areas
        if (multiline) {
            area.setPreferredSize(new Dimension(400, 150));
        }
        
        return area;
    }

    private void addSection(JPanel container, String title, JComponent component) {
        JLabel header = new JLabel(title);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        container.add(Box.createVerticalStrut(10));
        container.add(header);
        container.add(Box.createVerticalStrut(5));
        
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (component instanceof JTextArea && ((JTextArea)component).getPreferredSize().height > 50) {
            JScrollPane scroll = new JScrollPane(component);
            scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            container.add(scroll);
        } else {
            container.add(component);
        }
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
        
        // Display deck (short description)
        deckArea.setText(comic.getDeck());
        
        // Display authors
        authorsArea.setText(comic.getAuthorsAsString());
        
        // Display rating
        ratingLabel.setText(comic.getRating() != null ? comic.getRating() : "N/A");
        
        // Display description
        descriptionArea.setText(comic.getDescription());
        
        // Display characters
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
