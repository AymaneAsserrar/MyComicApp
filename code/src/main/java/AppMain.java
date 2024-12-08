
import com.project.ui.UiMain;

import javax.swing.*;

public class AppMain {
    public static void main(String[] args) {
        // Start the UI in the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(UiMain::new);
    }
}
