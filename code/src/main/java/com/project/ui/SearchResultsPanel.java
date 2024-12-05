package com.project.ui;

import com.project.controller.SearchController;
import com.project.util.ScrollUtil;
import com.project.model.Comic;
import com.project.model.Hero;

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
        if (isLoading || !hasMoreResults || currentSearchText == null || currentSearchText.isEmpty()) {
            return;
        }

        isLoading = true;
        currentPage = offset / PAGE_SIZE;

        if ("Comic".equals(searchType)) {
            List<Comic> searchResults = searchController.searchComicsByTitle(currentSearchText);
            processComics(searchResults);
        } else if ("Character".equals(searchType)) {
            List<Hero> searchResults = searchController.searchCharactersByName(currentSearchText);
            processHeroes(searchResults);
        } else {
            hasMoreResults = false;
        }

        isLoading = false;
    }

    private void processComics(List<Comic> comics) {
        if (comics.isEmpty()) {
            hasMoreResults = false;
        } else {
            for (Comic comic : comics) {
                if (!displayedComicIds.contains(comic.getId())) {
                    addComicPanel(comic);
                    displayedComicIds.add(comic.getId());
                }
            }
            revalidate();
            repaint();
        }
    }

    private void processHeroes(List<Hero> heroes) {
        if (heroes.isEmpty()) {
            hasMoreResults = false;
        } else {
            for (Hero hero : heroes) {
                if (!displayedComicIds.contains(hero.getId())) { // Assuming Hero has getId()
                    addHeroPanel(hero);
                    displayedComicIds.add(hero.getId());
                }
            }
            revalidate();
            repaint();
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

    private void addHeroPanel(Hero hero) {
        JPanel comicPanel = new JPanel(new BorderLayout());
        comicPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
        resultsGridPanel.add(comicPanel);
    }
}