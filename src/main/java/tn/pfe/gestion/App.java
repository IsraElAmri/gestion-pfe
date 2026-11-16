package tn.pfe.gestion;

import tn.pfe.gestion.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Minimal entry point to validate JDBC connectivity.
 */
public final class App {
    public static void main(String[] args) {
        try (Connection cn = DbConnection.getConnection()) {
            System.out.println("Connected OK: " + (cn != null && !cn.isClosed()));

            try (PreparedStatement ps = cn.prepareStatement("SELECT DATABASE()")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Current DB: " + rs.getString(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DB connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

