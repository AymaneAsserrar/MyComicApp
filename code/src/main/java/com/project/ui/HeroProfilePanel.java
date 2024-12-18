package com.project.ui;
import javax.swing.*;
import java.awt.*;
public class HeroProfilePanel extends JPanel {
private JLabel nameLabel;
private JTextArea descriptionArea;
private JLabel imageLabel;
private JList<String> titlesList;
    

    public HeroProfilePanel() {
        setLayout(new BorderLayout());

        
        JPanel profilePanel = new JPanel(new BorderLayout());
        nameLabel = new JLabel("Name", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        descriptionArea = new JTextArea("Description");
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        imageLabel = new JLabel();

        profilePanel.add(nameLabel, BorderLayout.NORTH);
        profilePanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        profilePanel.add(imageLabel, BorderLayout.WEST);

       
        titlesList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(titlesList);


        // Add sections to the main panel
        add(profilePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateProfile(String name, String description, Icon image, String[] titles) {
        nameLabel.setText(name);
        descriptionArea.setText(description != null ? description : "N/A");
        imageLabel.setIcon(image != null ? image : new ImageIcon());
        titlesList.setListData(titles != null ? titles : new String[]{"No associated titles found"});
    }

}

