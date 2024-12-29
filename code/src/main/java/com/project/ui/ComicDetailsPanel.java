package com.project.ui;

import com.project.model.Comic;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.project.model.Hero;
import com.project.controller.SearchController;

public class ComicDetailsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JLabel titleLabel;
    private JLabel coverLabel;
    private JTextArea descriptionArea;
    private JTextArea authorsArea;
    private JPanel charactersPanel;
    private JLabel ratingLabel;
    private JButton backButton;
    private String previousPanel;
    private JTextArea deckArea;
    private JTextArea teamsArea;
    private JTextArea issueInfoArea;
    private JTextArea datesArea;
    private JScrollPane scrollPane;
    private JTextArea genresArea;

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
        backButton.addActionListener(e -> {
            UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(ComicDetailsPanel.this);
            parentFrame.showPreviousPanel();
        });

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
        genresArea = createTextArea(false);
        authorsArea = createTextArea(false);
        descriptionArea = createTextArea(true);
        charactersPanel = new JPanel();
        charactersPanel.setLayout(new BoxLayout(charactersPanel, BoxLayout.Y_AXIS));
        charactersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        teamsArea = createTextArea(false);
        issueInfoArea = createTextArea(false);
        datesArea = createTextArea(false);
        ratingLabel = new JLabel();
        ratingLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add sections with headers
        addSection(detailsPanel, "Description", descriptionArea);
        addSection(detailsPanel, "Genres", genresArea);
        addSection(detailsPanel, "Publisher", authorsArea);
        addSection(detailsPanel, "Issue Information", issueInfoArea);
        addSection(detailsPanel, "Published", datesArea);
        addSection(detailsPanel, "Characters", charactersPanel);
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
        if (component instanceof JTextArea && ((JTextArea) component).getPreferredSize().height > 50) {
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
            URL imageURL = new URI(comic.getCoverImageUrl()).toURL();
            ImageIcon icon = new ImageIcon(imageURL);
            Image img = icon.getImage().getScaledInstance(300, 450, Image.SCALE_SMOOTH);
            coverLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            coverLabel.setIcon(null);
            coverLabel.setText("Image unavailable");
        }

        // Display other comic details...
        deckArea.setText(comic.getDeck());
        genresArea.setText(comic.getGenresAsString());
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
        displayCharacterLinks(comic);
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

    public void setPreviousPanel(String panel) {
        this.previousPanel = panel;
    }

    public String getPreviousPanel() {
        return previousPanel;
    }

    private void displayCharacterLinks(Comic comic) {
        charactersPanel.removeAll();

        if (comic.getHeroes() == null || comic.getHeroes().isEmpty()) {
            JLabel noCharacters = new JLabel("No characters available");
            noCharacters.setAlignmentX(Component.LEFT_ALIGNMENT);
            charactersPanel.add(noCharacters);
        } else {
            for (Hero hero : comic.getHeroes()) {
                JLabel characterLink = new JLabel("• " + hero.getName());
                // Change style to bold black instead of blue
                characterLink.setFont(new Font("Arial", Font.BOLD, 14));
                characterLink.setForeground(Color.BLACK);
                characterLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
                characterLink.setAlignmentX(Component.LEFT_ALIGNMENT);

                characterLink.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Get full character details from API before displaying
                        SearchController searchController = new SearchController();
                        Hero detailedHero = searchController.getCharacterDetails(hero.getId());

                        if (detailedHero != null) {
                            UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(ComicDetailsPanel.this);
                            parentFrame.displayHeroDetails(detailedHero, "ComicDetails");
                        } else {
                            JOptionPane.showMessageDialog(ComicDetailsPanel.this,
                                    "Could not load character details",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        characterLink.setText("• " + hero.getName() + " (Click to view details)");
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        characterLink.setText("• " + hero.getName());
                    }
                });

                charactersPanel.add(characterLink);
                charactersPanel.add(Box.createVerticalStrut(5));
            }
        }

        charactersPanel.revalidate();
        charactersPanel.repaint();
    }
}
