package com.project.ui;

import javax.swing.*;
import java.awt.*;

public class RecommendationPanel extends JPanel {

    private static final long serialVersionUID = 2561771664627867791L;

	public RecommendationPanel() {
        setLayout(new GridLayout(0, 4, 10, 10)); // 4 columns, variable rows

        // Mock Data for Recommendations (Replace with API data later)
        for (int i = 1; i <= 12; i++) {
            JPanel comicPanel = new JPanel(new BorderLayout());
            JLabel logoLabel = new JLabel(new ImageIcon("path_to_logo_" + i + ".png")); // Replace with actual image paths
            JLabel titleLabel = new JLabel("Comic " + i, SwingConstants.CENTER);
            comicPanel.add(logoLabel, BorderLayout.CENTER);
            comicPanel.add(titleLabel, BorderLayout.SOUTH);
            add(comicPanel);
        }
    }
}

