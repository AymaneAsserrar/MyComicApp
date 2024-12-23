package com.project.ui;

import com.project.model.Comic;
import com.project.model.UserLibraryController;
import com.project.util.DatabaseUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LibraryPanel extends JPanel {
    private JPanel comicsGridPanel;
    private UserLibraryController libraryController;
    private JLabel messageLabel;
    private String currentUserEmail;

    public LibraryPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Initialize components
        libraryController = new UserLibraryController();
        
        // Message label for login status
        messageLabel = new JLabel("Please login to view your library", SwingConstants.CENTER);
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

    public void updateLibrary(String userEmail) {
        this.currentUserEmail = userEmail;
        if (userEmail == null || userEmail.isEmpty()) {
            messageLabel.setText("Please login to view your library");
            comicsGridPanel.removeAll();
            comicsGridPanel.revalidate();
            comicsGridPanel.repaint();
            return;
        }

        messageLabel.setText("My Library");
        loadUserLibrary(userEmail);
    }

    private void loadUserLibrary(String userEmail) {
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
                      "WHERE u.email = ? AND b.added = 1";

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

    private void addComicPanel(Comic comic) {
        JPanel comicPanel = new JPanel(new BorderLayout());
        comicPanel.setPreferredSize(new Dimension(200, 300));
        comicPanel.setBackground(Color.WHITE);
        
        // Add shadow effect
        comicPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(0, 0, 0, 30))
        ));

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

        // Title label
        JLabel titleLabel = new JLabel(comic.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        comicPanel.add(coverLabel, BorderLayout.CENTER);
        comicPanel.add(titleLabel, BorderLayout.SOUTH);
        comicsGridPanel.add(comicPanel);
    }
}