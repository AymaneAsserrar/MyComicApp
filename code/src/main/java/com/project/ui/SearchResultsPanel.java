package com.project.ui;

import com.project.controller.SearchController;
import com.project.model.Comic;
import com.project.util.ScrollUtil;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class SearchResultsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 12;
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMoreResults = true;
    private SearchController searchController;
    private JPanel resultsGridPanel;
    private String currentSearchText;

    public SearchResultsPanel() {
        setLayout(new BorderLayout());

        JLabel searchResultsLabel = new JLabel("Comics Found", SwingConstants.CENTER);
        searchResultsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(searchResultsLabel, BorderLayout.NORTH);

        resultsGridPanel = new JPanel(new GridLayout(0, 3, 5, 5));
        resultsGridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = ScrollUtil.createInfiniteScrollPane(resultsGridPanel, 
            offset -> loadMoreResults(offset));

        add(scrollPane, BorderLayout.CENTER);
        searchController = new SearchController();
    }

    public void displayResults(String searchText) {
        currentSearchText = searchText;
        resultsGridPanel.removeAll();
        loadMoreResults(0);
    }

    private void loadMoreResults(int offset) {
        if (isLoading || !hasMoreResults) {
            return;
        }

        if (currentSearchText != null && !currentSearchText.isEmpty()) {
            isLoading = true;
            
            // Calculate the page number based on offset
            currentPage = offset / PAGE_SIZE;
            
            List<Comic> searchResults = searchController.searchComicsByTitle(
                currentSearchText, 
                currentPage, 
                PAGE_SIZE
            );
            
            if (searchResults.isEmpty()) {
                hasMoreResults = false;
            } else {
                for (Comic comic : searchResults) {
                    addComicPanel(comic);
                }
                revalidate();
                repaint();
            }
            
            isLoading = false;
        }
    }

    private void addComicPanel(Comic comic) {
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
}
