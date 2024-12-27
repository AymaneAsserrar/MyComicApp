package com.project.ui;

import com.project.controller.RecommendationController;
import com.project.model.Comic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.project.util.DatabaseUtil;
import com.project.model.UserLibraryController;

public class RecommendationPanel extends JPanel {
    private static final long serialVersionUID = 2561771664627867791L;
    private JPanel comicsGridPanel;
    private RecommendationController recommendationController;
    private int currentOffset = 0;
    private static final int PAGE_SIZE = 10;
    private JLabel libraryMessageLabel;

    public RecommendationPanel() {
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

        JScrollPane scrollPane = new JScrollPane(comicsGridPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
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
                BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0, 0, 0, 30))
        ));

        // Heart-shaped button
        JButton likeButton = new JButton();
        likeButton.setFocusPainted(false);
        likeButton.setBorderPainted(false);
        likeButton.setContentAreaFilled(false);
        likeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        setupHeartButton(likeButton, comic);

        JPanel likeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        likeButtonPanel.setOpaque(false);
        likeButtonPanel.add(likeButton);

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
                Comic detailedComic = recommendationController.getComicDetails(comic.getId());
                if (detailedComic != null) {
                    UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(RecommendationPanel.this);
                    parentFrame.displayComicDetails(detailedComic, "Recommendation");
                }
            }
        });

        comicPanel.add(coverLabel, BorderLayout.CENTER);
        comicPanel.add(titleLabel, BorderLayout.SOUTH);
        comicPanel.add(likeButtonPanel, BorderLayout.NORTH);
        comicsGridPanel.add(comicPanel, comicsGridPanel.getComponentCount() - 1);
    }

    private void setupHeartButton(JButton likeButton, Comic comic) {
        URL whiteHeartURL = getClass().getClassLoader().getResource("white.png");
        URL redHeartURL = getClass().getClassLoader().getResource("heart.png");
        
        // Si les images ne sont pas trouvées, utiliser un texte par défaut
        if (whiteHeartURL == null || redHeartURL == null) {
            likeButton.setText("♡"); // Utiliser un caractère coeur comme fallback
            likeButton.setFont(new Font("Arial", Font.PLAIN, 20));
        } else {
            try {
                ImageIcon whiteHeartIcon = new ImageIcon(whiteHeartURL);
                ImageIcon redHeartIcon = new ImageIcon(redHeartURL);
                Image whiteHeartImage = whiteHeartIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                Image redHeartImage = redHeartIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                
                // Le reste du code...
                UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(this);
                String userEmail = parentFrame != null ? parentFrame.getCurrentUserEmail() : null;
                
                if (userEmail == null) {
                    likeButton.setIcon(new ImageIcon(whiteHeartImage));
                    likeButton.addActionListener(e -> {
                        JOptionPane.showMessageDialog(this, "Please login to add comics to your library");
                    });
                } else {
                    UserLibraryController controller = new UserLibraryController();
                    boolean isInLibrary = controller.isComicInLibrary(getUserId(userEmail), comic.getId());
                    likeButton.setIcon(isInLibrary ? new ImageIcon(redHeartImage) : new ImageIcon(whiteHeartImage));
                    
                    likeButton.addActionListener(e -> {
                        if (!isInLibrary && controller.addComicToLibrary(getUserId(userEmail), comic)) {
                            likeButton.setIcon(new ImageIcon(redHeartImage));
                        }
                    });
                }
            } catch (Exception e) {
                likeButton.setText("♡");
                likeButton.setFont(new Font("Arial", Font.PLAIN, 20));
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

    public void updateLibraryMessage(boolean isSignedIn) {
        if (isSignedIn) {
            libraryMessageLabel.setText("This section will be implemented soon");
        } else {
            libraryMessageLabel.setText("You are not signed in yet");
        }
    }
}