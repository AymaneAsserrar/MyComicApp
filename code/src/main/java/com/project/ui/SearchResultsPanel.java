package com.project.ui;

import com.project.controller.SearchController;
import com.project.model.Comic;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class SearchResultsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private SearchController searchController;
    private JPanel resultsGridPanel;

    public SearchResultsPanel() {
        setLayout(new BorderLayout());

        // Title for the search results section
        JLabel searchResultsLabel = new JLabel("Comics Found", SwingConstants.CENTER);
        searchResultsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(searchResultsLabel, BorderLayout.NORTH);

        // Panel for displaying the results in a grid
        resultsGridPanel = new JPanel(new GridLayout(4, 3, 5, 5)); // Ensure it maintains 4 rows and 3 columns
        resultsGridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(resultsGridPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize the search controller
        searchController = new SearchController();
    }

    public void displayResults(String searchText) {
        // Clear the previous results
        resultsGridPanel.removeAll();

        // Get the search results from the API via the SearchController
        List<Comic> searchResults = searchController.searchComicsByTitle(searchText);

        int resultsCount = searchResults.size();
        int limit = 12;

        // Add comics to the grid
        for (int i = 0; i < resultsCount; i++) {
            Comic comic = searchResults.get(i);

            JPanel comicPanel = new JPanel(new BorderLayout());
            comicPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel coverLabel;
            try {
                URL imageURL = new URL(comic.getCoverImageUrl());
                ImageIcon icon = new ImageIcon(imageURL);
                Image img = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                coverLabel = new JLabel(new ImageIcon(img));
            } catch (Exception e) {
                coverLabel = new JLabel("Image unavailable");
            }

            JLabel titleLabel = new JLabel(comic.getName(), SwingConstants.CENTER);
            comicPanel.add(coverLabel, BorderLayout.CENTER);
            comicPanel.add(titleLabel, BorderLayout.SOUTH);

            resultsGridPanel.add(comicPanel);
        }

        // Fill empty cells with blank panels to maintain consistent layout
        for (int i = resultsCount; i < limit; i++) {
            JPanel emptyPanel = new JPanel();
            resultsGridPanel.add(emptyPanel);
        }

        // Repaint the panel after updating the results
        revalidate();
        repaint();
    }
}
