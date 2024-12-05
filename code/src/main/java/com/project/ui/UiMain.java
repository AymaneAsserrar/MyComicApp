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
    private AuthenticationPanel authenticationPanel;
    private JLabel homeLabel;
    private JLabel authLabel;
    private CardLayout cardLayout;
    private JPanel containerPanel;
    private ComicDetailsPanel comicDetailsPanel;

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

        // Load the authentication logo from resources
        URL AuthLogoURL = getClass().getClassLoader().getResource("auth_logo.png");
        if (AuthLogoURL != null) {
            ImageIcon authIcon = new ImageIcon(AuthLogoURL);
            Image authImage = authIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH); // Agrandir le logo pour une meilleure visibilité
            authLabel = new JLabel(new ImageIcon(authImage));
        } else {
        	authLabel = new JLabel("Auth");
        }

        authLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        authLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showAuthenticationPage(); // call method to display the authentication panel
            }
        });
        // Top panel with the Home logo, search bar, and authentication logo
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(homeLabel, BorderLayout.WEST); // add home logo to the left
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // create panel for search and auth logo
        rightPanel.add(searchPanel = new SearchPanel());
        rightPanel.add(authLabel);
        
        topPanel.add(rightPanel, BorderLayout.EAST); // add panel containing auth and search logos
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

      
        add(topPanel, BorderLayout.NORTH); // add the top panel to the frame

        // Initialize the panels
        recommendationPanel = new RecommendationPanel();
        searchResultsPanel = new SearchResultsPanel();
        authenticationPanel = new AuthenticationPanel();
        comicDetailsPanel = new ComicDetailsPanel();

        // Container for switching between panels
        containerPanel = new JPanel(cardLayout);
        containerPanel.add(recommendationPanel, "Recommendation");
        containerPanel.add(searchResultsPanel, "SearchResults");
        containerPanel.add(authenticationPanel,"Authentication");
        containerPanel.add(comicDetailsPanel, "ComicDetails");

        add(containerPanel, BorderLayout.CENTER);

        comicDetailsPanel.addBackButtonListener(e -> showPreviousPanel());

        setVisible(true);
    }

    public void displaySearchResults(String searchText, String searchType) {
        searchResultsPanel.displayResults(searchText, searchType);
        cardLayout.show(containerPanel, "SearchResults");
    }

    public void displayComicDetails(Comic comic, String sourcePanel) {
        comicDetailsPanel.setPreviousPanel(sourcePanel);
        comicDetailsPanel.displayComicDetails(comic);
        cardLayout.show(containerPanel, "ComicDetails");
    }

    private void showRecommendationPage() {
        cardLayout.show(containerPanel, "Recommendation");
        containerPanel.revalidate();
        containerPanel.repaint();
    }
    
    private void showAuthenticationPage() {
    	cardLayout.show(containerPanel, "Authentication");
        containerPanel.revalidate();
        containerPanel.repaint();
        }

    private void showPreviousPanel() {
        String previousPanel = comicDetailsPanel.getPreviousPanel();
        cardLayout.show(containerPanel, previousPanel);
    }
}
