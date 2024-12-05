package com.project.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import com.project.ui.UiMain;

public class CustomSearchField extends JPanel {
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> searchTypeComboBox;
    private boolean isSearchVisible = false;

    public CustomSearchField() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(new Color(70, 70, 70)); // Better background color

        // Search Field
        searchField = new JTextField(10);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        searchField.setMaximumSize(new Dimension(150, 30)); // Set maximum size to prevent overlap
        searchField.setVisible(isSearchVisible);

        // Search Type ComboBox
        searchTypeComboBox = new JComboBox<>(new String[]{"Comic", "Character"});
        searchTypeComboBox.setFont(new Font("Verdana", Font.BOLD, 14));
        searchTypeComboBox.setPreferredSize(new Dimension(100, 30));
        searchTypeComboBox.setMaximumSize(new Dimension(100, 30)); // Set maximum size to prevent overlap
        searchTypeComboBox.setBackground(Color.WHITE);
        searchTypeComboBox.setForeground(Color.BLACK);
        searchTypeComboBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        searchTypeComboBox.setVisible(isSearchVisible);

        // Search Icon Button
        ImageIcon searchIconImage = new ImageIcon(getClass().getResource("/search.png"));
        Image searchImage = searchIconImage.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        searchButton = new JButton(new ImageIcon(searchImage));
        searchButton.setPreferredSize(new Dimension(40, 40));
        searchButton.setMaximumSize(new Dimension(40, 40)); // Set maximum size to prevent overlap
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                searchButton.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                searchButton.setBorder(null);
            }
        });

        // Add components to main panel
        add(searchField);
        add(Box.createRigidArea(new Dimension(5, 0))); // Add space between components
        add(searchTypeComboBox);
        add(Box.createRigidArea(new Dimension(5, 0))); // Add space between components
        add(searchButton);

        // Action Listeners
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSearchVisibility();
            }
        });

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
    }

    private void toggleSearchVisibility() {
        isSearchVisible = !isSearchVisible;
        searchField.setVisible(isSearchVisible);
        searchTypeComboBox.setVisible(isSearchVisible);
        revalidate();
        repaint();
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        String searchType = (String) searchTypeComboBox.getSelectedItem();
        if (!searchTerm.isEmpty()) {
            UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(this);
            parentFrame.displaySearchResults(searchTerm, searchType);
        }
    }
}