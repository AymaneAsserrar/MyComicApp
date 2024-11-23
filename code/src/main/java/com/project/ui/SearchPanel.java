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

    public SearchPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Search icon setup
        ImageIcon searchIconImage = new ImageIcon(getClass().getResource("/search.png"));
        Image searchImage = searchIconImage.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH); // Smaller icon size
        searchIconButton = new JButton(new ImageIcon(searchImage));
        searchIconButton.setPreferredSize(new Dimension(35, 35)); // Reduced the size to make it more compact
        searchIconButton.setFocusPainted(false);
        searchIconButton.setBorderPainted(false);
        searchIconButton.setContentAreaFilled(false); // Makes the button transparent
        searchIconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Search field setup
        searchField = new JTextField(15);
        searchField.setVisible(false); // Initially hidden
        searchField.setPreferredSize(new Dimension(150, 25)); // Width: 150 pixels, Height: 25 pixels
        searchField.setMaximumSize(new Dimension(150, 25)); // Set maximum size to limit the height

        // Toggle the visibility of the search field on icon click
        searchIconButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean currentlyVisible = searchField.isVisible();
                searchField.setVisible(!currentlyVisible);
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
        rightAlignedPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightAlignedPanel.add(Box.createHorizontalGlue());
        rightAlignedPanel.add(searchField);
        rightAlignedPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Small space between field and icon
        rightAlignedPanel.add(searchIconButton);

        // Set up the main panel layout
        add(Box.createHorizontalGlue(), BorderLayout.WEST);
        add(rightAlignedPanel, BorderLayout.EAST);
    }

    private void performSearch(String searchTerm) {
        if (!searchTerm.isEmpty()) {
            // Trigger the search if the search field is not empty
            UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(this);
            parentFrame.displaySearchResults(searchTerm);
        }
    }
}
