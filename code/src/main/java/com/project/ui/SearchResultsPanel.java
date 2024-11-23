package com.project.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.project.controller.SearchController;
import com.project.model.Comic;

import java.awt.*;
import java.net.URL;
import java.util.List;

public class SearchResultsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private SearchController searchController = new SearchController();
    private List<Comic> searchList;
    JLabel searchComicsLabel = new JLabel("Found Comics", SwingConstants.CENTER);
    JPanel comicsGridPanel = new JPanel(new GridLayout(0, 3, 5, 5)); // 3 columns, as many rows as needed

    public SearchResultsPanel() {
        setLayout(new BorderLayout());

        searchComicsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(searchComicsLabel, BorderLayout.NORTH);
    }

    public boolean performSearch(String searchText) {

        this.searchList = searchController.searchComicsByTitle(searchText);
        int limit = this.searchList.size() > 12 ? 12 : this.searchList.size();

        if(this.searchList.size() <= 0) {return false;}
        for (int i = 0; i < limit; i++) {
            JPanel comicPanel = new JPanel(new BorderLayout());
            JLabel logoLabel;

            try {
                String coverImageUrl = searchList.get(i).getCoverImageUrl();
                if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
                    URL imageURL = new URL(coverImageUrl);
                    ImageIcon icon = new ImageIcon(imageURL);
                    Image img = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                    logoLabel = new JLabel(new ImageIcon(img));
                } else {
                    ImageIcon fallbackIcon = new ImageIcon("image-comics-seeklogo.svg");
                    Image fallbackImg = fallbackIcon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                    logoLabel = new JLabel(new ImageIcon(fallbackImg));
                }
            } catch (Exception e) {
                e.printStackTrace();
                ImageIcon fallbackIcon = new ImageIcon("image-comics-seeklogo.svg");
                Image fallbackImg = fallbackIcon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                logoLabel = new JLabel(new ImageIcon(fallbackImg));
            }
            
            JLabel titleLabel = new JLabel(searchList.get(i).getName(), SwingConstants.CENTER);
            comicPanel.add(logoLabel, BorderLayout.CENTER);
            comicPanel.add(titleLabel, BorderLayout.SOUTH);
            comicsGridPanel.add(comicPanel);
        }

        add(comicsGridPanel, BorderLayout.CENTER);

        return true;
    }
    /*
    public SearchResultsPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Title", "Publisher", "Release Date"}, 0);
        resultsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Initially, hide the search results panel until there's a search
        setVisible(false);
    }

    public boolean performSearch(String searchText) {
        // Mock data for demonstration (replace with real API call)
        Object[][] mockResults = {
            {"Comic 1", "Publisher A", "2024-01-01"},
            {"Comic 2", "Publisher B", "2024-01-05"},
            {"Comic 3", "Publisher C", "2024-01-10"}
        };

        // Clear previous results
        tableModel.setRowCount(0);
        boolean resultsFound = false;

        // Check if any result contains the search text (case insensitive)
        for (Object[] result : mockResults) {
            if (result[0].toString().toLowerCase().contains(searchText.toLowerCase())) {
                tableModel.addRow(result);
                resultsFound = true;
            }
        }


        // After populating the table, make the panel visible if there are results
        setVisible(resultsFound);


        // Refresh layout
        revalidate();
        repaint();

        return resultsFound;
    }*/

}
