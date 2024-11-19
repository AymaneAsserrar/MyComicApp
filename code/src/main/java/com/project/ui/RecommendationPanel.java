package com.project.ui;
import com.project.controller.RecommendationController;
import com.project.model.Comic;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.net.URL;

public class RecommendationPanel extends JPanel {

    private static final long serialVersionUID = 2561771664627867791L;

    public RecommendationPanel() {
        setLayout(new BorderLayout());

        JLabel popularComicsLabel = new JLabel("Popular Comics", SwingConstants.CENTER);
        popularComicsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(popularComicsLabel, BorderLayout.NORTH);

        JPanel comicsGridPanel = new JPanel(new GridLayout(0, 3, 5, 5)); // 3 columns, as many rows as needed

        int limit = 12; // number of recommended comics displayed

        // Import recommendation list with API
        RecommendationController recommendationController = new RecommendationController();
        List<Comic> recommendationList = recommendationController.getPopularComics(limit);

        for (int i = 0; i < limit; i++) {
            JPanel comicPanel = new JPanel(new BorderLayout());
            JLabel logoLabel;

            try {
                String coverImageUrl = recommendationList.get(i).getCoverImageUrl();
                if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
                    URL imageURL = new URL(coverImageUrl);
                    ImageIcon icon = new ImageIcon(imageURL);
                    Image img = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                    logoLabel = new JLabel(new ImageIcon(img));
                } else {
                    logoLabel = new JLabel(new ImageIcon("path/to/default/image.png"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                logoLabel = new JLabel(new ImageIcon("path/to/default/image.png"));
            }

            JLabel titleLabel = new JLabel(recommendationList.get(i).getName(), SwingConstants.CENTER);
            comicPanel.add(logoLabel, BorderLayout.CENTER);
            comicPanel.add(titleLabel, BorderLayout.SOUTH);
            comicsGridPanel.add(comicPanel);
        }

        add(comicsGridPanel, BorderLayout.CENTER);
    }
}
