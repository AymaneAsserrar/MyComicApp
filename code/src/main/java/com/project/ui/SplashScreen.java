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

public class SplashScreen extends JWindow {
    private Clip audioClip;
    private volatile boolean isLoadingComplete = false;
    private CompletableFuture<Void> splashComplete;
    private Timer progressTimer;
    private JProgressBar progressBar;
    private JLabel gifLabel;
    private volatile boolean isRunning = true;
    private List<BufferedImage> frames;
    private int currentFrame = 0;
    private long lastFrameTime;
    private static final int FRAME_DELAY = 33; // ~30 FPS

    public SplashScreen(Runnable loadingTask) {
        this.splashComplete = new CompletableFuture<>();

        // Enable better rendering
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));

        // Create high-quality rendering hints
        System.setProperty("awt.image.rendering", "quality");

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(50, 50, 50));
        content.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));

        // Setup GIF with special handling
        setupGifPanel(content);

        // Setup audio
        setupAudio();

        // Setup progress panel
        setupProgressPanel(content);

        setContentPane(content);
        setSize(800, 800);
        setLocationRelativeTo(null);

        // Start the loading task independently
        CompletableFuture.runAsync(loadingTask)
                .thenRun(() -> {
                    isLoadingComplete = true;
                    if (progressBar.getValue() >= 100) {
                        completeSplash();
                    }
                });
    }

    private void setupGifPanel(JPanel content) {
        JPanel gifPanel = new JPanel(new BorderLayout());
        gifPanel.setOpaque(false);

        try {
            URL gifUrl = getClass().getClassLoader().getResource("splash.gif");
            if (gifUrl != null) {
                // Pre-load all frames
                ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
                reader.setInput(ImageIO.createImageInputStream(gifUrl.openStream()));
                
                frames = new ArrayList<>();
                int count = reader.getNumImages(true);
                for (int i = 0; i < count; i++) {
                    BufferedImage frame = reader.read(i);
                    frames.add(frame);
                }

                // Custom animation panel
                JPanel animationPanel = new JPanel() {
                    {
                        setOpaque(false);
                        setDoubleBuffered(true);
                    }

                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (!frames.isEmpty()) {
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

                // Start animation thread
                Thread animationThread = new Thread(() -> {
                    lastFrameTime = System.nanoTime();
                    while (isRunning) {
                        long now = System.nanoTime();
                        if (now - lastFrameTime >= FRAME_DELAY * 1_000_000) {
                            currentFrame = (currentFrame + 1) % frames.size();
                            SwingUtilities.invokeLater(() -> animationPanel.repaint());
                            lastFrameTime = now;
                        }
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                });
                animationThread.setDaemon(true);
                animationThread.start();

                gifPanel.add(animationPanel, BorderLayout.CENTER);
                content.add(gifPanel, BorderLayout.CENTER);
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

    public CompletableFuture<Void> showSplash() {
        setVisible(true);
        
        if (audioClip != null) {
            audioClip.setFramePosition(0);
            audioClip.start();
        }

        startProgress();
        return splashComplete;
    }

    private void startProgress() {
        final long startTime = System.currentTimeMillis();
        final int totalDuration = 5000; // Match with total loading time

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
        SwingUtilities.invokeLater(() -> {
            if (audioClip != null) {
                audioClip.stop();
                audioClip.close();
            }
            dispose();
            splashComplete.complete(null);
        });
    }

    @Override
    public void dispose() {
        isRunning = false;
        super.dispose();
    }
}