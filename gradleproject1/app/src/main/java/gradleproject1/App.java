package gradleproject1;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUIManager guiManager = new GUIManager();
        });
    }
}