package com.project.ui;

import com.project.controller.SearchController;
import com.project.model.Comic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class SearchPanel extends JPanel {
    private static final long serialVersionUID = 1260918379909615305L;

    private JTextField searchField;
    private JButton searchButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private SearchController searchController;

    public SearchPanel() {
        setLayout(new BorderLayout());

        // Search field setup
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        
        // Search button setup
        ImageIcon searchIcon = new ImageIcon(getClass().getResource("/search.png"));
        Image searchImage = searchIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        searchButton = new JButton(new ImageIcon(searchImage));
        searchButton.setPreferredSize(new Dimension(40, 40));
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setEnabled(false);

        // Results table setup
        String[] columnNames = {"ID", "Name", "Cover Image URL"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        // Search field listener to enable/disable button
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim();
                searchButton.setEnabled(!text.isEmpty());
            }
        });

        // Initialize search controller
        searchController = new SearchController();

        // Search button action listener
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim();
                performSearch(searchTerm);
            }
        });

        // Layout components
        JPanel searchBar = new JPanel(new BorderLayout());
        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(searchButton, BorderLayout.EAST);

        add(searchBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void performSearch(String searchTerm) {
        // Clear previous results
        tableModel.setRowCount(0);

        // Perform search
        List<Comic> searchResults = searchController.searchComicsByTitle(searchTerm);

        // Populate table with results
        for (Comic comic : searchResults) {
            tableModel.addRow(new Object[]{
                comic.getId(),
                comic.getName(),
                comic.getCoverImageUrl()
            });
        }
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getSearchButton() {
        return searchButton;
    }
}