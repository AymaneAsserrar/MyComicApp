package com.project.ui;

import com.project.model.Comic;
import com.project.model.UserLibraryController;
import com.project.util.DatabaseUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WishlistPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JPanel comicsGridPanel;
    private UserLibraryController libraryController;
    private JLabel messageLabel;
    private Map<JButton, Comic> starButtons = new HashMap<>();
    private String currentUserEmail;

    public WishlistPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Initialize components
        libraryController = new UserLibraryController();

        // Message label for login status
        messageLabel = new JLabel("Please login to view your wishlist", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Comics grid setup
        comicsGridPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        comicsGridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        comicsGridPanel.setBackground(Color.WHITE);

        // Scroll pane for comics
        JScrollPane scrollPane = new JScrollPane(comicsGridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(messageLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateWishlist(String userEmail) {
        this.currentUserEmail = userEmail;
        if (userEmail == null || userEmail.isEmpty()) {
            messageLabel.setText("Please login to view your wishlist");
            comicsGridPanel.removeAll();
            comicsGridPanel.revalidate();
            comicsGridPanel.repaint();
            return;
        }

        messageLabel.setText("My Wishlist");
        loadUserWishlist(userEmail);
    }

    private void loadUserWishlist(String userEmail) {
        comicsGridPanel.removeAll();
        List<Comic> userComics = getUserComics(userEmail);

        for (Comic comic : userComics) {
            addComicPanel(comic);
        }

        comicsGridPanel.revalidate();
        comicsGridPanel.repaint();
    }

    private List<Comic> getUserComics(String userEmail) {
        List<Comic> comics = new ArrayList<>();
        String query = "SELECT c.* FROM comic c " +
                "JOIN biblio b ON c.id_comic = b.id_comic " +
                "JOIN user u ON b.id_biblio = u.id " +
                "WHERE u.email = ? AND (b.possede = 0 OR b.possede = 1)";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Comic comic = new Comic();
                comic.setId(rs.getInt("id_comic"));
                comic.setName(rs.getString("name"));
                comic.setDescription(rs.getString("description"));
                comic.setCoverImageUrl(rs.getString("image"));
                comics.add(comic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comics;
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
    
            if (currentUserEmail == null || currentUserEmail.isEmpty()) {
                starButton.setIcon(new ImageIcon(wStarImage));
                starButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Please login to manage wishlist"));
                return;
            }
    
            int userId = getUserId(currentUserEmail);
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
                    UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(WishlistPanel.this);
                    if (parentFrame != null) {
                        parentFrame.refreshAllPanels();
                    }
                }
            });
        }
    }
    
    public void refreshStarButtons() {
        SwingUtilities.invokeLater(() -> {
            starButtons.forEach(this::setupStarButton);
        });
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
        starButtons.put(starButton, comic);

        // Panel to hold star button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(starButton);

        // Cover image with click handler
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

        // Make comic panel clickable for details
        comicPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comicPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(WishlistPanel.this);
                parentFrame.displayComicDetails(comic, "Wishlist");
            }
        });

        // Title label
        JLabel titleLabel = new JLabel(comic.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        comicPanel.add(buttonPanel, BorderLayout.NORTH);
        comicPanel.add(coverLabel, BorderLayout.CENTER);
        comicPanel.add(titleLabel, BorderLayout.SOUTH);
        comicsGridPanel.add(comicPanel);
    }
}
