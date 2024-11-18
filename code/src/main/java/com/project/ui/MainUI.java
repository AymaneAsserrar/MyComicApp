package com.project.ui;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {
    private static final long serialVersionUID = 2008701708169261499L;

	public MainUI() {
        setTitle("My Comic App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Create and add the TitleSearchPanel
        TitleSearchPanel titleSearchPanel = new TitleSearchPanel();
        add(titleSearchPanel, BorderLayout.NORTH);

        // Create and add the RecommendationPanel
        RecommendationPanel recommendationPanel = new RecommendationPanel();
        JScrollPane scrollPane = new JScrollPane(recommendationPanel); // Make recommendations scrollable
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::new);
    }
}
