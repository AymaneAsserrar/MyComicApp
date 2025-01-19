package com.project.ui;

import com.project.controller.SearchController;
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

public class ReadListPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JPanel comicsGridPanel;
    private UserLibraryController libraryController;
    private JLabel messageLabel;
    private Map<JButton, Comic> readButtons = new HashMap<>();
    private String currentUserEmail;

    public ReadListPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        libraryController = new UserLibraryController();

        messageLabel = new JLabel("Please login to view your reading list", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));

        comicsGridPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        comicsGridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        comicsGridPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(comicsGridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(messageLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadUserReadlist(String userEmail) {
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
        String query = "SELECT DISTINCT c.* FROM comic c " +
                "JOIN biblio b ON c.id_comic = b.id_comic " +
                "JOIN user u ON b.id_biblio = u.id " +
                "WHERE u.email = ? AND (b.status = 0 OR b.status = 1)";

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

    private void setupReadButton(JButton readButton, Comic comic) {
        readButtons.put(readButton, comic);
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

            // Remove existing action listeners
            ActionListener[] listeners = readButton.getActionListeners();
            for (ActionListener listener : listeners) {
                readButton.removeActionListener(listener);
            }

            // Get user email from parent frame
            UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(this);
            String userEmail = parentFrame != null ? parentFrame.getCurrentUserEmail() : currentUserEmail;

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
                    loadUserReadlist(currentUserEmail); // Reload full list instead of just refreshing button
                }
            });
        }
    }

    public void refreshReadButtons() {
        SwingUtilities.invokeLater(() -> {
            readButtons.forEach(this::setupReadButton);
        });
    }

    public void updateReadlist(String userEmail) {
        System.out.println("Updating readlist for user: " + userEmail); // Debug log
        this.currentUserEmail = userEmail;

        if (userEmail == null || userEmail.isEmpty()) {
            messageLabel.setText("Please login to view your reading list");
            comicsGridPanel.removeAll();
            comicsGridPanel.revalidate();
            comicsGridPanel.repaint();
            return;
        }

        messageLabel.setText("My Reading List");
        loadUserReadlist(userEmail);
        refreshReadButtons();
    }

    private void addComicPanel(Comic comic) {
        JPanel comicPanel = new JPanel(new BorderLayout());
        comicPanel.setPreferredSize(new Dimension(200, 300));
        comicPanel.setBackground(Color.WHITE);

        comicPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0, 0, 0, 30))));

        JButton readButton = new JButton();
        readButton.setFocusPainted(false);
        readButton.setBorderPainted(false);
        readButton.setContentAreaFilled(false);
        readButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setupReadButton(readButton, comic);
        readButtons.put(readButton, comic);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(readButton);

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

        comicPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comicPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Comic detailedComic = getDetailedComic(comic);
                UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(ReadListPanel.this);
                parentFrame.displayComicDetails(detailedComic, "ReadList");
            }
        });

        JLabel titleLabel = new JLabel(comic.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        comicPanel.add(buttonPanel, BorderLayout.NORTH);
        comicPanel.add(coverLabel, BorderLayout.CENTER);
        comicPanel.add(titleLabel, BorderLayout.SOUTH);
        comicsGridPanel.add(comicPanel);
    }

    private Comic getDetailedComic(Comic basicComic) {
        SearchController searchController = new SearchController();
        Comic detailedComic = searchController.getComicDetails(basicComic.getId());
        if (detailedComic != null) {
            return detailedComic;
        }
        return basicComic; // Fallback to basic comic if details fetch fails
    }
}