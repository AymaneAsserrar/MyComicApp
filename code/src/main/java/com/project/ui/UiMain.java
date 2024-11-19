package com.project.ui;

import javax.swing.*;
import java.awt.*;

public class UiMain extends JFrame {
    private static final long serialVersionUID = 2008701708169261499L;

    private RecommendationPanel recommendationPanel; // Panel for recommendations
    private SearchPanel searchPanel; // Panel for search bar and results


    public UiMain() {
        setTitle("My Comic App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Initialize and add the search panel
        searchPanel = new SearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        // Initialize and add the recommendation panel
        recommendationPanel = new RecommendationPanel();
        JScrollPane recommendationScrollPane = new JScrollPane(recommendationPanel); // Make it scrollable
        add(recommendationScrollPane, BorderLayout.CENTER);

        // Set up the search functionality to hide recommendations
        setupSearchFunctionality(recommendationScrollPane);

        setVisible(true);
    }

    private void setupSearchFunctionality(JScrollPane recommendationScrollPane) {
        searchPanel.getSearchField().addActionListener(e -> performSearch(recommendationScrollPane));
        searchPanel.getSearchButton().addActionListener(e -> performSearch(recommendationScrollPane));
    }

    private void performSearch(JScrollPane recommendationScrollPane) {
        String searchText = searchPanel.getSearchField().getText().trim();
        boolean hasResults = searchPanel.performSearch(searchText);

        // Show or hide the recommendations panel
        recommendationScrollPane.setVisible(!hasResults);

        // Refresh the layout to reflect visibility changes
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UiMain::new);
    }
}
