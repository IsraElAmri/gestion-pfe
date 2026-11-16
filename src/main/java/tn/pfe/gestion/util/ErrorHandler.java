package tn.pfe.gestion.util;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.SQLException;

/**
 * Centralizes user-friendly error messages.
 */
public final class ErrorHandler {
    private ErrorHandler() {
    }

    public static void showSqlError(Component parent, SQLException ex) {
        String msg = ex.getMessage();
        if (msg == null) msg = ex.toString();
        String low = msg.toLowerCase();

        if (low.contains("duplicate") && low.contains("email")) {
            msg = "Email déjà utilisé (doit être unique).";
        } else if (low.contains("duplicate") && low.contains("id_etudiant")) {
            msg = "Cet étudiant a déjà un PFE (1 seul PFE par étudiant).";
        } else if (low.contains("duplicate") && low.contains("id_pfe")) {
            msg = "Ce PFE a déjà une soutenance (1 soutenance max par PFE).";
        }

        JOptionPane.showMessageDialog(parent, msg, "Erreur BD", JOptionPane.ERROR_MESSAGE);
    }
}

