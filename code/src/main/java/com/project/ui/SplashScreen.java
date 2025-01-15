package com.project.ui;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SplashScreen extends JWindow {
    private static final int DISPLAY_TIME = 8000;
    private final JProgressBar progressBar;
    private Clip audioClip;
    private Runnable loadingTask;
    private volatile boolean isLoadingComplete = false;
    private CompletableFuture<Void> splashComplete;
    private int currentProgress = 0;
    private Timer animationTimer;

    public SplashScreen(Runnable loadingTask) {
        this.loadingTask = loadingTask;
        this.splashComplete = new CompletableFuture<>();
        
        // Enable double buffering
        getRootPane().setDoubleBuffered(true);
        
        // Create main content panel
        JPanel content = (JPanel)getContentPane();
        content.setLayout(new BorderLayout());
        content.setBackground(new Color(50, 50, 50));
        content.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));

        // Add animated logo with double buffering
        URL gifURL = getClass().getClassLoader().getResource("splash.gif");
        if (gifURL != null) {
            ImageIcon imageIcon = new ImageIcon(gifURL);
            JLabel animation = new JLabel(imageIcon) {
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Toolkit.getDefaultToolkit().sync(); // Sync graphics
                }
            };
            animation.setHorizontalAlignment(SwingConstants.CENTER);
            content.add(animation, BorderLayout.CENTER);
        }

        // Load and prepare sound
        try {
            URL soundURL = getClass().getResource("/splash.wav");
            if (soundURL != null) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                        new BufferedInputStream(soundURL.openStream()));
                audioClip = AudioSystem.getClip();
                audioClip.open(audioStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Bottom panel setup remains the same
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(new Color(50, 50, 50));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel loadingText = new JLabel("Loading...", SwingConstants.CENTER);
        loadingText.setFont(new Font("Arial", Font.BOLD, 16));
        loadingText.setForeground(Color.WHITE);
        bottomPanel.add(loadingText, BorderLayout.NORTH);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(70, 130, 180));
        progressBar.setBackground(new Color(30, 30, 30));
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        content.add(bottomPanel, BorderLayout.SOUTH);

        // Configure window with new dimensions
        setContentPane(content);
        setSize(800, 800);
        setLocationRelativeTo(null);
    }

    public CompletableFuture<Void> showSplash() {
        setVisible(true);
        progressBar.setValue(0);

        if (audioClip != null) {
            audioClip.setFramePosition(0);
            audioClip.start();
        }

        // Start loading task in background
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                long startTime = System.currentTimeMillis();
                
                // Execute loading task
                loadingTask.run();
                isLoadingComplete = true;

                // Ensure minimum display time
                while (System.currentTimeMillis() - startTime < DISPLAY_TIME) {
                    Thread.sleep(50);
                }
                return null;
            }

            @Override
            protected void done() {
                if (audioClip != null) {
                    audioClip.stop();
                    audioClip.close();
                }
                dispose();
                splashComplete.complete(null);
            }
        };

        // Create smooth animation timer
        animationTimer = new Timer(50, e -> {
            if (currentProgress < 100) {
                currentProgress++;
                progressBar.setValue(currentProgress);
            }
        });

        worker.execute();
        animationTimer.start();

        return splashComplete;
    }
}