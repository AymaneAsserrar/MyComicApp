package com.project.ui;

import com.project.controller.RecommendationController;
import com.project.model.Comic;
import com.project.util.ScrollUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.net.URL;

public class RecommendationPanel extends JPanel {
    private static final long serialVersionUID = 2561771664627867791L;
    private JPanel comicsGridPanel;
    private RecommendationController recommendationController;

    public RecommendationPanel() {
        setLayout(new BorderLayout());

        JLabel popularComicsLabel = new JLabel("Popular Comics", SwingConstants.CENTER);
        popularComicsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(popularComicsLabel, BorderLayout.NORTH);

        comicsGridPanel = new JPanel(new GridLayout(0, 3, 5, 5));
        recommendationController = new RecommendationController();

        JScrollPane scrollPane = ScrollUtil.createInfiniteScrollPane(comicsGridPanel, 
            offset -> loadMoreComics(offset));

        add(scrollPane, BorderLayout.CENTER);
        loadMoreComics(0);
    }

    private void loadMoreComics(int offset) {
        List<Comic> recommendationList = recommendationController.getPopularComics(ScrollUtil.PAGE_SIZE);
        for (Comic comic : recommendationList) {
            addComicPanel(comic);
        }
        revalidate();
        repaint();
    }

    private void addComicPanel(Comic comic) {
        JPanel comicPanel = new JPanel(new BorderLayout());
        JLabel logoLabel;

        try {
            String coverImageUrl = comic.getCoverImageUrl();
            if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
                URL imageURL = new URL(coverImageUrl);
                ImageIcon icon = new ImageIcon(imageURL);
                Image img = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                logoLabel = new JLabel(new ImageIcon(img));
            } else {
                logoLabel = createFallbackLabel();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logoLabel = createFallbackLabel();
        }

        JLabel titleLabel = new JLabel(comic.getName(), SwingConstants.CENTER);
        comicPanel.add(logoLabel, BorderLayout.CENTER);
        comicPanel.add(titleLabel, BorderLayout.SOUTH);
        
        comicPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comicPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Comic detailedComic = recommendationController.getComicDetails(comic.getId());
                if (detailedComic != null) {
                    UiMain parentFrame = (UiMain) SwingUtilities.getWindowAncestor(RecommendationPanel.this);
                    parentFrame.displayComicDetails(detailedComic, "Recommendation");
                }
            }
        });
        
        comicsGridPanel.add(comicPanel);
    }

    private JLabel createFallbackLabel() {
        ImageIcon fallbackIcon = new ImageIcon("image-comics-seeklogo.svg");
        Image fallbackImg = fallbackIcon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(fallbackImg));
    }
}
