package com.project.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SearchResultsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTable resultsTable;
    private DefaultTableModel tableModel;

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
    }


}
