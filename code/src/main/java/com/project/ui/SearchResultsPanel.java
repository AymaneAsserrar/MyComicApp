package com.project.ui;

import com.project.controller.SearchController;
import com.project.model.Comic;
import com.project.model.Hero;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.project.util.DatabaseUtil;
import com.project.model.UserLibraryController;

public class SearchResultsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private SearchController searchController;
    private JPanel resultsGridPanel;
    private String currentSearchText;
    private String currentSearchType;
    private JLabel searchResultsLabel;
    private Map<JButton, Comic> starButtons = new HashMap<>();
    private Map<JButton, Comic> readButtons = new HashMap<>();

    public SearchResultsPanel() {
        setLayout(new BorderLayout());

        searchResultsLabel = new JLabel("Comics Found", SwingConstants.CENTER);
        searchResultsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(searchResultsLabel, BorderLayout.NORTH);

        resultsGridPanel = new JPanel(new GridLayout(0, 4, 15, 15)); // Grid layout with 4 columns
        resultsGridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        resultsGridPanel.setBackground(new Color(255, 255, 255)); // White background for comics grid

        JScrollPane scrollPane = new JScrollPane(resultsGridPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(48); // Increase scroll speed
        add(scrollPane, BorderLayout.CENTER);

        searchController = new SearchController();
    }

    public void displayResults(String searchText, String searchType) {
        currentSearchText = searchText;
        currentSearchType = searchType;
        loadResults();
    }

    private void loadResults() {
        SearchController.SearchResult searchResult;
        if ("Comic".equals(currentSearchType)) {
            searchResult = searchController.searchComicsByTitle(currentSearchText, 0, 10);
            searchResultsLabel.setText("Comics Found");
        } else if ("Character".equals(currentSearchType)) {
            searchResult = searchController.searchCharactersByName(currentSearchText);
            searchResultsLabel.setText("Characters Found");
        } else {
            searchResult = new SearchController.SearchResult(List.of(), 0);
        }

        List<?> searchResults = searchResult.getResults();

        resultsGridPanel.removeAll();
        if (searchResults.isEmpty()) {
            searchResultsLabel.setText("No results found");
        } else {
            if ("Comic".equals(currentSearchType)) {
                for (Object result : searchResults) {
                    addComicPanel((Comic) result);
                }
            } else if ("Character".equals(currentSearchType)) {
                for (Object result : searchResults) {
                    addHeroPanel((Hero) result);
                }
            }
        }

        resultsGridPanel.revalidate();
        resultsGridPanel.repaint();
        revalidate();
        repaint();
    }

    private void addComicPanel(Comic comic) {
        JPanel comicPanel = new JPanel(new BorderLayout());
        comicPanel.setPreferredSize(new Dimension(200, 300));
        comicPanel.setBackground(Color.WHITE);

        // Shadow effect
        comicPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0, 0, 0, 30))));

        // Create star button
        JButton starButton = new JButton();
        starButton.setFocusPainted(false);
        starButton.setBorderPainted(false);
        starButton.setContentAreaFilled(false);
        starButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setupStarButton(starButton, comic);

        // Create heart button
        JButton likeButton = new JButton();
        likeButton.setFocusPainted(false);
        likeButton.setBorderPainted(false);
        likeButton.setContentAreaFilled(false);
        likeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setupHeartButton(likeButton, comic);

        // Create read button
        JButton readButton = new JButton();
        readButton.setFocusPainted(false);
        readButton.setBorderPainted(false);
        readButton.setContentAreaFilled(false);
        readButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setupReadButton(readButton, comic);


        // Panel to hold buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(starButton);
        buttonPanel.add(likeButton);
        buttonPanel.add(readButton);

        // Cover image
        JLabel coverLabel = new JLabel();
        coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            URL imageURL = new URL(comic.getCoverImageUrl());
            ImageIcon icon = new ImageIcon(imageURL);
            Image img = icon.getImage().getScaledInstance(180, 250, Image.SCALE_SMOOTH);
            coverLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            coverLabel.setText("Image unavailable");
            coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        // Comic title
        JLabel titleLabel = new JLabel(comic.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.BLACK); // Set title color to black
        titleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        comicPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comicPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Comic detailedComic = searchController.getComicDetails(comic.getId());
                if (detailedComic != null) {
                    UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(SearchResultsPanel.this);
                    parentFrame.displayComicDetails(detailedComic, "SearchResults");
                }
            }
        });

        comicPanel.add(coverLabel, BorderLayout.CENTER);
        comicPanel.add(titleLabel, BorderLayout.SOUTH);
        comicPanel.add(buttonPanel, BorderLayout.NORTH);
        resultsGridPanel.add(comicPanel);
    }

    private void addHeroPanel(Hero hero) {
        // Create panel with same dimensions and style as comic panels
        JPanel heroPanel = new JPanel(new BorderLayout());
        heroPanel.setPreferredSize(new Dimension(200, 300));
        heroPanel.setBackground(Color.WHITE);

        // Add same shadow effect
        heroPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0, 0, 0, 30))));

        // Cover image with same dimensions
        JLabel coverLabel = new JLabel();
        coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            URL imageURL = new URL(hero.getImageUrl());
            ImageIcon icon = new ImageIcon(imageURL);
            Image img = icon.getImage().getScaledInstance(180, 250, Image.SCALE_SMOOTH);
            coverLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            coverLabel.setText("Image unavailable");
            coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        // Title label with same styling
        JLabel titleLabel = new JLabel(hero.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        // Add click behavior
        heroPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        heroPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Hero detailedHero = searchController.getCharacterDetails(hero.getId());
                if (detailedHero != null) {
                    UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(SearchResultsPanel.this);
                    parentFrame.displayHeroDetails(detailedHero, "SearchResults");
                }
            }
        });

        // Add components to panel
        heroPanel.add(coverLabel, BorderLayout.CENTER);
        heroPanel.add(titleLabel, BorderLayout.SOUTH);

        // Add to grid
        resultsGridPanel.add(heroPanel);
    }

    private void setupStarButton(JButton starButton, Comic comic) {
        starButtons.put(starButton, comic);  // Track button-comic mapping
        URL wStarURL = getClass().getClassLoader().getResource("wStar.png");
        URL yStarURL = getClass().getClassLoader().getResource("yStar.png");
        URL ownedURL = getClass().getClassLoader().getResource("Owned.png");
    
        if (wStarURL != null && yStarURL != null && ownedURL != null) {
            ImageIcon wStarIcon = new ImageIcon(wStarURL);
            ImageIcon yStarIcon = new ImageIcon(yStarURL);
            ImageIcon ownedIcon = new ImageIcon(ownedURL);
            Image wStarImage = wStarIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            Image yStarImage = yStarIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            Image ownedImage = ownedIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    
            UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(this);
            String userEmail = parentFrame.getCurrentUserEmail();
            if (userEmail == null || userEmail.isEmpty()) {
                starButton.setIcon(new ImageIcon(wStarImage));
                starButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Please login to manage wishlist"));
                return;
            }
    
            int userId = getUserId(userEmail);
            UserLibraryController controller = new UserLibraryController();
            
            // Get initial state once
            String currentStatus = controller.getComicStatus(userId, comic.getId());
            
            // Set initial icon
            switch (currentStatus) {
                case "owned":
                    starButton.setIcon(new ImageIcon(ownedImage));
                    break;
                case "ystar":
                    starButton.setIcon(new ImageIcon(yStarImage));
                    break;
                default:
                    starButton.setIcon(new ImageIcon(wStarImage));
            }
    
            // Simple click handler with minimal operations
            starButton.addActionListener(e -> {
                String status = controller.getComicStatus(userId, comic.getId());
                boolean updated = false;
                
                switch (status) {
                    case "owned":
                        updated = controller.updateComicOwnership(userId, comic.getId(), null);
                        if (updated) starButton.setIcon(new ImageIcon(wStarImage));
                        break;
                    case "ystar":
                        updated = controller.updateComicOwnership(userId, comic.getId(), 1);
                        if (updated) starButton.setIcon(new ImageIcon(ownedImage));
                        break;
                    default:
                        updated = controller.updateComicOwnership(userId, comic.getId(), 0);
                        if (updated) starButton.setIcon(new ImageIcon(yStarImage));
                }
                
                if (updated) {
                    refreshComicsGrid(); // Reload comics instead of just refreshing buttons
                }
            });
        }
    }
    
    public void refreshStarButtons() {
        refreshComicsGrid();
    }

    private void setupHeartButton(JButton heartButton, Comic comic) {
        // Similar approach to RecommendationPanel
        URL whiteHeartURL = getClass().getClassLoader().getResource("white.png");
        URL redHeartURL = getClass().getClassLoader().getResource("heart.png");

        if (whiteHeartURL != null && redHeartURL != null) {
            ImageIcon whiteHeartIcon = new ImageIcon(whiteHeartURL);
            ImageIcon redHeartIcon = new ImageIcon(redHeartURL);
            Image whiteHeartImage = whiteHeartIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            Image redHeartImageScaled = redHeartIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);

            UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(this);
            String userEmail = parentFrame.getCurrentUserEmail();
            heartButton.removeActionListener(heartButton.getActionListeners().length > 0
                    ? heartButton.getActionListeners()[0]
                    : null);

            if (userEmail == null || userEmail.isEmpty()) {
                heartButton.setIcon(new ImageIcon(whiteHeartImage));
                heartButton
                        .addActionListener(e -> JOptionPane.showMessageDialog(this, "Please login to manage library."));
                return;
            }

            int userId = getUserId(userEmail);
            UserLibraryController controller = new UserLibraryController();
            boolean isInLibrary = controller.isComicInLibrary(userId, comic.getId());

            heartButton.setIcon(isInLibrary
                    ? new ImageIcon(redHeartImageScaled)
                    : new ImageIcon(whiteHeartImage));

            heartButton.addActionListener(e -> {
                boolean currentState = controller.isComicInLibrary(userId, comic.getId());
                if (currentState) {
                    if (controller.removeComicFromLibrary(userId, comic.getId())) {
                        heartButton.setIcon(new ImageIcon(whiteHeartImage));
                    }
                } else {
                    if (controller.addComicToLibrary(userId, comic)) {
                        heartButton.setIcon(new ImageIcon(redHeartImageScaled));
                    }
                }
            });
        }
    }
    private void setupReadButton(JButton readButton, Comic comic) {
        URL notReadingURL = getClass().getClassLoader().getResource("notreading.png");
        URL readingURL = getClass().getClassLoader().getResource("currentlyreading.png");
        URL finishedURL = getClass().getClassLoader().getResource("finished.png");
    
        if (notReadingURL != null && readingURL != null && finishedURL != null) {
            ImageIcon notReadingIcon = new ImageIcon(notReadingURL);
            ImageIcon readingIcon = new ImageIcon(readingURL);
            ImageIcon finishedIcon = new ImageIcon(finishedURL);
            
            Image notReadingImage = notReadingIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            Image readingImage = readingIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            Image finishedImage = finishedIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    
            UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(this);
            String userEmail = parentFrame.getCurrentUserEmail();
    
            // Remove existing action listeners
            ActionListener[] listeners = readButton.getActionListeners();
            for (ActionListener listener : listeners) {
                readButton.removeActionListener(listener);
            }
    
            if (userEmail == null || userEmail.isEmpty()) {
                readButton.setIcon(new ImageIcon(notReadingImage));
                readButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Please login to manage reading status"));
                return;
            }
    
            int userId = getUserId(userEmail);
            UserLibraryController controller = new UserLibraryController();
            String currentStatus = controller.getReadStatus(userId, comic.getId());
    
            // Set initial icon based on status
            switch (currentStatus) {
                case "reading":
                    readButton.setIcon(new ImageIcon(readingImage));
                    break;
                case "finished":
                    readButton.setIcon(new ImageIcon(finishedImage));
                    break;
                default:
                    readButton.setIcon(new ImageIcon(notReadingImage));
            }
    
            // Add click handler
            readButton.addActionListener(e -> {
                String status = controller.getReadStatus(userId, comic.getId());
                boolean updated = false;
    
                switch (status) {
                    case "notreading":
                        updated = controller.updateReadStatus(userId, comic.getId(), 0); // Set to reading
                        if (updated) {
                            readButton.setIcon(new ImageIcon(readingImage));
                        }
                        break;
                    case "reading":
                        updated = controller.updateReadStatus(userId, comic.getId(), 1); // Set to finished
                        if (updated) {
                            readButton.setIcon(new ImageIcon(finishedImage));
                        }
                        break;
                    case "finished":
                        updated = controller.resetReadStatus(userId, comic.getId()); // Reset to not reading
                        if (updated) {
                            readButton.setIcon(new ImageIcon(notReadingImage));
                        }
                        break;
                }
    
                if (updated) {
                    refreshComicsGrid(); // Reload comics instead of just refreshing buttons
                }
            });
        }
    }
    public void refreshReadButtons() {
        refreshComicsGrid(); 
    }
    private int getUserId(String email) {
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT id FROM user WHERE email = ?")) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void refreshHeartButtons() {
        refreshComicsGrid();
    }
    // Add refresh method for comics grid
    private void refreshComicsGrid() {
        SwingUtilities.invokeLater(() -> {
            loadResults(); // Reload all comics
        });
    }
}