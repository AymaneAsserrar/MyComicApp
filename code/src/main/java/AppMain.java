import com.project.ui.UiMain;
import com.project.util.DatabaseUtil;

import javax.swing.*;

public class AppMain {
    public static void main(String[] args) {
        try {
            // Initialize database tables before starting UI
            DatabaseUtil.createTables();
            
            // Start the UI in the Event Dispatch Thread (EDT)
            SwingUtilities.invokeLater(UiMain::new);
        } catch (Exception e) {
            System.err.println("Failed to initialize application: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize application database", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
