package com.project.ui;

import javax.swing.*;
import java.awt.*;

public class UiMain extends JFrame {
    private static final long serialVersionUID = 2008701708169261499L;

    private RecommendationPanel recommendationPanel; // Panel for recommendations
    private SearchPanel searchPanel; // Panel for search bar
    private SearchResultsPanel searchResultsPanel; // Panel for search results
    private JButton homeButton; // Button for going back to recommendations
    private CardLayout cardLayout; // to handle panel switching

    public UiMain() {
        setTitle("My Comic App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);

        cardLayout = new CardLayout();
        setLayout(cardLayout); // for panel switching

        // Initialize and add the home button to the top left corner
        homeButton = new JButton("Home");
        homeButton.setPreferredSize(new Dimension(80, 40)); // Set button size to prevent stretching
        homeButton.addActionListener(e -> showRecommendationPage()); // Action to go back to recommendations

        // Create a panel to hold the "Home" button and search bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Use FlowLayout for alignment
        topPanel.add(homeButton); // Add the "Home" button to the panel

        // Initialize and add the search panel to the top panel
        searchPanel = new SearchPanel();
        topPanel.add(searchPanel); // Add search bar next to the "Home" button

        // Add the topPanel to the NORTH of the window
        add(topPanel, BorderLayout.NORTH);

        // Initialize the recommendation panel and explicitly set it visible
        recommendationPanel = new RecommendationPanel();
        recommendationPanel.setVisible(true); // Ensure the panel is visible

        // Initialize the search results panel
        searchResultsPanel = new SearchResultsPanel();

        // Create a container panel with CardLayout to manage different panels
        JPanel containerPanel = new JPanel(cardLayout);
        containerPanel.add(recommendationPanel, "Recommendation");
        containerPanel.add(searchResultsPanel, "SearchResults");

        // Add the containerPanel to the CENTER of the window
        add(containerPanel, BorderLayout.CENTER);

        // Set up search functionality
        setupSearchFunctionality();

        setVisible(true);
    }

    private void setupSearchFunctionality() {
        searchPanel.getSearchField().addActionListener(e -> performSearch());
        searchPanel.getSearchButton().addActionListener(e -> performSearch());
    }

    private void performSearch() {
        String searchText = searchPanel.getSearchField().getText().trim();
        System.out.println("Searching for: " + searchText);  // Debug line to check search input
        boolean hasResults = searchResultsPanel.performSearch(searchText);

        // Debug: Show whether search results are found
        System.out.println("Search Results Found: " + hasResults);

        // Toggle panels using CardLayout
        if (hasResults) {
            System.out.println("Switching to SearchResults Panel");
            cardLayout.show(getContentPane(), "SearchResults");
        } else {
            System.out.println("Switching to Recommendation Panel");
            cardLayout.show(getContentPane(), "Recommendation");
        }

        // Force layout refresh to ensure the UI updates
        revalidate();
        repaint();
    }

    private void showRecommendationPage() {
        System.out.println("Switching to Recommendation Panel...");
        cardLayout.show(getContentPane(), "Recommendation");

        // Force layout refresh after switching
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UiMain::new);
    }
}
