import com.project.ui.UiMain;
import com.project.ui.SplashScreen;
import com.project.util.DatabaseUtil;
import javax.swing.*;
import com.project.controller.RecommendationController;

public class AppMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UiMain mainUI = new UiMain();
                mainUI.setVisible(false);
                
                // Create loading task that does not block UI
                Runnable loadingTask = () -> {
                    try {
                        SwingWorker<Void, Void> worker = new SwingWorker<>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                Thread.sleep(1000); // Initial delay
                                DatabaseUtil.createTables();
                                RecommendationController controller = new RecommendationController();
                                controller.getPopularComics(0, 10);
                                Thread.sleep(4000); // Remaining delay
                                return null;
                            }

                            @Override
                            protected void done() {
                                SwingUtilities.invokeLater(() -> mainUI.setVisible(true));
                            }
                        };
                        worker.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
                
                SplashScreen splash = new SplashScreen(loadingTask);
                splash.showSplash().thenRun(() -> 
                    SwingUtilities.invokeLater(() -> mainUI.setVisible(true))
                );
                
            } catch (Exception e) {
                System.err.println("Failed to initialize application: " + e.getMessage());
                JOptionPane.showMessageDialog(null, 
                    "Failed to initialize application", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}