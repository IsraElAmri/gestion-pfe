package tn.pfe.gestion.dao;

import tn.pfe.gestion.db.DbConnection;
import tn.pfe.gestion.model.Etudiant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class EtudiantDao {

    public int insert(Etudiant e) throws SQLException {
        String sql = "INSERT INTO etudiant(nom, prenom, email, classe) VALUES(?,?,?,?)";
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getPrenom());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getClasse());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public boolean update(Etudiant e) throws SQLException {
        if (e.getIdEtudiant() == null) {
            throw new IllegalArgumentException("idEtudiant is required for update");
        }

        String sql = "UPDATE etudiant SET nom=?, prenom=?, email=?, classe=? WHERE id_etudiant=?";
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getPrenom());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getClasse());
            ps.setInt(5, e.getIdEtudiant());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteById(int idEtudiant) throws SQLException {
        String sql = "DELETE FROM etudiant WHERE id_etudiant=?";
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEtudiant);
            return ps.executeUpdate() == 1;
        }
    }

    public Optional<Etudiant> findById(int idEtudiant) throws SQLException {
        String sql = "SELECT id_etudiant, nom, prenom, email, classe FROM etudiant WHERE id_etudiant=?";
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEtudiant);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(rs));
            }
        }
    }

    public List<Etudiant> findAll() throws SQLException {
        String sql = "SELECT id_etudiant, nom, prenom, email, classe FROM etudiant ORDER BY id_etudiant DESC";
        List<Etudiant> out = new ArrayList<>();
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(mapRow(rs));
            }
        }
        return out;
    }

    private Etudiant mapRow(ResultSet rs) throws SQLException {
        Etudiant e = new Etudiant();
        e.setIdEtudiant(rs.getInt("id_etudiant"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        e.setEmail(rs.getString("email"));
        e.setClasse(rs.getString("classe"));
        return e;
    }
}

