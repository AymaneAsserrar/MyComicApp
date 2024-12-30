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
    private JLabel libraryMessageLabel;
    private Map<JButton, Comic> heartButtons;
    private Map<JButton, Comic> starButtons;
    private Map<JButton, Comic> validationButtons;
    private UiMain parentFrame;

    public RecommendationPanel(UiMain parent) {
        this.parentFrame = parent;
        this.heartButtons = new HashMap<>();
        this.starButtons = new HashMap<>();
        this.validationButtons = new HashMap<>();
        parentFrame.addLoginListener(this);

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Main content panel to hold both sections
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 240, 240));

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
        scrollPane.setPreferredSize(new Dimension(1000, 500));
        popularPanel.add(scrollPane, BorderLayout.CENTER);

        recommendationController = new RecommendationController();
        loadComics(currentOffset);

        // Add load more button
        JButton loadMoreButton = new JButton("→");
        loadMoreButton.setPreferredSize(new Dimension(50, 300));
        loadMoreButton.setFont(new Font("Arial", Font.BOLD, 35));
        loadMoreButton.setForeground(Color.BLACK); // Set title color to black
        loadMoreButton.setFocusPainted(false);
        loadMoreButton.setBorderPainted(false);
        loadMoreButton.setContentAreaFilled(false);
        loadMoreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loadMoreButton.addActionListener(e -> loadComics(currentOffset));
        comicsGridPanel.add(loadMoreButton);

        // Your Library Section
        JPanel libraryPanel = new JPanel(new BorderLayout());
        libraryPanel.setBackground(new Color(255, 255, 255));
        libraryPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel libraryTitle = new JLabel("Recommendations", SwingConstants.CENTER);
        libraryTitle.setFont(new Font("Arial", Font.BOLD, 24));
        libraryTitle.setBorder(new EmptyBorder(20, 0, 20, 0));
        libraryPanel.add(libraryTitle, BorderLayout.NORTH);

        libraryMessageLabel = new JLabel("You are not signed in yet", SwingConstants.CENTER);
        libraryMessageLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        libraryMessageLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        libraryPanel.add(libraryMessageLabel, BorderLayout.CENTER);

        // Adjust height if needed
        libraryPanel.setPreferredSize(new Dimension(1000, 300));

        contentPanel.add(popularPanel);
        contentPanel.add(libraryPanel);

        // Add the content panel to the main panel
        add(contentPanel, BorderLayout.CENTER);
    }

    private void loadComics(int offset) {
        List<Comic> recommendationList = recommendationController.getPopularComics(offset, PAGE_SIZE);
        for (Comic comic : recommendationList) {
            addComicPanel(comic);
        }
        currentOffset += recommendationList.size();
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

        // Add comic panel to the grid
        comicsGridPanel.add(comicPanel, comicsGridPanel.getComponentCount() - 1);
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
        if (isSignedIn) {
            libraryMessageLabel.setText("This section will be implemented soon");
        } else {
            libraryMessageLabel.setText("You are not signed in yet");
        }
    }

    public void updateWishlistMessage(boolean isSignedIn) {
        if (isSignedIn) {
            libraryMessageLabel.setText("This section will be implemented soon");
        } else {
            libraryMessageLabel.setText("You are not signed in yet");
        }
    }
}