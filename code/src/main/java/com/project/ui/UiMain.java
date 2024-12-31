package com.project.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.project.model.Comic;
import com.project.model.Hero;
import com.project.util.CustomSearchField;
import com.project.ui.ReadListPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UiMain extends JFrame {
    private static final long serialVersionUID = 2008701708169261499L;
    private String currentUserEmail;
    private List<UserLoginListener> loginListeners;

    private RecommendationPanel recommendationPanel;
    private SearchResultsPanel searchResultsPanel;
    private ComicDetailsPanel comicDetailsPanel;
    private JLabel homeLabel;
    private CardLayout cardLayout;
    private JPanel containerPanel;
    private CustomSearchField customSearchField;
    private JLabel profileLabel;
    private JButton profileButton;
    private JPanel searchProfilePanel;
    private LibraryPanel libraryPanel;
    private WishlistPanel wishlistPanel;
    private ReadListPanel readListPanel;

    public UiMain() {
        // Set FlatLaf theme
        FlatDarkLaf.setup();

        setTitle("My Comic App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 850); // Adjust window height

        loginListeners = new ArrayList<>();

        cardLayout = new CardLayout();
        setLayout(new BorderLayout());

        // Load the home logo from resources
        URL logoURL = getClass().getClassLoader().getResource("homeLogo.png");
        if (logoURL != null) {
            ImageIcon logoIcon = new ImageIcon(logoURL);
            Image logoImage = logoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH); // Slightly bigger logo
            homeLabel = new JLabel(new ImageIcon(logoImage));
            ImageIcon appLogoIcon = new ImageIcon(logoURL);
            Image appLogoImage = appLogoIcon.getImage();
            setIconImage(appLogoImage);
        } else {
            homeLabel = new JLabel("Home");
        }

        homeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                showRecommendationPage();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                homeLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                homeLabel.setBorder(null);
            }
        });

        // Initialize the header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 50, 50));

        // Navigation panel for readlist and wishlist
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navPanel.setBackground(new Color(50, 50, 50));
        JLabel libraryLabel = createHeaderLabel("Library");
        JLabel readlistLabel = createHeaderLabel("Readlist");
        JLabel wishlistLabel = createHeaderLabel("Wishlist");
        navPanel.add(homeLabel);
        navPanel.add(libraryLabel);
        navPanel.add(readlistLabel);
        navPanel.add(wishlistLabel);

        // Custom search field
        customSearchField = new CustomSearchField();

        // Profile Icon Button
        URL profileURL = getClass().getClassLoader().getResource("profile.png");
        if (profileURL != null) {
            ImageIcon profileIconImage = new ImageIcon(profileURL);
            Image profileImage = profileIconImage.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            profileButton = new JButton(new ImageIcon(profileImage));
            profileButton.setPreferredSize(new Dimension(40, 40));
            profileButton.setFocusPainted(false);
            profileButton.setBorderPainted(false);
            profileButton.setContentAreaFilled(false);
            profileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            profileButton.addActionListener(e -> new LoginForm(this).setVisible(true));
        } else {
            System.err.println("Profile image not found.");
            profileButton = new JButton("Profile");
        }

        profileLabel = new JLabel();
        profileLabel.setForeground(Color.WHITE);

        // Panel to hold search field and profile button
        searchProfilePanel = new JPanel();
        searchProfilePanel.setLayout(new BoxLayout(searchProfilePanel, BoxLayout.X_AXIS));
        searchProfilePanel.setBackground(new Color(50, 50, 50));
        searchProfilePanel.add(customSearchField);
        searchProfilePanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add space between components
        searchProfilePanel.add(profileButton);

        // Add components to header panel
        headerPanel.add(navPanel, BorderLayout.WEST);
        headerPanel.add(searchProfilePanel, BorderLayout.EAST);

        // Top panel to hold the header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(50, 50, 50));
        topPanel.add(headerPanel, BorderLayout.CENTER);

        // Set margin around top panel elements
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        add(topPanel, BorderLayout.NORTH);

        // Initialize the panels
        recommendationPanel = new RecommendationPanel(this);
        searchResultsPanel = new SearchResultsPanel();
        comicDetailsPanel = new ComicDetailsPanel();

        // Container for switching between panels
        containerPanel = new JPanel(cardLayout);
        containerPanel.add(recommendationPanel, "Recommendation");
        containerPanel.add(searchResultsPanel, "SearchResults");
        containerPanel.add(comicDetailsPanel, "ComicDetails");
        libraryPanel = new LibraryPanel();
        wishlistPanel = new WishlistPanel();
        readListPanel = new ReadListPanel();
        containerPanel.add(libraryPanel, "Library");
        containerPanel.add(wishlistPanel, "Wishlist");
        containerPanel.add(readListPanel, "Readlist");

        add(containerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16)); // Adjust font size
        label.setForeground(Color.WHITE);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setText("<html><u>" + text + "</u></html>");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if ("Library".equals(text)) {
                    showLibraryPanel();
                }
                if ("Wishlist".equals(text)) {
                    showWishlistPanel();
                }
                if ("Readlist".equals(text)) {
                    showReadListPanel();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setText(text);
            }
        });
        return label;
    }

    private void showLibraryPanel() {
        libraryPanel.updateLibrary(currentUserEmail);
        cardLayout.show(containerPanel, "Library");
    }

    private void showWishlistPanel() {
        wishlistPanel.updateWishlist(currentUserEmail);
        cardLayout.show(containerPanel, "Wishlist");
    }

    private void showReadListPanel() {
        readListPanel.updateReadlist(currentUserEmail);
        cardLayout.show(containerPanel, "Readlist");
    }

    public void displaySearchResults(String searchText, String searchType) {
        searchResultsPanel.displayResults(searchText, searchType);
        cardLayout.show(containerPanel, "SearchResults");
    }

    private void showRecommendationPage() {
        cardLayout.show(containerPanel, "Recommendation");
        containerPanel.revalidate();
        containerPanel.repaint();
    }

    public void updateProfile(String email) {
        this.currentUserEmail = email;
        profileLabel.setText("Hey, " + email);
        profileLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logout();
            }
        });

        searchProfilePanel.remove(profileButton);
        searchProfilePanel.add(profileLabel);
        searchProfilePanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add space between components
        JLabel logoutLabel = new JLabel("Disconnect");
        logoutLabel.setForeground(Color.RED);
        logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logout();
            }
        });
        searchProfilePanel.add(logoutLabel);
        searchProfilePanel.revalidate();
        searchProfilePanel.repaint();

        recommendationPanel.updateLibraryMessage(true);
        libraryPanel.updateLibrary(email);

        recommendationPanel.updateWishlistMessage(true);
        wishlistPanel.updateWishlist(email);

        readListPanel.updateReadlist(email);

    }

    private void logout() {
        currentUserEmail = null;
        profileLabel.setText("");
        searchProfilePanel.removeAll();
        searchProfilePanel.add(customSearchField);
        searchProfilePanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add space between components
        searchProfilePanel.add(profileButton);
        searchProfilePanel.revalidate();
        searchProfilePanel.repaint();
        recommendationPanel.onUserLogout();
        recommendationPanel.updateLibraryMessage(false);
        libraryPanel.updateLibrary(null);

        recommendationPanel.updateWishlistMessage(false);
        wishlistPanel.updateWishlist(null);

        readListPanel.updateReadlist(null);

    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public void setCurrentUserEmail(String email) {
        this.currentUserEmail = email;
        notifyLoginListeners(email);
    }

    public interface UserLoginListener {
        void onUserLogin(String email);
    }

    public void addLoginListener(UserLoginListener listener) {
        loginListeners.add(listener);
    }

    private void notifyLoginListeners(String email) {
        for (UserLoginListener listener : loginListeners) {
            listener.onUserLogin(email);
        }
    }

    public void displayComicDetails(Comic comic, String sourcePanel) {
        comicDetailsPanel.setPreviousPanel(sourcePanel);
        comicDetailsPanel.displayComicDetails(comic);
        cardLayout.show(containerPanel, "ComicDetails");
    }

    public void showPreviousPanel() {
        String previousPanel = comicDetailsPanel.getPreviousPanel();
        cardLayout.show(containerPanel, previousPanel);
    }

    public void displayHeroDetails(Hero hero, String sourcePanel) {
        HeroProfilePanel profilePanel = new HeroProfilePanel();

        String description = hero.getDescription() != null
                ? hero.getDescription()
                : "<p style='color:gray;'>No description available.</p>";

        ImageIcon heroImage = null;
        if (hero.getImageUrl() != null && !hero.getImageUrl().isEmpty()) {
            try {
                URL imageURL = new URL(hero.getImageUrl());
                heroImage = new ImageIcon(imageURL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        profilePanel.updateProfile(
                hero.getName(),
                description,
                heroImage,
                hero.getTitles());

        JDialog dialog = new JDialog(this, hero.getName(), true);
        dialog.setLayout(new BorderLayout());
        dialog.add(profilePanel, BorderLayout.CENTER);
        dialog.setSize(1000, 600); // Landscape size
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void refreshAllPanels() {
        // Refresh recommendation panel
        if (recommendationPanel != null) {
            recommendationPanel.refreshHeartButtons();
            recommendationPanel.refreshStarButtons();
            recommendationPanel.refreshReadButtons();

        }

        // Refresh search results panel
        if (searchResultsPanel != null) {
            searchResultsPanel.refreshHeartButtons();
            searchResultsPanel.refreshStarButtons();
            searchResultsPanel.refreshReadButtons(); 

        }

        // Refresh library panel
        if (libraryPanel != null) {
            libraryPanel.updateLibrary(currentUserEmail);
        }

        // Refresh wishlist panel
        if (wishlistPanel != null) {
            wishlistPanel.updateWishlist(currentUserEmail);
        }
        if (readListPanel != null) {
            readListPanel.updateReadlist(currentUserEmail);
        }
    }

}
