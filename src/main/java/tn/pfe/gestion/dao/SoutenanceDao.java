package tn.pfe.gestion.dao;

import tn.pfe.gestion.db.DbConnection;
import tn.pfe.gestion.model.Soutenance;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public final class SoutenanceDao {
    public int insert(Soutenance s) throws SQLException {
        String sql = "INSERT INTO soutenance(date, salle, note, id_pfe) VALUES(?,?,?,?)";
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(s.getDate()));
            ps.setString(2, s.getSalle());
            if (s.getNote() == null) {
                ps.setNull(3, java.sql.Types.DECIMAL);
            } else {
                ps.setBigDecimal(3, s.getNote());
            }
            ps.setInt(4, s.getIdPfe());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public boolean update(Soutenance s) throws SQLException {
        if (s.getIdSoutenance() == null) {
            throw new IllegalArgumentException("idSoutenance is required for update");
        }
        String sql = "UPDATE soutenance SET date=?, salle=?, note=?, id_pfe=? WHERE id_soutenance=?";
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(s.getDate()));
            ps.setString(2, s.getSalle());
            if (s.getNote() == null) {
                ps.setNull(3, java.sql.Types.DECIMAL);
            } else {
                ps.setBigDecimal(3, s.getNote());
            }
            ps.setInt(4, s.getIdPfe());
            ps.setInt(5, s.getIdSoutenance());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteById(int idSoutenance) throws SQLException {
        String sql = "DELETE FROM soutenance WHERE id_soutenance=?";
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idSoutenance);
            return ps.executeUpdate() == 1;
        }
    }

    public List<Soutenance> findAll() throws SQLException {
        String sql = "SELECT s.id_soutenance, s.date, s.salle, s.note, s.id_pfe, "
                + "p.titre AS pfe_titre, e.nom AS et_nom, e.prenom AS et_prenom "
                + "FROM soutenance s "
                + "JOIN pfe p ON p.id_pfe = s.id_pfe "
                + "JOIN etudiant e ON e.id_etudiant = p.id_etudiant "
                + "ORDER BY s.id_soutenance DESC";

        List<Soutenance> out = new ArrayList<>();
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Soutenance s = new Soutenance();
                s.setIdSoutenance(rs.getInt("id_soutenance"));
                Timestamp ts = rs.getTimestamp("date");
                s.setDate(ts == null ? null : ts.toLocalDateTime());
                s.setSalle(rs.getString("salle"));
                BigDecimal note = rs.getBigDecimal("note");
                s.setNote(note);
                s.setIdPfe(rs.getInt("id_pfe"));
                s.setPfeLabel("PFE " + s.getIdPfe() + ": " + rs.getString("pfe_titre")
                        + " — " + rs.getString("et_nom") + " " + rs.getString("et_prenom"));
                out.add(s);
            }
        }
        return out;
    }
}

