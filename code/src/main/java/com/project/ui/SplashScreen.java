package com.project.ui;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class SplashScreen extends JWindow {
    private Clip audioClip;
    private volatile boolean isLoadingComplete = false;
    private CompletableFuture<Void> splashComplete;
    private Timer progressTimer;
    private Timer animationTimer;
    private JProgressBar progressBar;
    private List<BufferedImage> frames;
    private int currentFrame = 0;
    private static final int FRAME_DELAY = 33; // ~30 FPS
    private volatile boolean canComplete = false;
    private volatile boolean framesLoaded = false;
    private JPanel animationPanel;
    private final CountDownLatch framesLoadedLatch = new CountDownLatch(1);

    public SplashScreen(Runnable loadingTask) {
        this.splashComplete = new CompletableFuture<>();
        setupWindow();
        setupUI(loadingTask);
        setVisible(true);

        // Start all components in parallel
        CompletableFuture.allOf(
            // Load frames
            CompletableFuture.runAsync(() -> {
                preloadFrames();
                framesLoadedLatch.countDown();
            }),
            // Setup and start audio
            CompletableFuture.runAsync(() -> {
                setupAudio();
                if (audioClip != null) {
                    SwingUtilities.invokeLater(() -> {
                        audioClip.setFramePosition(0); // Ensure we start from beginning
                        audioClip.flush(); // Clear any buffered audio
                        audioClip.start();
                    });
                }
            }),
            // Start animation when frames are ready
            CompletableFuture.runAsync(() -> {
                try {
                    framesLoadedLatch.await();
                    SwingUtilities.invokeLater(() -> {
                        startAnimation();
                        startProgress();
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            })
        );
    }

    private void preloadFrames() {
        try {
            URL gifUrl = getClass().getClassLoader().getResource("splash.gif");
            if (gifUrl != null) {
                ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
                reader.setInput(ImageIO.createImageInputStream(gifUrl.openStream()));

                frames = new ArrayList<>();
                int count = reader.getNumImages(true);
                for (int i = 0; i < count; i++) {
                    frames.add(reader.read(i));
                }
                framesLoaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupAudio() {
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
    }

    private void setupWindow() {
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));
        System.setProperty("awt.image.rendering", "quality");
        setSize(800, 800);
        setLocationRelativeTo(null);
    }

    private void setupUI(Runnable loadingTask) {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(50, 50, 50));
        content.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));

        setupGifPanel(content);
        setupProgressPanel(content);

        setContentPane(content);

        // Start loading task
        CompletableFuture.runAsync(loadingTask)
                .thenRun(() -> isLoadingComplete = true);
    }

    private void setupGifPanel(JPanel content) {
        JPanel gifPanel = new JPanel(new BorderLayout());
        gifPanel.setOpaque(false);

        animationPanel = new JPanel() {
            {
                setOpaque(false);
                setDoubleBuffered(true);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (framesLoaded && !frames.isEmpty()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    BufferedImage currentImage = frames.get(currentFrame);
                    int x = (getWidth() - currentImage.getWidth()) / 2;
                    int y = (getHeight() - currentImage.getHeight()) / 2;
                    g2d.drawImage(currentImage, x, y, null);
                    g2d.dispose();
                }
            }
        };

        gifPanel.add(animationPanel, BorderLayout.CENTER);
        content.add(gifPanel, BorderLayout.CENTER);
    }

    private void setupProgressPanel(JPanel content) {
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(new Color(50, 50, 50));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel loadingText = new JLabel("Loading...", SwingConstants.CENTER);
        loadingText.setFont(new Font("Arial", Font.BOLD, 16));
        loadingText.setForeground(Color.WHITE);
        bottomPanel.add(loadingText, BorderLayout.NORTH);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(70, 130, 180));
        progressBar.setBackground(new Color(30, 30, 30));
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        content.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void startAnimation() {
        if (!framesLoaded || frames == null || frames.isEmpty()) return;

        animationTimer = new Timer(FRAME_DELAY, e -> {
            currentFrame = (currentFrame + 1) % frames.size();
            if (currentFrame == 0 && isLoadingComplete && progressBar.getValue() >= 100) {
                canComplete = true;
                completeSplash();
            }
            animationPanel.repaint();
        });
        animationTimer.start();
    }

    public CompletableFuture<Void> showSplash() {
        return splashComplete;
    }

    private void startProgress() {
        final long startTime = System.currentTimeMillis();
        final int totalDuration = 4500; // Match with total loading time

        progressTimer = new Timer(16, e -> { // More frequent updates
            long elapsed = System.currentTimeMillis() - startTime;
            int progress = (int) ((elapsed * 100.0) / totalDuration);

            SwingUtilities.invokeLater(() -> {
                if (progress >= 100) {
                    progressBar.setValue(100);
                    ((Timer) e.getSource()).stop();

                    if (isLoadingComplete) {
                        completeSplash();
                    }
                } else {
                    progressBar.setValue(progress);
                }
            });
        });

        progressTimer.start();
    }

    private void completeSplash() {
        if (!canComplete)
            return;

        SwingUtilities.invokeLater(() -> {
            if (animationTimer != null) {
                animationTimer.stop();
            }
            if (audioClip != null) {
                audioClip.stop();
                audioClip.close();
            }
            if (progressTimer != null) {
                progressTimer.stop();
            }
            dispose();
            splashComplete.complete(null);
        });
    }

    @Override
    public void dispose() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        if (progressTimer != null) {
            progressTimer.stop();
        }
        super.dispose();
    }
}