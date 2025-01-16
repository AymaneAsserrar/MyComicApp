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
                
                Runnable loadingTask = () -> {
                    try {
                        DatabaseUtil.createTables();
                        RecommendationController controller = new RecommendationController();
                        controller.getPopularComics(0, 10);
                        Thread.sleep(5000);
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