package com.project.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class UiMain extends JFrame {
    private static final long serialVersionUID = 2008701708169261499L;

    private RecommendationPanel recommendationPanel; // Panel for recommendations
    private SearchPanel searchPanel; // Panel for search bar
    private SearchResultsPanel searchResultsPanel; // Panel for search results
    private JLabel homeLabel; // Label for home icon
    private CardLayout cardLayout; // to handle panel switching
    private JPanel containerPanel;


    public UiMain() {
        setTitle("My Comic App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);

        cardLayout = new CardLayout();
        setLayout(new BorderLayout());

        // Load the home logo from resources
        URL logoURL = getClass().getClassLoader().getResource("homeLogo.png");
        if (logoURL != null) {
            ImageIcon logoIcon = new ImageIcon(logoURL);
            Image logoImage = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            homeLabel = new JLabel(new ImageIcon(logoImage));
        } else {
            homeLabel = new JLabel("Home");
        }
        homeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showRecommendationPage(); // Show recommendation panel when clicked
            }
        });

        // Create a panel to hold the "Home" icon and search bar using BorderLayout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(homeLabel, BorderLayout.WEST); // Add the "Home" icon to the left (WEST)

        // Initialize the search panel and add to the center
        searchPanel = new SearchPanel();
        topPanel.add(searchPanel, BorderLayout.CENTER); // Search Panel will manage search field and icon
        topPanel.setPreferredSize(new Dimension(getWidth(),40)); // Fixer la hauteur de la barre superieure
        
        // Add the topPanel to the NORTH of the window
        add(topPanel, BorderLayout.NORTH);

        // Initialize the recommendation panel
        recommendationPanel = new RecommendationPanel();
        recommendationPanel.setVisible(true); // Ensure the panel is visible

        // Initialize the search results panel
        searchResultsPanel = new SearchResultsPanel();

        // Create a container panel with CardLayout to manage different panels
        containerPanel = new JPanel(cardLayout);
        containerPanel.add(recommendationPanel, "Recommendation");
        containerPanel.add(searchResultsPanel, "SearchResults");

        // Add the containerPanel to the CENTER of the window
        add(containerPanel, BorderLayout.CENTER);

        // Set up search functionality
        setupSearchFunctionality();

        setVisible(true);
    }

    private void setupSearchFunctionality() {
        searchPanel.getSearchButton().addActionListener(e -> performSearch());
        searchPanel.getSearchField().addActionListener(e -> performSearch());
    }

    private void performSearch() {
        String searchText = searchPanel.getSearchField().getText().trim();
        boolean hasResults = searchResultsPanel.performSearch(searchText);

        // Toggle panels using CardLayout
        if (hasResults) {
            cardLayout.show(containerPanel, "SearchResults");
        } else {
            cardLayout.show(containerPanel, "Recommendation");
        }

        // Force layout refresh to ensure the UI updates
        containerPanel.revalidate();
        containerPanel.repaint();
    }
    
    private void showRecommendationPage() {
        cardLayout.show(containerPanel, "Recommendation");

        // Force layout refresh after switching
        containerPanel.revalidate();
        containerPanel.repaint();
    }


}
