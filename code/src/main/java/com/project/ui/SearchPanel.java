package com.project.ui;

import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {
    private static final long serialVersionUID = 1260918379909615305L;

    private JTextField searchField;
    private JButton searchButton;
    private JButton searchIconButton;

    public SearchPanel() {
        setLayout(new BorderLayout()); // Utiliser BorderLayout pour gérer la position des éléments

        // search field
        searchField = new JTextField(20);
        searchField.setVisible(false); // Masquer initialement le champ de recherche

        // actual search button
        searchButton = new JButton("Rechercher");
        searchButton.setVisible(false); // Masquer initialement le bouton de recherche

        // search icon button
        ImageIcon searchIcon = new ImageIcon(getClass().getResource("/search.png"));
        Image searchImage = searchIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        searchIconButton = new JButton(new ImageIcon(searchImage));
        searchIconButton.setPreferredSize(new Dimension(40, 40));
        searchIconButton.setFocusPainted(false); // Supprimer la bordure de focus
        searchIconButton.setBorderPainted(false); // Supprimer la bordure pour une apparence plus propre

        // Action de clic sur l'icône de recherche pour afficher/masquer le champ de recherche et le bouton
        searchIconButton.addActionListener(e -> {
            boolean isVisible = searchField.isVisible();
            searchField.setVisible(!isVisible);
            searchButton.setVisible(!isVisible);
            revalidate();
            repaint();
        });

        // Ajouter les composants dans les différentes régions du BorderLayout
        add(searchField, BorderLayout.CENTER); // Champ de recherche au centre
        add(searchButton, BorderLayout.WEST); // Bouton de recherche à gauche du champ
        add(searchIconButton, BorderLayout.EAST); // Icône de recherche toujours à droite
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getSearchButton() {
        return searchButton;
    }
}
