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
    private Map<JButton, Comic> validationButtons = new HashMap<>();

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

        // Create star button
        JButton validationButton = new JButton();
        validationButton.setFocusPainted(false);
        validationButton.setBorderPainted(false);
        validationButton.setContentAreaFilled(false);
        validationButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setupValidationButton(validationButton, comic);

        // Panel to hold both buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(validationButton);
        buttonPanel.add(starButton);
        buttonPanel.add(likeButton);

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

    private void setupHeartButton(JButton likeButton, Comic comic) {
        URL whiteHeartURL = getClass().getClassLoader().getResource("white.png");
        URL redHeartURL = getClass().getClassLoader().getResource("heart.png");

        if (whiteHeartURL != null && redHeartURL != null) {
            ImageIcon whiteHeartIcon = new ImageIcon(whiteHeartURL);
            ImageIcon redHeartIcon = new ImageIcon(redHeartURL);
            Image whiteHeartImage = whiteHeartIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            Image redHeartImage = redHeartIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    
            UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(this);
            String userEmail = parentFrame.getCurrentUserEmail();
            
            // Remove existing action listeners
            ActionListener[] listeners = likeButton.getActionListeners();
            for (ActionListener listener : listeners) {
                likeButton.removeActionListener(listener);
            }
            

            if (userEmail == null) {
                likeButton.setIcon(new ImageIcon(whiteHeartImage));
                likeButton.addActionListener(e -> {
                    JOptionPane.showMessageDialog(this, "Please login to add comics to your library");
                });
            } else {
                UserLibraryController controller = new UserLibraryController();
                int userId = getUserId(userEmail);
                boolean isInLibrary = controller.isComicInLibrary(userId, comic.getId());
                
                // Set initial icon based on library status
                likeButton.setIcon(isInLibrary ? new ImageIcon(redHeartImage) : new ImageIcon(whiteHeartImage));
                
                // Add click handler
                likeButton.addActionListener(e -> {
                    boolean currentState = controller.isComicInLibrary(userId, comic.getId());
                    
                    if (currentState) {
                        if (controller.removeComicFromLibrary(userId, comic.getId())) {
                            likeButton.setIcon(new ImageIcon(whiteHeartImage));
                            parentFrame.refreshAllPanels(); // Refresh all panels to update the UI
                        }
                    } else {
                        if (controller.addComicToLibrary(userId, comic)) {
                            likeButton.setIcon(new ImageIcon(redHeartImage));
                            parentFrame.refreshAllPanels(); // Refresh all panels to update the UI
                        }
                    }
                });
            }
        }
    }

    private void setupStarButton(JButton starButton, Comic comic) {
        starButtons.put(starButton, comic);
        URL wStarURL = getClass().getClassLoader().getResource("wStar.png");
        URL yStarURL = getClass().getClassLoader().getResource("yStar.png");

        if (wStarURL != null && yStarURL != null) {
            ImageIcon wStarIcon = new ImageIcon(wStarURL);
            ImageIcon yStarIcon = new ImageIcon(yStarURL);
            Image wStarImage = wStarIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            Image yStarImage = yStarIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);

            UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(this);
            String userEmail = parentFrame.getCurrentUserEmail();

            if (userEmail == null) {
                starButton.setIcon(new ImageIcon(wStarImage));
                starButton.addActionListener(
                        e -> JOptionPane.showMessageDialog(this, "Please login to add comics to your wishlist"));
            } else {
                UserLibraryController controller = new UserLibraryController();
                int userId = getUserId(userEmail);
                boolean isComicInWishlist = controller.isComicInWishlist(userId, comic.getId());

                starButton.setIcon(isComicInWishlist ? new ImageIcon(yStarImage) : new ImageIcon(wStarImage));

                starButton.addActionListener(e -> {
                    boolean currentState = controller.isComicInWishlist(userId, comic.getId());
                    if (currentState) {
                        if (controller.resetComicOwnership(userId, comic.getId())) {
                            refreshValidationButtons();
                            starButton.setIcon(new ImageIcon(wStarImage));
                        }
                    } else {
                        if (!controller.isComicInLibrary(userId, comic.getId())) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Please add the comic to your library first before adding it to your wishlist",
                                    "Not in Library",
                                    JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        if (controller.updateComicOwnership(userId, comic.getId(), 0)) {
                            refreshValidationButtons();
                            starButton.setIcon(new ImageIcon(yStarImage));
                        }
                    }
                });
            }
        }
    }

    private void setupValidationButton(JButton validaButton, Comic comic) {
        validationButtons.put(validaButton, comic);
        URL blackValidationURL = getClass().getClassLoader().getResource("notOwned.png");
        URL greenValidationURL = getClass().getClassLoader().getResource("Owned.png");

        if (blackValidationURL != null && greenValidationURL != null) {
            ImageIcon blackValidationIcon = new ImageIcon(blackValidationURL);
            ImageIcon greenValidationIcon = new ImageIcon(greenValidationURL);
            Image blackValidationImage = blackValidationIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            Image greenValidationImage = greenValidationIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);

            UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(this);
            String userEmail = parentFrame.getCurrentUserEmail();

            if (userEmail == null) {
                validaButton.setIcon(new ImageIcon(blackValidationImage));
                validaButton.addActionListener(
                        e -> JOptionPane.showMessageDialog(this, "Please login to add comics to your wishlist"));
            } else {
                UserLibraryController controller = new UserLibraryController();
                int userId = getUserId(userEmail);
                boolean isComicOwned = controller.isComicOwned(userId, comic.getId());
                // Set initial icon based on wishlist status
                validaButton.setIcon(
                        isComicOwned ? new ImageIcon(greenValidationImage) : new ImageIcon(blackValidationImage));

                // Add click handler
                validaButton.addActionListener(e -> {
                    boolean currentState = controller.isComicOwned(userId, comic.getId());
                    if (currentState) {
                        if (controller.resetComicOwnership(userId, comic.getId())) {
                            refreshStarButtons();
                            validaButton.setIcon(new ImageIcon(blackValidationImage));
                        }
                    } else {
                        if (controller.updateComicOwnership(userId, comic.getId(), 1)) {
                            refreshStarButtons();
                            validaButton.setIcon(new ImageIcon(greenValidationImage));
                        }
                    }
                });
            }
        }

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
        // Reload current search results to refresh heart buttons
        if (currentSearchText != null && currentSearchType != null) {
            loadResults();
        }
    }

    public void refreshValidationButtons() {
        // Refresh all star buttons
        for (Map.Entry<JButton, Comic> entry : validationButtons.entrySet()) {
            setupValidationButton(entry.getKey(), entry.getValue());
        }
        // Also reload current search results
        if (currentSearchText != null && currentSearchType != null) {
            loadResults();
        }
    }

    public void refreshStarButtons() {
        // Refresh all star buttons
        for (Map.Entry<JButton, Comic> entry : starButtons.entrySet()) {
            setupStarButton(entry.getKey(), entry.getValue());
        }
        // Also reload current search results
        if (currentSearchText != null && currentSearchType != null) {
            loadResults();
        }
    }
}