package com.project.ui;

import com.project.controller.SearchController;
import com.project.model.Comic;
import com.project.util.ScrollUtil;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchResultsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 12;
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMoreResults = true;
    private SearchController searchController;
    private JPanel resultsGridPanel;
    private String currentSearchText;
    private Set<Integer> displayedComicIds;
    public SearchResultsPanel() {
        setLayout(new BorderLayout());
    
        JLabel searchResultsLabel = new JLabel("Comics Found", SwingConstants.CENTER);
        searchResultsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(searchResultsLabel, BorderLayout.NORTH);
    
        resultsGridPanel = new JPanel(new GridLayout(0, 3, 5, 5));
        resultsGridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
        JScrollPane scrollPane = ScrollUtil.createInfiniteScrollPane(resultsGridPanel, 
            offset -> loadMoreResults(offset, currentSearchText));
    
        add(scrollPane, BorderLayout.CENTER);
        searchController = new SearchController();
        displayedComicIds = new HashSet<>();
    }

public void displayResults(String searchText, String searchType) {
    currentSearchText = searchText;
    currentPage = 0;
    hasMoreResults = true;
    isLoading = false;
    displayedComicIds.clear();
    resultsGridPanel.removeAll();
    loadMoreResults(0, searchType);
}
private void loadMoreResults(int offset, String searchType) {
    if (isLoading || !hasMoreResults) {
        return;
    }

    if (currentSearchText != null && !currentSearchText.isEmpty()) {
        isLoading = true;

        // Calculate the page number based on offset
        currentPage = offset / PAGE_SIZE;

        List<Comic> searchResults;
        if ("Comic".equals(searchType)) {
            searchResults = searchController.searchComicsByTitle(currentSearchText);
        } else if ("Character".equals(searchType)) {
            searchResults = searchController.searchCharactersByName(currentSearchText);
        } else {
            searchResults = new ArrayList<>();
        }

        if (searchResults.isEmpty()) {
            hasMoreResults = false;
        } else {
            for (Comic comic : searchResults) {
                if (!displayedComicIds.contains(comic.getId())) {
                    addComicPanel(comic, searchType);
                    displayedComicIds.add(comic.getId());
                }
            }
            revalidate();
            repaint();
        }

        isLoading = false;
    }
}

    private void addComicPanel(Comic comic, String searchType) {
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