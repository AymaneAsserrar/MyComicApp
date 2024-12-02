package com.project.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SearchPanel extends JPanel {
    private static final long serialVersionUID = 1260918379909615305L;

    private JTextField searchField;
    private JButton searchIconButton;
    private JComboBox<String> searchTypeComboBox;
    private boolean isSearchVisible = false;

    public SearchPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Search type combo box setup
        searchTypeComboBox = new JComboBox<>(new String[]{"Comic", "Character"});
        searchTypeComboBox.setFont(new Font("Verdana", Font.BOLD, 10));
        searchTypeComboBox.setPreferredSize(new Dimension(80, 25));
        searchTypeComboBox.setMaximumSize(new Dimension(80, 25));
        searchTypeComboBox.setBackground(Color.WHITE);
        searchTypeComboBox.setForeground(Color.BLACK);
        searchTypeComboBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        searchTypeComboBox.setVisible(false);

        // Search icon setup
        ImageIcon searchIconImage = new ImageIcon(getClass().getResource("/search.png"));
        Image searchImage = searchIconImage.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        searchIconButton = new JButton(new ImageIcon(searchImage));
        searchIconButton.setPreferredSize(new Dimension(40, 40));
        searchIconButton.setMaximumSize(new Dimension(40, 40));
        searchIconButton.setFocusPainted(false);
        searchIconButton.setBorderPainted(false);
        searchIconButton.setContentAreaFilled(false);
        searchIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Search field setup
        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(150, 25));
        searchField.setMaximumSize(new Dimension(150, 25));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        searchField.setVisible(false);

        // Toggle the visibility of the search field on icon click
        searchIconButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isSearchVisible = !isSearchVisible;
                searchField.setVisible(isSearchVisible);
                searchTypeComboBox.setVisible(isSearchVisible);
                if (!isSearchVisible) {
                    searchField.setText("");
                }
                revalidate();
                repaint();
            }
        });

        // Add key listener for pressing Enter in the search field to trigger search
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim();
                performSearch(searchTerm);
            }
        });

        // Add key listener to enable/disable the search action based on input
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim();
                searchIconButton.setEnabled(!text.isEmpty());
            }
        });

        // Create a container to hold search bar elements aligned to the right
        JPanel rightAlignedPanel = new JPanel();
        rightAlignedPanel.setLayout(new BoxLayout(rightAlignedPanel, BoxLayout.X_AXIS));
        rightAlignedPanel.setOpaque(false);
        rightAlignedPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightAlignedPanel.add(searchField);
        rightAlignedPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        rightAlignedPanel.add(searchTypeComboBox);

        // Set up the main panel layout: Search icon stays on the right, right-aligned components come before it
        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setOpaque(false);
        searchContainer.add(rightAlignedPanel, BorderLayout.CENTER);
        searchContainer.add(searchIconButton, BorderLayout.EAST);

        // Add the container panel
        add(searchContainer, BorderLayout.EAST);
    }

    private void performSearch(String searchTerm) {
        if (!searchTerm.isEmpty()) {
            UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(this);
            String searchType = (String) searchTypeComboBox.getSelectedItem();
            parentFrame.displaySearchResults(searchTerm, searchType);
        }
    }
}
