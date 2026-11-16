package tn.pfe.gestion.ui;

import tn.pfe.gestion.dao.PfeDao;
import tn.pfe.gestion.dao.SoutenanceDao;
import tn.pfe.gestion.model.Pfe;
import tn.pfe.gestion.model.Soutenance;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import tn.pfe.gestion.util.Validators;
import tn.pfe.gestion.util.Constants;
import tn.pfe.gestion.util.ErrorHandler;

public final class SoutenanceFrame extends JFrame {
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final SoutenanceDao dao = new SoutenanceDao();
    private final PfeDao pfeDao = new PfeDao();

    private final JTextField tfDateTime = new JTextField(16);
    private final JTextField tfSalle = new JTextField(12);
    private final JTextField tfNote = new JTextField(6);
    private final JComboBox<IdLabel> cbPfe = new JComboBox<>();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Date", "Salle", "Note", "PFE"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public SoutenanceFrame() {
        super("Gestion des soutenances");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(980, 520));
        setLocationRelativeTo(null);

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildActionsPanel(), BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelection();
        });

        tfDateTime.setToolTipText("Format: yyyy-MM-dd HH:mm (ex: 2026-06-15 10:30)");
        tfSalle.setToolTipText("Bloc A ou B (ex: A ou B)");

        reloadPfeCombo();
        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Soutenance"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;
        c.gridy = 0;

        c.gridx = 0;
        p.add(new JLabel("Date/Heure (yyyy-MM-dd HH:mm)"), c);
        c.gridx = 1;
        p.add(tfDateTime, c);

        c.gridx = 2;
        p.add(new JLabel("Salle"), c);
        c.gridx = 3;
        p.add(tfSalle, c);

        c.gridy++;
        c.gridx = 0;
        p.add(new JLabel("Note (optionnel)"), c);
        c.gridx = 1;
        p.add(tfNote, c);

        c.gridx = 2;
        p.add(new JLabel("PFE"), c);
        c.gridx = 3;
        p.add(cbPfe, c);

        return p;
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Liste des soutenances"));
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildActionsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnReload = new JButton("Recharger PFEs");
        JButton btnRefresh = new JButton("Rafraîchir");
        JButton btnClear = new JButton("Vider");
        JButton btnAdd = new JButton("Ajouter");
        JButton btnUpdate = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");

        btnReload.addActionListener(e -> reloadPfeCombo());
        btnRefresh.addActionListener(e -> refreshTable());
        btnClear.addActionListener(e -> clearForm());
        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());

        p.add(btnReload);
        p.add(btnRefresh);
        p.add(btnClear);
        p.add(btnDelete);
        p.add(btnUpdate);
        p.add(btnAdd);
        return p;
    }

    private void reloadPfeCombo() {
        try {
            cbPfe.removeAllItems();
            for (Pfe p : pfeDao.findAll()) {
                cbPfe.addItem(new IdLabel(p.getIdPfe(), "PFE " + p.getIdPfe() + ": " + p.getTitre() + " — " + p.getEtudiantLabel()));
            }
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private void refreshTable() {
        try {
            List<Soutenance> list = dao.findAll();
            tableModel.setRowCount(0);
            for (Soutenance s : list) {
                tableModel.addRow(new Object[]{
                        s.getIdSoutenance(),
                        s.getDate() == null ? "" : DT.format(s.getDate()),
                        s.getSalle(),
                        s.getNote() == null ? "" : s.getNote().toPlainString(),
                        s.getPfeLabel()
                });
            }
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private void onAdd() {
        Soutenance s = readForm(null);
        if (s == null) return;
        try {
            int id = dao.insert(s);
            JOptionPane.showMessageDialog(this, "Soutenance ajoutée (ID=" + id + ")");
            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private void onUpdate() {
        Integer id = getSelectedId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Sélectionne une soutenance dans le tableau.");
            return;
        }
        Soutenance s = readForm(id);
        if (s == null) return;
        try {
            boolean ok = dao.update(s);
            JOptionPane.showMessageDialog(this, ok ? "Modifiée." : "Aucune ligne modifiée.");
            refreshTable();
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private void onDelete() {
        Integer id = getSelectedId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Sélectionne une soutenance dans le tableau.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer la soutenance ID=" + id + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = dao.deleteById(id);
            JOptionPane.showMessageDialog(this, ok ? "Supprimée." : "Aucune ligne supprimée.");
            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private Soutenance readForm(Integer id) {
        String dtTxt = tfDateTime.getText().trim();
        String salle = tfSalle.getText().trim();
        String noteTxt = tfNote.getText().trim();
        IdLabel pfe = (IdLabel) cbPfe.getSelectedItem();

        if (dtTxt.isEmpty() || salle.isEmpty() || pfe == null) {
            JOptionPane.showMessageDialog(this, "Date/heure, salle, et PFE sont obligatoires.");
            return null;
        }

        LocalDateTime dt;
        try {
            dt = LocalDateTime.parse(dtTxt, DT);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Format date/heure invalide. Utilise yyyy-MM-dd HH:mm (ex: 2026-06-15 10:30)");
            return null;
        }

        BigDecimal note = null;
        if (!noteTxt.isEmpty()) {
            try {
                note = new BigDecimal(noteTxt);
                if (note.compareTo(new BigDecimal(Constants.NOTE_MIN)) < 0
                        || note.compareTo(new BigDecimal(Constants.NOTE_MAX)) > 0) {
                    JOptionPane.showMessageDialog(this, "Note doit être entre " + Constants.NOTE_MIN + " et " + Constants.NOTE_MAX + ".");
                    return null;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Note invalide (ex: 15.5)");
                return null;
            }
        }

        if (!Validators.isValidSalle(salle)) {
            JOptionPane.showMessageDialog(this, "Salle invalide. Utilise uniquement A ou B.");
            return null;
        }

        Soutenance s = new Soutenance();
        s.setIdSoutenance(id);
        s.setDate(dt);
        s.setSalle(salle);
        s.setNote(note);
        s.setIdPfe(pfe.getId());
        return s;
    }

    private void fillFormFromSelection() {
        Integer id = getSelectedId();
        if (id == null) return;
        int row = table.getSelectedRow();
        tfDateTime.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        tfSalle.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        tfNote.setText(String.valueOf(tableModel.getValueAt(row, 3)));
    }

    private void clearForm() {
        tfDateTime.setText("");
        tfSalle.setText("");
        tfNote.setText("");
        table.clearSelection();
    }

    private Integer getSelectedId() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        Object v = tableModel.getValueAt(row, 0);
        if (v == null) return null;
        return Integer.parseInt(String.valueOf(v));
    }

}

