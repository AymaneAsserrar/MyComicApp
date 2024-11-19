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
        setLayout(new GridLayout(0, 4, 10, 10)); // 4 columns, variable rows
        int limit = 12;

        // Import recommendation list with API
        RecommendationController recommendationController = new RecommendationController();
        List<Comic> recommendationList = recommendationController.getPopularComics(limit);

        for (int i = 0; i < limit; i++) {
            JPanel comicPanel = new JPanel(new BorderLayout());
            JLabel logoLabel;

            try {
                // Charger l'image depuis l'URL du comic
                String coverImageUrl = recommendationList.get(i).getCoverImageUrl();
                if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
                    URL imageURL = new URL(coverImageUrl);
                    ImageIcon icon = new ImageIcon(imageURL);
                    Image img = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH); // Ajuster la taille selon les besoins
                    logoLabel = new JLabel(new ImageIcon(img));
                } else {
                    // Utiliser une image par défaut si aucune URL de couverture n'est disponible
                    logoLabel = new JLabel(new ImageIcon("path/to/default/image.png")); // Assurez-vous que cette image existe
                }
            } catch (Exception e) {
                e.printStackTrace();
                // En cas d'erreur lors du chargement de l'image, utiliser une image par défaut
                logoLabel = new JLabel(new ImageIcon("path/to/default/image.png")); // Assurez-vous que cette image existe
            }

            JLabel titleLabel = new JLabel(recommendationList.get(i).getName(), SwingConstants.CENTER); // display of Comic name
            comicPanel.add(logoLabel, BorderLayout.CENTER);
            comicPanel.add(titleLabel, BorderLayout.SOUTH);
            add(comicPanel);
        }
    }
}
