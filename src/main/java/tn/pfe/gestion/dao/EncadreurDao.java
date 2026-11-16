package tn.pfe.gestion.dao;

import tn.pfe.gestion.db.DbConnection;
import tn.pfe.gestion.model.Encadreur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class EncadreurDao {
    public int insert(Encadreur e) throws SQLException {
        String sql = "INSERT INTO encadreur(nom, prenom, grade, email) VALUES(?,?,?,?)";
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getPrenom());
            ps.setString(3, e.getGrade());
            ps.setString(4, e.getEmail());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public boolean update(Encadreur e) throws SQLException {
        if (e.getIdEncadreur() == null) {
            throw new IllegalArgumentException("idEncadreur is required for update");
        }
        String sql = "UPDATE encadreur SET nom=?, prenom=?, grade=?, email=? WHERE id_encadreur=?";
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getPrenom());
            ps.setString(3, e.getGrade());
            ps.setString(4, e.getEmail());
            ps.setInt(5, e.getIdEncadreur());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteById(int idEncadreur) throws SQLException {
        String sql = "DELETE FROM encadreur WHERE id_encadreur=?";
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEncadreur);
            return ps.executeUpdate() == 1;
        }
    }

    public Optional<Encadreur> findById(int idEncadreur) throws SQLException {
        String sql = "SELECT id_encadreur, nom, prenom, grade, email FROM encadreur WHERE id_encadreur=?";
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEncadreur);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapRow(rs));
            }
        }
    }

    public List<Encadreur> findAll() throws SQLException {
        String sql = "SELECT id_encadreur, nom, prenom, grade, email FROM encadreur ORDER BY id_encadreur DESC";
        List<Encadreur> out = new ArrayList<>();
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
        }
        return out;
    }

    private Encadreur mapRow(ResultSet rs) throws SQLException {
        Encadreur e = new Encadreur();
        e.setIdEncadreur(rs.getInt("id_encadreur"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        e.setGrade(rs.getString("grade"));
        e.setEmail(rs.getString("email"));
        return e;
    }
}

