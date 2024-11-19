package com.project.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class SearchPanel extends JPanel {
    private static final long serialVersionUID = 1260918379909615305L;

    private JTextField searchField;
    private JButton searchButton;

    public SearchPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER));

        // search field
        searchField = new JTextField(20);
        // search button
        ImageIcon searchIcon = new ImageIcon(getClass().getResource("/search.png"));
        Image searchImage = searchIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); // resizing the image
        searchButton = new JButton(new ImageIcon(searchImage));


        // Disable the search button initially
        searchButton.setEnabled(false);

        // Add a listener to enable/disable the button based on text input
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                toggleSearchButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                toggleSearchButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                toggleSearchButton();
            }
        });

        add(new JLabel("Enter Title:"));
        add(searchField);
        add(searchButton);
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    private void toggleSearchButton() {
        // Enable the button only if there is text in the search field
        searchButton.setEnabled(!searchField.getText().trim().isEmpty());
    }
}
