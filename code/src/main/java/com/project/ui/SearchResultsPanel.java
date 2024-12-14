package com.project.ui;

import com.project.controller.SearchController;
import com.project.util.ScrollUtil;
import com.project.model.Comic;
import com.project.model.Hero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchResultsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private SearchController searchController;
    private JPanel resultsGridPanel;
    private String currentSearchText;
    private String currentSearchType;
    private JLabel searchResultsLabel;

    public SearchResultsPanel() {
        setLayout(new BorderLayout());

        searchResultsLabel = new JLabel("Comics Found", SwingConstants.CENTER);
        searchResultsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(searchResultsLabel, BorderLayout.NORTH);

        resultsGridPanel = new JPanel(new GridLayout(0, 3, 5, 5));
        resultsGridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(resultsGridPanel);
        add(scrollPane, BorderLayout.CENTER);

        searchController = new SearchController();
    }

    public void displayResults(String searchText, String searchType) {
        currentSearchText = searchText;
        currentSearchType = searchType;
        loadResults();
    }

    private void loadResults() {
        SearchController.SearchResult searchResult;
        if ("Comic".equals(currentSearchType)) {
            searchResult = searchController.searchComicsByTitle(currentSearchText, 0, 10);
            searchResultsLabel.setText("Comics Found");
        } else if ("Character".equals(currentSearchType)) {
            searchResult = searchController.searchCharactersByName(currentSearchText);
            searchResultsLabel.setText("Characters Found");
        } else {
            searchResult = new SearchController.SearchResult(new ArrayList<>(), 0);
        }

        List<?> searchResults = searchResult.getResults();

        resultsGridPanel.removeAll();
        if (searchResults.isEmpty()) {
            searchResultsLabel.setText("No results found");
        } else {
            if ("Comic".equals(currentSearchType)) {
                for (Object result : searchResults) {
                    addComicPanel((Comic) result);
                }
            } else if ("Character".equals(currentSearchType)) {
                for (Object result : searchResults) {
                    addHeroPanel((Hero) result);
                }
            }
        }

        resultsGridPanel.revalidate();
        resultsGridPanel.repaint();
        revalidate();
        repaint();
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
        
        comicPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comicPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Comic detailedComic = searchController.getComicDetails(comic.getId());
                if (detailedComic != null) {
                    UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(SearchResultsPanel.this);
                    parentFrame.displayComicDetails(detailedComic, "SearchResults");
                }
            }
        });
        
        resultsGridPanel.add(comicPanel);
    }

    private void addHeroPanel(Hero hero) {
        JPanel comicPanel = new JPanel(new BorderLayout());
        comicPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel heroPanel = new JPanel(new BorderLayout());
        heroPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel coverLabel;
        try {
            URL imageURL = new URL(hero.getImageUrl());
            ImageIcon icon = new ImageIcon(imageURL);
            Image img = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
            coverLabel = new JLabel(new ImageIcon(img));
        } catch (Exception e) {
            coverLabel = new JLabel("Image unavailable");
        }

        JLabel titleLabel = new JLabel(hero.getName(), SwingConstants.CENTER);
        comicPanel.add(coverLabel, BorderLayout.CENTER);
        comicPanel.add(titleLabel, BorderLayout.SOUTH);
        // Détecte les clics sur ce panneau pour récupérer les détails du personnage
        heroPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        heroPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            // Appelle le contrôleur pour récupérer les détails du personnage
            searchController.fetchCharacterProfile(hero.getId());
        }
    });

        resultsGridPanel.add(comicPanel);
    }
}