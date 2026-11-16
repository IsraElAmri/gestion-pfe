package tn.pfe.gestion.ui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class UiLauncher {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            MainMenuFrame frame = new MainMenuFrame();
            frame.setVisible(true);
        });
    }
}

