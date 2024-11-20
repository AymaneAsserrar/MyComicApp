package com.project.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SearchPanel extends JPanel {
    private static final long serialVersionUID = 1260918379909615305L;

    private JTextField searchField;
    private JButton searchButton;

    public SearchPanel() {
        setLayout(new BorderLayout());

        // Champ de recherche toujours visible
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200,30));
        
        // Bouton de recherche avec un logo
        ImageIcon searchIcon = new ImageIcon(getClass().getResource("/search.png"));
        Image searchImage = searchIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        searchButton = new JButton(new ImageIcon(searchImage));
        searchButton.setPreferredSize(new Dimension(40, 40));
        searchButton.setFocusPainted(false); // Supprimer la bordure de focus
        searchButton.setBorderPainted(false); // Supprimer la bordure pour une apparence plus propre
        searchButton.setEnabled(false); // Désactiver initialement le bouton

        // Ajouter un écouteur au champ de recherche pour activer/désactiver le bouton
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim();
                searchButton.setEnabled(!text.isEmpty());
            }
        });

        // Ajouter les composants au panneau
        add(searchField, BorderLayout.CENTER); // Champ de recherche au centre
        add(searchButton, BorderLayout.EAST);  // Bouton de recherche à droite
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getSearchButton() {
        return searchButton;
    }
}
