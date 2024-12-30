package com.project.ui;

import com.project.controller.RecommendationController;
import com.project.model.Comic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.project.util.DatabaseUtil;
import com.project.model.UserLibraryController;

public class RecommendationPanel extends JPanel implements UiMain.UserLoginListener {
    private static final long serialVersionUID = 2561771664627867791L;
    private JPanel comicsGridPanel;
    private RecommendationController recommendationController;
    private int currentOffset = 0;
    private static final int PAGE_SIZE = 10;
    private static final int RECOMMENDATION_PAGE_SIZE = 12;
    private JLabel libraryMessageLabel;
    private Map<JButton, Comic> heartButtons;
    private Map<JButton, Comic> starButtons;
    private Map<JButton, Comic> validationButtons;
    private UiMain parentFrame;
    private JPanel recommendedGridPanel;
    private int recommendedOffset = 0;

    public RecommendationPanel(UiMain parent) {
        this.parentFrame = parent;
        this.heartButtons = new HashMap<>();
        this.starButtons = new HashMap<>();
        this.validationButtons = new HashMap<>();
        parentFrame.addLoginListener(this);

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Main scrollable content panel
        JPanel mainScrollContent = new JPanel();
        mainScrollContent.setLayout(new BoxLayout(mainScrollContent, BoxLayout.Y_AXIS));
        mainScrollContent.setBackground(new Color(240, 240, 240));

        // Create a scroll pane for the entire content
        JScrollPane mainScrollPane = new JScrollPane(mainScrollContent);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Initialize message label
        this.libraryMessageLabel = new JLabel("Your library message here");
        libraryMessageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        libraryMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Popular Comics Section
        JPanel popularPanel = new JPanel(new BorderLayout());
        popularPanel.setBackground(new Color(255, 255, 255));
        popularPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel popularComicsLabel = new JLabel("Popular Comics", SwingConstants.CENTER);
        popularComicsLabel.setFont(new Font("Arial", Font.BOLD, 30));
        popularComicsLabel.setForeground(Color.BLACK); // Set title color to black
        popularComicsLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        popularPanel.add(popularComicsLabel, BorderLayout.NORTH);

        comicsGridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        comicsGridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        comicsGridPanel.setBackground(new Color(255, 255, 255)); // White background for comics grid

        JScrollPane scrollPane = new JScrollPane(comicsGridPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(48); // Increase scroll speed
        scrollPane.setPreferredSize(new Dimension(1000, 400)); // Reduced height
        popularPanel.add(scrollPane, BorderLayout.CENTER);

        recommendationController = new RecommendationController();
        loadPopularComics(currentOffset);

        // Add load more button
        JButton loadMoreButton = new JButton("→");
        loadMoreButton.setPreferredSize(new Dimension(50, 300));
        loadMoreButton.setFont(new Font("Arial", Font.BOLD, 35));
        loadMoreButton.setForeground(Color.BLACK); // Set title color to black
        loadMoreButton.setFocusPainted(false);
        loadMoreButton.setBorderPainted(false);
        loadMoreButton.setContentAreaFilled(false);
        loadMoreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loadMoreButton.addActionListener(e -> loadPopularComics(currentOffset));
        comicsGridPanel.add(loadMoreButton);

        // Add panels to main scroll content
        mainScrollContent.add(popularPanel);
        mainScrollContent.add(Box.createVerticalStrut(20)); // Spacing between sections
        initializeRecommendationSection(mainScrollContent);

        // Add main scroll pane to panel
        add(mainScrollPane, BorderLayout.CENTER);
        add(libraryMessageLabel, BorderLayout.SOUTH);

        // Initialize controllers and load content
        recommendationController = new RecommendationController();
        loadPopularComics(currentOffset);
        updateRecommendations();
    }

    private void loadPopularComics(int offset) {
        List<Comic> recommendationList = recommendationController.getPopularComics(offset, PAGE_SIZE);
        for (Comic comic : recommendationList) {
            addComicPanel(comic, comicsGridPanel);
        }
        currentOffset += recommendationList.size();
        revalidate();
        repaint();
    }

    private void addComicPanel(Comic comic, JPanel targetPanel) {
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

        // Panel to hold buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(starButton);
        buttonPanel.add(likeButton);

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
        }

        // Comic title
        JLabel titleLabel = new JLabel(comic.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        // Add click listener to open comic details
        comicPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comicPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Comic detailedComic = recommendationController.getComicDetails(comic.getId());
                if (detailedComic != null) {
                    UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(RecommendationPanel.this);
                    parentFrame.displayComicDetails(detailedComic, "Recommendation");
                }
            }
        });

        // Add components to the comic panel
        comicPanel.add(buttonPanel, BorderLayout.NORTH);
        comicPanel.add(coverLabel, BorderLayout.CENTER);
        comicPanel.add(titleLabel, BorderLayout.SOUTH);
        comicPanel.add(likeButtonPanel, BorderLayout.NORTH);
        targetPanel.add(comicPanel, targetPanel.getComponentCount() - 1);
        

    }

    private void initializeRecommendationSection(JPanel mainScrollContent) {
        // Recommendations Section
        JPanel recommendationsPanel = new JPanel(new BorderLayout());
        recommendationsPanel.setBackground(Color.WHITE);
        recommendationsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel recommendationsLabel = new JLabel("Recommended For You", SwingConstants.CENTER);
        recommendationsLabel.setFont(new Font("Arial", Font.BOLD, 30));
        recommendationsLabel.setForeground(Color.BLACK);
        recommendationsLabel.setBorder(new EmptyBorder(20, 0, 20, 0));

        recommendedGridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        recommendedGridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        recommendedGridPanel.setBackground(Color.WHITE);

        // Create horizontal scroll pane for recommendations
        JScrollPane recommendedScrollPane = new JScrollPane(recommendedGridPanel);
        recommendedScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        recommendedScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        recommendedScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        recommendedScrollPane.setPreferredSize(new Dimension(1000, 400));

        recommendationsPanel.add(recommendationsLabel, BorderLayout.NORTH);
        recommendationsPanel.add(libraryMessageLabel, BorderLayout.SOUTH);
        recommendationsPanel.add(recommendedScrollPane, BorderLayout.CENTER);

        mainScrollContent.add(Box.createVerticalStrut(20));
        mainScrollContent.add(recommendationsPanel);
    }

    private void updateRecommendations() {
        recommendedGridPanel.removeAll();
        String userEmail = parentFrame.getCurrentUserEmail();

        if (userEmail == null || userEmail.isEmpty()) {
            libraryMessageLabel.setText("You are not signed in yet");
            recommendedGridPanel.removeAll();
            recommendedGridPanel.revalidate();
            recommendedGridPanel.repaint();
            return;
        }

        int userId = getUserId(userEmail);
        List<Comic> recommendations = recommendationController.getRecommendedComics(userId, 0,
                RECOMMENDATION_PAGE_SIZE);

        if (recommendations.isEmpty()) {
            libraryMessageLabel.setText("Please add a comic to your library to get recommendations");
            return;
        }

        libraryMessageLabel.setText("");

        for (Comic comic : recommendations) {
            addComicPanel(comic, recommendedGridPanel);
        }

        // Add load more button if there are recommendations
        if (!recommendations.isEmpty()) {
            addLoadMoreButton(recommendedGridPanel, userId);
        }

        recommendedGridPanel.revalidate();
        recommendedGridPanel.repaint();
    }

    private void addLoadMoreButton(JPanel panel, int userId) {
        JButton loadMoreButton = new JButton("→");
        loadMoreButton.setPreferredSize(new Dimension(50, 300));
        loadMoreButton.setFont(new Font("Arial", Font.BOLD, 35));
        loadMoreButton.setForeground(Color.BLACK);
        loadMoreButton.setFocusPainted(false);
        loadMoreButton.setBorderPainted(false);
        loadMoreButton.setContentAreaFilled(false);
        loadMoreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loadMoreButton.addActionListener(e -> loadMoreRecommendations(userId));
        panel.add(loadMoreButton);
    }

    private void loadMoreRecommendations(int userId) {
        List<Comic> newRecommendations = recommendationController.getRecommendedComics(userId,
                recommendedOffset, 12);
        if (!newRecommendations.isEmpty()) {
            recommendedOffset += newRecommendations.size();
            for (Comic comic : newRecommendations) {
                addComicPanel(comic, recommendedGridPanel);
            }
            recommendedGridPanel.revalidate();
            recommendedGridPanel.repaint();
        }
    }

    public void refreshHeartButtons() {
        // Refresh hearts in both popular and recommended sections
        for (Map.Entry<JButton, Comic> entry : heartButtons.entrySet()) {
            setupHeartButton(entry.getKey(), entry.getValue());
        }
        comicsGridPanel.revalidate();
        comicsGridPanel.repaint();
        updateRecommendations();
    }

    private void setupHeartButton(JButton likeButton, Comic comic) {
        heartButtons.put(likeButton, comic);
        URL whiteHeartURL = getClass().getClassLoader().getResource("white.png");
        URL redHeartURL = getClass().getClassLoader().getResource("heart.png");

        if (whiteHeartURL != null && redHeartURL != null) {
            ImageIcon whiteHeartIcon = new ImageIcon(whiteHeartURL);
            ImageIcon redHeartIcon = new ImageIcon(redHeartURL);
            Image whiteHeartImage = whiteHeartIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            Image redHeartImage = redHeartIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);

            String userEmail = parentFrame.getCurrentUserEmail();

            // Remove existing action listeners safely
            ActionListener[] listeners = likeButton.getActionListeners();
            for (ActionListener listener : listeners) {
                likeButton.removeActionListener(listener);
            }

            if (userEmail == null || userEmail.isEmpty()) {
                likeButton.setIcon(new ImageIcon(whiteHeartImage));
                likeButton.addActionListener(
                        e -> JOptionPane.showMessageDialog(this, "Please login to add comics to your library"));
                return;
            }

            int userId = getUserId(userEmail);
            UserLibraryController controller = new UserLibraryController();
            boolean isInLibrary = controller.isComicInLibrary(userId, comic.getId());

            // Set initial icon based on library status
            likeButton.setIcon(isInLibrary ? new ImageIcon(redHeartImage) : new ImageIcon(whiteHeartImage));

            // Add click handler
            likeButton.addActionListener(e -> {
                boolean currentState = controller.isComicInLibrary(userId, comic.getId());

                if (currentState) {
                    if (controller.removeComicFromLibrary(userId, comic.getId())) {
                        likeButton.setIcon(new ImageIcon(whiteHeartImage));
                    }
                } else {
                    if (controller.addComicToLibrary(userId, comic)) {
                        likeButton.setIcon(new ImageIcon(redHeartImage));
                    }
                }
                parentFrame.refreshAllPanels(); // Force a UI refresh in RecommendationPanel
            });
        }
    }

    private void setupStarButton(JButton starButton, Comic comic) {
        starButtons.put(starButton, comic);
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
    
            String userEmail = parentFrame.getCurrentUserEmail();
    
            // Remove existing action listeners safely
            ActionListener[] listeners = starButton.getActionListeners();
            for (ActionListener listener : listeners) {
                starButton.removeActionListener(listener);
            }
    
            if (userEmail == null || userEmail.isEmpty()) {
                starButton.setIcon(new ImageIcon(wStarImage));
                starButton.addActionListener(
                        e -> JOptionPane.showMessageDialog(this, "Please login to add comics to your wishlist"));
                return;
            }
    
            int userId = getUserId(userEmail);
            UserLibraryController controller = new UserLibraryController();
            String currentStatus = controller.getComicStatus(userId, comic.getId());
    
            // Set initial icon based on current status
            switch (currentStatus) {
                case "owned":
                    starButton.setIcon(new ImageIcon(ownedImage));
                    break;
                case "ystar":
                    starButton.setIcon(new ImageIcon(yStarImage));
                    break;
                default:
                    starButton.setIcon(new ImageIcon(wStarImage));
                    break;
            }
    
            // Add click handler with single update
            starButton.addActionListener(e -> {
                String status = controller.getComicStatus(userId, comic.getId());
                boolean updated = false;
    
                switch (status) {
                    case "owned":
                        updated = controller.updateComicOwnership(userId, comic.getId(), null);
                        if (updated) {
                            starButton.setIcon(new ImageIcon(wStarImage));
                            SwingUtilities.invokeLater(() -> {
                                UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(RecommendationPanel.this);
                                if (parentFrame != null) {
                                    parentFrame.refreshAllPanels();
                                }
                            });
                        }
                        break;
                    case "ystar": 
                        updated = controller.updateComicOwnership(userId, comic.getId(), 1);
                        if (updated) {
                            starButton.setIcon(new ImageIcon(ownedImage));
                            SwingUtilities.invokeLater(() -> {
                                UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(RecommendationPanel.this);
                                if (parentFrame != null) {
                                    parentFrame.refreshAllPanels();
                                }
                            });
                        }
                        break;
                    case "wstar":
                    default:
                        updated = controller.updateComicOwnership(userId, comic.getId(), 0);
                        if (updated) {
                            starButton.setIcon(new ImageIcon(yStarImage));
                            SwingUtilities.invokeLater(() -> {
                                UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(RecommendationPanel.this);
                                if (parentFrame != null) {
                                    parentFrame.refreshAllPanels();
                                }
                            });
                        }
                        break;
                }
            });
        }
    }
    
    public void refreshStarButtons() {
        SwingUtilities.invokeLater(() -> {
            for (Map.Entry<JButton, Comic> entry : starButtons.entrySet()) {
                setupStarButton(entry.getKey(), entry.getValue());
            }
        });
    }

    public void refreshHeartButtons() {
        SwingUtilities.invokeLater(() -> {
            for (Map.Entry<JButton, Comic> entry : heartButtons.entrySet()) {
                setupHeartButton(entry.getKey(), entry.getValue());
            }
        });
    }

    @Override
    public void onUserLogin(String email) {
        refreshStarButtons();
        refreshHeartButtons();
    }

    private int getUserId(String email) {
        if (email == null || email.isEmpty()) {
            System.out.println("Email is null or empty"); // Debug log
            return -1;
        }

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT id FROM user WHERE email = ?")) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("Found user ID: " + id); // Debug log
                return id;
            }
            System.out.println("No user found for email: " + email); // Debug log
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Database error: " + e.getMessage()); // Debug log
        }
        return -1;
    }

    public void updateLibraryMessage(boolean isSignedIn) {
        if (!isSignedIn) {
            // Clear recommendations when signed out
            recommendedOffset = 0;
            recommendedGridPanel.removeAll();
            libraryMessageLabel.setText("You are not signed in yet");
            recommendedGridPanel.revalidate();
            recommendedGridPanel.repaint();
        } else {
            updateRecommendations();
        }
    }

    // Add this to handle sign out
    public void onUserLogout() {
        recommendedOffset = 0;
        recommendedGridPanel.removeAll();
        libraryMessageLabel.setText("You are not signed in yet");
        recommendedGridPanel.revalidate();
        recommendedGridPanel.repaint();
        refreshHeartButtons();
    }
}