package com.project.ui;
import com.project.controller.RecommendationController;
import com.project.model.Comic;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RecommendationPanel extends JPanel {

    private static final long serialVersionUID = 2561771664627867791L;
	public RecommendationPanel() {
		int columns = 3;
        setLayout(new GridLayout(0, columns, 10, 10));
        int limit = 12;
        // import recommendation list with API
        RecommendationController recommendationController = new RecommendationController();
        List<Comic> recommendationList = recommendationController.getPopularComics(limit);
        for (int i = 0; i < limit; i++) {
            JPanel comicPanel = new JPanel(new BorderLayout());
            JLabel logoLabel = new JLabel(new ImageIcon(recommendationList.get(i).getCoverImageUrl())); // display of Comic image
            JLabel titleLabel = new JLabel(recommendationList.get(i).getName(), SwingConstants.CENTER); // display of Comic name
            comicPanel.add(logoLabel, BorderLayout.CENTER);
            comicPanel.add(titleLabel, BorderLayout.SOUTH);
            add(comicPanel);
        }
    }
}

