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
import com.project.ui.LoadingDialog;

import kotlin.Pair;

import com.project.model.UserLibraryController;

public class RecommendationPanel extends JPanel implements UiMain.UserLoginListener {
    private static final long serialVersionUID = 2561771664627867791L;
    private JPanel comicsGridPanel;
    private RecommendationController recommendationController;
    private int currentOffset = 0;
    private static final int PAGE_SIZE = 10;
    private static final int RECOMMENDATION_PAGE_SIZE = 5;
    private JLabel libraryMessageLabel;
    private Map<JButton, Comic> heartButtons;
    private Map<JButton, Comic> starButtons;
    private Map<JButton, Comic> readButtons;
    private UiMain parentFrame;
    private JPanel recommendedGridPanel;
    private int recommendedOffset = 0;
    private JPanel becauseYouReadPanel;
    private JLabel becauseYouReadLabel;
    private int becauseYouReadOffset = 0;
    private boolean recommendationsLoaded = false;

    public RecommendationPanel(UiMain parent) {
        this.parentFrame = parent;
        this.heartButtons = new HashMap<>();
        this.starButtons = new HashMap<>();
        this.readButtons = new HashMap<>();
        parentFrame.addLoginListener(this);

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Main scrollable content panel.
        JPanel mainScrollContent = new JPanel();
        mainScrollContent.setLayout(new BoxLayout(mainScrollContent, BoxLayout.Y_AXIS));
        mainScrollContent.setBackground(new Color(240, 240, 240));

        // Create a scroll pane for the entire content
        JScrollPane mainScrollPane = new JScrollPane(mainScrollContent);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Initialize message label
        this.libraryMessageLabel = new JLabel("");
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
        addLoadMoreButton(comicsGridPanel, getUserId(parentFrame.getCurrentUserEmail()),
                () -> loadPopularComics(currentOffset));

        // Add panels to main scroll content
        mainScrollContent.add(popularPanel);
        mainScrollContent.add(Box.createVerticalStrut(20)); // Spacing between sections
        initializeRecommendationSection(mainScrollContent);

        // Initialize "Because You Read" section
        initializeBecauseYouReadSection(mainScrollContent);

        // Add main scroll pane to panel
        add(mainScrollPane, BorderLayout.CENTER);
        add(libraryMessageLabel, BorderLayout.SOUTH);

        // Initialize controllers and load content
        recommendationController = new RecommendationController();
        loadPopularComics(currentOffset);
    }

    public void loadPopularComics(int offset) {
        LoadingDialog loadingDialog = new LoadingDialog(parentFrame, "Loading comics...");
        SwingWorker<List<Comic>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Comic> doInBackground() throws Exception {
                return recommendationController.getPopularComics(offset, PAGE_SIZE);
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    List<Comic> comics = get();
                    for (Comic comic : comics) {
                        addComicPanel(comic, comicsGridPanel);
                    }
                    currentOffset += comics.size();
                    revalidate();
                    repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
        loadingDialog.setVisible(true);
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
                if (targetPanel == recommendedGridPanel) {
                    // Fetch issue details and then volume details for recommended comics
                    Comic detailedComic = recommendationController.getComicDetails(comic.getId());
                    if (detailedComic != null) {
                        UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(RecommendationPanel.this);
                        parentFrame.displayComicDetails(detailedComic, "Recommendation");
                    }
                } else if (targetPanel == becauseYouReadPanel) {
                    // Fetch volume details for "Because You Read" comics
                    Comic detailedComic = recommendationController.getComicDetails(comic.getId());
                    if (detailedComic != null) {
                        UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(RecommendationPanel.this);
                        parentFrame.displayComicDetails(detailedComic, "Recommendation");
                    }
                } else {
                    // Directly fetch volume details for popular comics
                    Comic detailedComic = recommendationController.getComicDetails(comic.getId());
                    if (detailedComic != null) {
                        UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(RecommendationPanel.this);
                        parentFrame.displayComicDetails(detailedComic, "Recommendation");
                    }
                }
            }
        });

        // Add components to the comic panel
        comicPanel.add(buttonPanel, BorderLayout.NORTH);
        comicPanel.add(coverLabel, BorderLayout.CENTER);
        comicPanel.add(titleLabel, BorderLayout.SOUTH);
        targetPanel.add(comicPanel, targetPanel.getComponentCount() - 1);
    }

    private void initializeBecauseYouReadSection(JPanel mainScrollContent) {
        // Initialize "Because You Read" section
        JPanel becauseYouReadSection = new JPanel(new BorderLayout());
        becauseYouReadSection.setBackground(Color.WHITE);
        becauseYouReadSection.setBorder(new EmptyBorder(10, 10, 10, 10));

        becauseYouReadLabel = new JLabel("Because You Read", SwingConstants.CENTER);
        becauseYouReadLabel.setFont(new Font("Arial", Font.BOLD, 30));
        becauseYouReadLabel.setForeground(Color.BLACK);
        becauseYouReadLabel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(100, 30));
        refreshButton.setFocusPainted(false);
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBorder(new EmptyBorder(5, 5, 5, 5));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadBecauseYouReadComics());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(becauseYouReadLabel);
        headerPanel.add(Box.createVerticalStrut(10)); // Add some space between the label and the button
        headerPanel.add(refreshButton);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        becauseYouReadLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        becauseYouReadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        becauseYouReadPanel.setBackground(Color.WHITE);

        JScrollPane becauseYouReadScrollPane = new JScrollPane(becauseYouReadPanel,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        becauseYouReadScrollPane.getHorizontalScrollBar().setUnitIncrement(48);
        becauseYouReadScrollPane.setPreferredSize(new Dimension(1000, 400));
        String userEmail = parentFrame.getCurrentUserEmail();
        if (userEmail != null && !userEmail.isEmpty()) {
            addLoadMoreButton(becauseYouReadPanel, getUserId(userEmail),
                    RecommendationPanel.this::loadMoreBecauseYouReadComics);
        }
        becauseYouReadSection.add(headerPanel, BorderLayout.NORTH);
        becauseYouReadSection.add(becauseYouReadScrollPane, BorderLayout.CENTER);

        mainScrollContent.add(becauseYouReadSection);

    }

    private void initializeRecommendationSection(JPanel mainScrollContent) {
        JPanel recommendationsPanel = new JPanel(new BorderLayout());
        recommendationsPanel.setBackground(Color.WHITE);
        recommendationsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE);

        JLabel recommendationsLabel = new JLabel("Recommended For You", SwingConstants.CENTER);
        recommendationsLabel.setFont(new Font("Arial", Font.BOLD, 30));
        recommendationsLabel.setForeground(Color.BLACK);
        recommendationsLabel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton refreshButton = new JButton("reload");
        refreshButton.setPreferredSize(new Dimension(42, 42));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 9));
        refreshButton.setBorder(new EmptyBorder(5, 5, 5, 5));

        refreshButton.addActionListener(e -> {
            recommendedGridPanel.removeAll();
            recommendedOffset = 0;
            recommendationsLoaded = true;
            updateRecommendations();
        });

        headerPanel.add(recommendationsLabel);
        headerPanel.add(refreshButton);
        recommendationsPanel.add(headerPanel, BorderLayout.NORTH);

        recommendedGridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        recommendedGridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        recommendedGridPanel.setBackground(Color.WHITE);

        JScrollPane recommendedScrollPane = new JScrollPane(recommendedGridPanel);
        recommendedScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        recommendedScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        recommendedScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        recommendedScrollPane.setPreferredSize(new Dimension(1000, 400));

        recommendationsPanel.add(recommendedScrollPane, BorderLayout.CENTER);

        mainScrollContent.add(Box.createVerticalStrut(20));
        mainScrollContent.add(recommendationsPanel);
    }

    private void loadBecauseYouReadComics() {
        LoadingDialog loadingDialog = new LoadingDialog(parentFrame, "Loading recommendations...");
        SwingWorker<Pair<String, List<Comic>>, Void> worker = new SwingWorker<>() {
            @Override
            protected Pair<String, List<Comic>> doInBackground() throws Exception {
                String userEmail = parentFrame.getCurrentUserEmail();
                if (userEmail == null || userEmail.isEmpty()) {
                    return null;
                }
                int userId = getUserId(userEmail);
                return recommendationController.getComicsFromSameVolume(userId, 0, RECOMMENDATION_PAGE_SIZE);
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    Pair<String, List<Comic>> result = get();
                    if (result != null) {
                        String selectedComicName = result.component1();
                        List<Comic> becauseYouReadComics = result.component2();

                        becauseYouReadPanel.removeAll();
                        if (selectedComicName != null) {
                            becauseYouReadLabel.setText("Because You Read: " + selectedComicName);
                        } else {
                            becauseYouReadLabel.setText("Because You Read");
                        }

                        for (Comic comic : becauseYouReadComics) {
                            addComicPanel(comic, becauseYouReadPanel);
                        }

                        // Add load more button after loading comics
                        String userEmail = parentFrame.getCurrentUserEmail();
                        if (userEmail != null && !userEmail.isEmpty()) {
                            addLoadMoreButton(becauseYouReadPanel, getUserId(userEmail),
                                    () -> loadMoreBecauseYouReadComics());
                        }

                        becauseYouReadPanel.revalidate();
                        becauseYouReadPanel.repaint();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
        loadingDialog.setVisible(true);
    }

    public void updateRecommendations() {
        if (!recommendationsLoaded) {
            return;
        }
        
        LoadingDialog loadingDialog = new LoadingDialog(parentFrame, "Loading recommendations...");
        SwingWorker<List<Comic>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Comic> doInBackground() throws Exception {
                String userEmail = parentFrame.getCurrentUserEmail();
                if (userEmail == null || userEmail.isEmpty()) {
                    return null;
                }
                int userId = getUserId(userEmail);
                return recommendationController.getRecommendedComics(userId, 0, RECOMMENDATION_PAGE_SIZE);
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    List<Comic> recommendations = get();
                    recommendedGridPanel.removeAll();
                    recommendedOffset = 0; // Reset offset when updating

                    if (recommendations != null && !recommendations.isEmpty()) {
                        for (Comic comic : recommendations) {
                            addComicPanel(comic, recommendedGridPanel);
                        }
                        Runnable doNotifyProgressChange = () -> {
                        };
                        addLoadMoreButton(recommendedGridPanel, getUserId(parentFrame.getCurrentUserEmail()),
                                doNotifyProgressChange);
                    } else {
                        libraryMessageLabel.setText("Add a comic to your library to get recommendations");
                    }

                    recommendedGridPanel.revalidate();
                    recommendedGridPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
        loadingDialog.setVisible(true);
    }

    private boolean hasNoComicsWithGenres(int userId) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String genreQuery = "SELECT COUNT(*) FROM comic c " +
                    "JOIN biblio ul ON c.id_comic = ul.id_comic " +
                    "WHERE ul.id_biblio = ? AND added = 1 AND c.genres IS NOT NULL AND c.genres != ''";

            PreparedStatement stmt = conn.prepareStatement(genreQuery);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void addLoadMoreButton(JPanel panel, int userId, Runnable loadMoreAction) {
        JButton loadMoreButton = new JButton("→");
        loadMoreButton.setPreferredSize(new Dimension(50, 300));
        loadMoreButton.setFont(new Font("Arial", Font.BOLD, 35));
        loadMoreButton.setForeground(Color.BLACK);
        loadMoreButton.setFocusPainted(false);
        loadMoreButton.setBorderPainted(false);
        loadMoreButton.setContentAreaFilled(false);
        loadMoreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loadMoreButton.addActionListener(e -> loadMoreAction.run());
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

    // Add a new method to load more comics in the "Because You Read" section
    private void loadMoreBecauseYouReadComics() {
        int userId = getUserId(parentFrame.getCurrentUserEmail());
        List<Comic> newComics = recommendationController.getComicsFromSameVolume(userId, becauseYouReadOffset, 12)
                .getSecond();
        if (!newComics.isEmpty()) {
            becauseYouReadOffset += newComics.size();
            for (Comic comic : newComics) {
                addComicPanel(comic, becauseYouReadPanel);
            }
            becauseYouReadPanel.revalidate();
            becauseYouReadPanel.repaint();
        }
    }

    private void loadMorePopularComics() {
        int userId = getUserId(parentFrame.getCurrentUserEmail());
        List<Comic> newComics = recommendationController.getPopularComics(currentOffset, PAGE_SIZE);
        if (!newComics.isEmpty()) {
            currentOffset += newComics.size();
            for (Comic comic : newComics) {
                addComicPanel(comic, comicsGridPanel);
            }
            comicsGridPanel.revalidate();
            comicsGridPanel.repaint();
        }
    }

    public void refreshHeartButtons() {
        // Refresh hearts in both popular and recommended sections
        for (Map.Entry<JButton, Comic> entry : heartButtons.entrySet()) {
            setupHeartButton(entry.getKey(), entry.getValue());
        }
        comicsGridPanel.revalidate();
        comicsGridPanel.repaint();
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
                                UiMain parentFrame = (UiMain) SwingUtilities
                                        .getWindowAncestor(RecommendationPanel.this);
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
                                UiMain parentFrame = (UiMain) SwingUtilities
                                        .getWindowAncestor(RecommendationPanel.this);
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
                                UiMain parentFrame = (UiMain) SwingUtilities
                                        .getWindowAncestor(RecommendationPanel.this);
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
            starButtons.forEach(this::setupStarButton);
        });
    }

    private void setupReadButton(JButton readButton, Comic comic) {
        readButtons.put(readButton, comic); // Use readButtons map
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

            String userEmail = parentFrame.getCurrentUserEmail();

            // Remove existing action listeners
            ActionListener[] listeners = readButton.getActionListeners();
            for (ActionListener listener : listeners) {
                readButton.removeActionListener(listener);
            }

            if (userEmail == null || userEmail.isEmpty()) {
                readButton.setIcon(new ImageIcon(notReadingImage));
                readButton.addActionListener(
                        e -> JOptionPane.showMessageDialog(this, "Please login to manage reading status"));
                return;
            }

            int userId = getUserId(userEmail);
            UserLibraryController controller = new UserLibraryController();
            String currentStatus = controller.getReadStatus(userId, comic.getId());

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

            readButton.addActionListener(e -> {
                String status = controller.getReadStatus(userId, comic.getId());
                boolean updated = false;

                switch (status) {
                    case "notreading":
                        updated = controller.updateReadStatus(userId, comic.getId(), 0);
                        if (updated)
                            readButton.setIcon(new ImageIcon(readingImage));
                        break;
                    case "reading":
                        updated = controller.updateReadStatus(userId, comic.getId(), 1);
                        if (updated)
                            readButton.setIcon(new ImageIcon(finishedImage));
                        break;
                    case "finished":
                        updated = controller.resetReadStatus(userId, comic.getId());
                        if (updated)
                            readButton.setIcon(new ImageIcon(notReadingImage));
                        break;
                }

                if (updated) {
                    SwingUtilities.invokeLater(() -> {
                        if (parentFrame != null) {
                            parentFrame.refreshAllPanels();
                        }
                    });
                }
            });
        }
    }

    public void refreshReadButtons() {
        SwingUtilities.invokeLater(() -> {
            readButtons.forEach(this::setupReadButton); // Use readButtons map
        });
    }

    @Override
    public void onUserLogin(String email) {
        currentOffset = 0;
        recommendedOffset = 0;
        becauseYouReadOffset = 0;
        recommendationsLoaded = false;

        becauseYouReadPanel.removeAll();
        recommendedGridPanel.removeAll();
        comicsGridPanel.removeAll();

        loadPopularComics(0);
        addLoadMoreButton(comicsGridPanel, getUserId(email), this::loadMorePopularComics);
        
        refreshHeartButtons();
        refreshStarButtons();
        refreshReadButtons();
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
        }
    }

    // Add this to handle sign out
    public void onUserLogout() {
        // Reset all offsets
        currentOffset = 0;
        recommendedOffset = 0;
        becauseYouReadOffset = 0;
        recommendationsLoaded = false;

        // Clear panels
        recommendedGridPanel.removeAll();
        comicsGridPanel.removeAll();
        becauseYouReadPanel.removeAll();

        // Update UI
        libraryMessageLabel.setText("You are not signed in yet");
        becauseYouReadLabel.setText("Because You Read");

        // Refresh and repaint
        recommendedGridPanel.revalidate();
        recommendedGridPanel.repaint();
        comicsGridPanel.revalidate();
        comicsGridPanel.repaint();
        becauseYouReadPanel.revalidate();
        becauseYouReadPanel.repaint();

        // Reset buttons
        refreshHeartButtons();
        refreshStarButtons();
        refreshReadButtons();

        // Reload popular comics
        loadPopularComics(0);
    }

    public void updateWishlistMessage(boolean isSignedIn) {
        if (isSignedIn) {
            libraryMessageLabel.setText("Add a comic to your library to get recommendations");
        } else {
            libraryMessageLabel.setText("You are not signed in yet");
        }
    }

    private void refreshComicsGrid() {
        SwingUtilities.invokeLater(() -> {
            // Reload content
            comicsGridPanel.removeAll();
            loadPopularComics(0); // Reset offset and reload
        });
    }

}