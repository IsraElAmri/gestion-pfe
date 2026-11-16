package tn.pfe.gestion.dao;

import tn.pfe.gestion.db.DbConnection;
import tn.pfe.gestion.model.Pfe;
import tn.pfe.gestion.model.PfeEtat;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class PfeDao {

    public int insert(Pfe p, List<Integer> encadreurIds) throws SQLException {
        String sql = "INSERT INTO pfe(titre, description, etat, date_soutenance, id_etudiant) VALUES(?,?,?,?,?)";

        try (Connection cn = DbConnection.getConnection()) {
            cn.setAutoCommit(false);
            try {
                int idPfe;
                try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, p.getTitre());
                    ps.setString(2, p.getDescription());
                    ps.setString(3, p.getEtat().name());
                    if (p.getDateSoutenance() == null) {
                        ps.setNull(4, java.sql.Types.DATE);
                    } else {
                        ps.setDate(4, Date.valueOf(p.getDateSoutenance()));
                    }
                    ps.setInt(5, p.getIdEtudiant());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("No generated key for PFE");
                        idPfe = rs.getInt(1);
                    }
                }

                replaceEncadreurs(cn, idPfe, encadreurIds);

                cn.commit();
                return idPfe;
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    public boolean update(Pfe p, List<Integer> encadreurIds) throws SQLException {
        if (p.getIdPfe() == null) {
            throw new IllegalArgumentException("idPfe is required for update");
        }

        String sql = "UPDATE pfe SET titre=?, description=?, etat=?, date_soutenance=?, id_etudiant=? WHERE id_pfe=?";

        try (Connection cn = DbConnection.getConnection()) {
            cn.setAutoCommit(false);
            try {
                boolean ok;
                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setString(1, p.getTitre());
                    ps.setString(2, p.getDescription());
                    ps.setString(3, p.getEtat().name());
                    if (p.getDateSoutenance() == null) {
                        ps.setNull(4, java.sql.Types.DATE);
                    } else {
                        ps.setDate(4, Date.valueOf(p.getDateSoutenance()));
                    }
                    ps.setInt(5, p.getIdEtudiant());
                    ps.setInt(6, p.getIdPfe());
                    ok = ps.executeUpdate() == 1;
                }

                replaceEncadreurs(cn, p.getIdPfe(), encadreurIds);

                cn.commit();
                return ok;
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    public boolean deleteById(int idPfe) throws SQLException {
        String sql = "DELETE FROM pfe WHERE id_pfe=?";
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPfe);
            return ps.executeUpdate() == 1;
        }
    }

    public List<Pfe> findAll() throws SQLException {
        String sql = "SELECT p.id_pfe, p.titre, p.description, p.etat, p.date_soutenance, p.id_etudiant, "
                + "e.nom AS et_nom, e.prenom AS et_prenom "
                + "FROM pfe p JOIN etudiant e ON e.id_etudiant = p.id_etudiant "
                + "ORDER BY p.id_pfe DESC";

        List<Pfe> out = new ArrayList<>();
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pfe p = new Pfe();
                p.setIdPfe(rs.getInt("id_pfe"));
                p.setTitre(rs.getString("titre"));
                p.setDescription(rs.getString("description"));
                p.setEtat(PfeEtat.valueOf(rs.getString("etat")));
                Date d = rs.getDate("date_soutenance");
                p.setDateSoutenance(d == null ? null : d.toLocalDate());
                p.setIdEtudiant(rs.getInt("id_etudiant"));
                p.setEtudiantLabel(rs.getString("et_nom") + " " + rs.getString("et_prenom") + " (ID=" + p.getIdEtudiant() + ")");
                out.add(p);
            }
        }
        return out;
    }

    public List<Integer> findEncadreurIdsByPfeId(int idPfe) throws SQLException {
        String sql = "SELECT id_encadreur FROM pfe_encadreur WHERE id_pfe=? ORDER BY id_encadreur";
        List<Integer> out = new ArrayList<>();
        try (Connection cn = DbConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPfe);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(rs.getInt(1));
            }
        }
        return out;
    }

    private void replaceEncadreurs(Connection cn, int idPfe, List<Integer> encadreurIds) throws SQLException {
        try (PreparedStatement del = cn.prepareStatement("DELETE FROM pfe_encadreur WHERE id_pfe=?")) {
            del.setInt(1, idPfe);
            del.executeUpdate();
        }
        if (encadreurIds == null || encadreurIds.isEmpty()) {
            throw new SQLException("A PFE must have at least one encadreur.");
        }
        try (PreparedStatement ins = cn.prepareStatement("INSERT INTO pfe_encadreur(id_pfe, id_encadreur) VALUES(?,?)")) {
            for (Integer idEnc : encadreurIds) {
                ins.setInt(1, idPfe);
                ins.setInt(2, idEnc);
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }
}

