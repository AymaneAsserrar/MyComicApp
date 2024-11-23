package com.project.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import com.project.model.Comic;

public class UiMain extends JFrame {
    private static final long serialVersionUID = 2008701708169261499L;

    private RecommendationPanel recommendationPanel;
    private SearchPanel searchPanel;
    private SearchResultsPanel searchResultsPanel;
    private JLabel homeLabel;
    private CardLayout cardLayout;
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
            Image logoImage = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH); // Agrandir le logo pour une meilleure visibilité
            homeLabel = new JLabel(new ImageIcon(logoImage));
            ImageIcon appLogoIcon = new ImageIcon(logoURL);
            Image appLogoImage = appLogoIcon.getImage();
            setIconImage(appLogoImage); 
        } else {
            homeLabel = new JLabel("Home");
        }
        homeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showRecommendationPage();
            }
        });

        // Top panel with the Home logo and search bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(homeLabel, BorderLayout.WEST);
        topPanel.add(searchPanel = new SearchPanel(), BorderLayout.EAST); // Search Panel à droite

        // Set margin around top panel elements
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        add(topPanel, BorderLayout.NORTH);

        // Initialize the panels
        recommendationPanel = new RecommendationPanel();
        searchResultsPanel = new SearchResultsPanel();

        // Container for switching between panels
        containerPanel = new JPanel(cardLayout);
        containerPanel.add(recommendationPanel, "Recommendation");
        containerPanel.add(searchResultsPanel, "SearchResults");

        add(containerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public void displaySearchResults(String searchText) {
        searchResultsPanel.displayResults(searchText);
        cardLayout.show(containerPanel, "SearchResults");
    }

    private void showRecommendationPage() {
        cardLayout.show(containerPanel, "Recommendation");
        containerPanel.revalidate();
        containerPanel.repaint();
    }

}
