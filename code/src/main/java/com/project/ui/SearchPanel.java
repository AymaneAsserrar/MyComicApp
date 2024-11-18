package com.project.ui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SearchPanel extends JPanel {
    private static final long serialVersionUID = 1260918379909615305L;
	private JTextField searchField;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JScrollPane resultsScrollPane;

    public SearchPanel() {
        setLayout(new BorderLayout());

        // Search Bar Panel
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchBarPanel.add(new JLabel("Enter Title:"));
        searchBarPanel.add(searchField);
        searchBarPanel.add(searchButton);
        add(searchBarPanel, BorderLayout.NORTH);

        // Search Results Table (Hidden by default)
        tableModel = new DefaultTableModel(new Object[]{"Title", "Publisher", "Release Date"}, 0);
        resultsTable = new JTable(tableModel);
        resultsScrollPane = new JScrollPane(resultsTable);
        resultsScrollPane.setVisible(false); // Initially hidden
        add(resultsScrollPane, BorderLayout.CENTER);

        // Add Action Listeners
        searchButton.addActionListener(new SearchAction());
        searchField.addActionListener(new SearchAction()); // Trigger on Enter key
    }

    private class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchText = searchField.getText().trim();

            // Mock data for demonstration (Replace with API call)
            Object[][] mockResults = {
                {"Comic 1", "Publisher A", "2024-01-01"},
                {"Comic 2", "Publisher B", "2024-01-05"},
                {"Comic 3", "Publisher C", "2024-01-10"}
            };

            // Clear previous results
            tableModel.setRowCount(0);

            // Filter mock data (simulate search)
            boolean resultsFound = false;
            for (Object[] result : mockResults) {
                if (result[0].toString().toLowerCase().contains(searchText.toLowerCase())) {
                    tableModel.addRow(result);
                    resultsFound = true;
                }
            }

            // Show or hide the results table based on search results
            resultsScrollPane.setVisible(resultsFound);

            if (!resultsFound) {
                JOptionPane.showMessageDialog(SearchPanel.this, "No comics found for the title: " + searchText);
            }

            // Repaint and revalidate to update UI
            revalidate();
            repaint();
        }
    }
}