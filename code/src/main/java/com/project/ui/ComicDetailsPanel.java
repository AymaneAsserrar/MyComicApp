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
    private JTextArea teamsArea;
    private JTextArea issueInfoArea;
    private JTextArea datesArea;
    private JScrollPane scrollPane;

    public ComicDetailsPanel() {
        setLayout(new BorderLayout());
        
        // Initialize main content
        JPanel contentSection = new JPanel(new GridBagLayout());
        
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
        scrollPane = new JScrollPane(contentSection);
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
    teamsArea = createTextArea(false);
    issueInfoArea = createTextArea(false);
    datesArea = createTextArea(false);
    ratingLabel = new JLabel();
    ratingLabel.setFont(new Font("Arial", Font.PLAIN, 14));

    // Add sections with headers
    addSection(detailsPanel, "Quick Summary", deckArea);
    addSection(detailsPanel, "Publisher", authorsArea);
    addSection(detailsPanel, "Issue Information", issueInfoArea);
    addSection(detailsPanel, "Published", datesArea);
    addSection(detailsPanel, "Description", descriptionArea);
    addSection(detailsPanel, "Characters", charactersArea);
    addSection(detailsPanel, "Teams", teamsArea);
    addSection(detailsPanel, "Rating", ratingLabel);

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

    // Display other comic details...
    deckArea.setText(comic.getDeck());
    authorsArea.setText(comic.getPublisherName());
    
    String issueInfo = String.format("Issues: %d\nFirst Issue: %s\nLast Issue: %s\nStart Year: %s",
        comic.getIssueCount(),
        comic.getFirstIssue(),
        comic.getLastIssue(),
        comic.getStartYear());
    issueInfoArea.setText(issueInfo);

    String dates = String.format("Added: %s\nLast Updated: %s",
        comic.getDateAdded(),
        comic.getDateLastUpdated());
    datesArea.setText(dates);

    descriptionArea.setText(comic.getDescription());
    charactersArea.setText(comic.getCharactersAsString());
    teamsArea.setText(comic.getTeamsAsString());

    // **Reset scroll position to top**
    SwingUtilities.invokeLater(() -> {
        if (scrollPane != null && scrollPane.getVerticalScrollBar() != null) {
            scrollPane.getVerticalScrollBar().setValue(0);
            scrollPane.getHorizontalScrollBar().setValue(0);
        }
    });

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
