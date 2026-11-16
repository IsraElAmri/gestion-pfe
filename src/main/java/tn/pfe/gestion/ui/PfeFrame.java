package tn.pfe.gestion.ui;

import tn.pfe.gestion.dao.EncadreurDao;
import tn.pfe.gestion.dao.EtudiantDao;
import tn.pfe.gestion.dao.PfeDao;
import tn.pfe.gestion.model.Encadreur;
import tn.pfe.gestion.model.Etudiant;
import tn.pfe.gestion.model.Pfe;
import tn.pfe.gestion.model.PfeEtat;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import tn.pfe.gestion.util.ErrorHandler;

public final class PfeFrame extends JFrame {
    private final PfeDao pfeDao = new PfeDao();
    private final EtudiantDao etudiantDao = new EtudiantDao();
    private final EncadreurDao encadreurDao = new EncadreurDao();

    private final JTextField tfTitre = new JTextField(22);
    private final JTextArea taDescription = new JTextArea(3, 22);
    private final JComboBox<PfeEtat> cbEtat = new JComboBox<>(PfeEtat.values());
    private final JTextField tfDate = new JTextField(10); // yyyy-mm-dd
    private final JComboBox<IdLabel> cbEtudiant = new JComboBox<>();

    private final DefaultListModel<IdLabel> encListModel = new DefaultListModel<>();
    private final JList<IdLabel> listEncadreurs = new JList<>(encListModel);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Titre", "État", "Date sout.", "Étudiant"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public PfeFrame() {
        super("Gestion des PFE");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildActionsPanel(), BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelection();
        });

        listEncadreurs.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listEncadreurs.setVisibleRowCount(10);

        reloadReferenceData();
        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("PFE"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;
        c.gridy = 0;

        c.gridx = 0;
        p.add(new JLabel("Titre"), c);
        c.gridx = 1;
        p.add(tfTitre, c);

        c.gridx = 2;
        p.add(new JLabel("État"), c);
        c.gridx = 3;
        p.add(cbEtat, c);

        c.gridy++;
        c.gridx = 0;
        p.add(new JLabel("Date soutenance (yyyy-mm-dd)"), c);
        c.gridx = 1;
        p.add(tfDate, c);

        c.gridx = 2;
        p.add(new JLabel("Étudiant"), c);
        c.gridx = 3;
        p.add(cbEtudiant, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 1;
        p.add(new JLabel("Description"), c);
        c.gridx = 1;
        c.gridwidth = 3;
        taDescription.setLineWrap(true);
        taDescription.setWrapStyleWord(true);
        p.add(new JScrollPane(taDescription), c);

        return p;
    }

    private JPanel buildCenterPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Encadreurs (multi-sélection)"));
        left.add(new JScrollPane(listEncadreurs), BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(320, 240));

        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createTitledBorder("Liste des PFE"));
        right.add(new JScrollPane(table), BorderLayout.CENTER);

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildActionsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnReload = new JButton("Recharger listes");
        JButton btnRefresh = new JButton("Rafraîchir");
        JButton btnClear = new JButton("Vider");
        JButton btnAdd = new JButton("Ajouter");
        JButton btnUpdate = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");

        btnReload.addActionListener(e -> reloadReferenceData());
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

    private void reloadReferenceData() {
        try {
            cbEtudiant.removeAllItems();
            for (Etudiant e : etudiantDao.findAll()) {
                cbEtudiant.addItem(new IdLabel(e.getIdEtudiant(), e.getNom() + " " + e.getPrenom() + " (ID=" + e.getIdEtudiant() + ")"));
            }

            encListModel.clear();
            for (Encadreur enc : encadreurDao.findAll()) {
                encListModel.addElement(new IdLabel(enc.getIdEncadreur(), enc.getNom() + " " + enc.getPrenom() + " — " + enc.getGrade() + " (ID=" + enc.getIdEncadreur() + ")"));
            }
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private void refreshTable() {
        try {
            List<Pfe> list = pfeDao.findAll();
            tableModel.setRowCount(0);
            for (Pfe p : list) {
                tableModel.addRow(new Object[]{
                        p.getIdPfe(),
                        p.getTitre(),
                        p.getEtat().name(),
                        p.getDateSoutenance() == null ? "" : p.getDateSoutenance().toString(),
                        p.getEtudiantLabel()
                });
            }
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private void onAdd() {
        Pfe p = readForm(null);
        if (p == null) return;
        List<Integer> encIds = getSelectedEncadreurIds();
        if (encIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sélectionne au moins un encadreur.");
            return;
        }
        try {
            int id = pfeDao.insert(p, encIds);
            JOptionPane.showMessageDialog(this, "PFE ajouté (ID=" + id + ")");
            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private void onUpdate() {
        Integer id = getSelectedPfeId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Sélectionne un PFE dans le tableau.");
            return;
        }
        Pfe p = readForm(id);
        if (p == null) return;
        List<Integer> encIds = getSelectedEncadreurIds();
        if (encIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sélectionne au moins un encadreur.");
            return;
        }
        try {
            boolean ok = pfeDao.update(p, encIds);
            JOptionPane.showMessageDialog(this, ok ? "PFE modifié." : "Aucune ligne modifiée.");
            refreshTable();
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private void onDelete() {
        Integer id = getSelectedPfeId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Sélectionne un PFE dans le tableau.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer le PFE ID=" + id + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = pfeDao.deleteById(id);
            JOptionPane.showMessageDialog(this, ok ? "Supprimé." : "Aucune ligne supprimée.");
            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private Pfe readForm(Integer id) {
        String titre = tfTitre.getText().trim();
        String desc = taDescription.getText().trim();
        PfeEtat etat = (PfeEtat) cbEtat.getSelectedItem();
        IdLabel etu = (IdLabel) cbEtudiant.getSelectedItem();
        String dateTxt = tfDate.getText().trim();

        if (titre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Titre obligatoire.");
            return null;
        }
        if (etat == null) {
            JOptionPane.showMessageDialog(this, "État obligatoire.");
            return null;
        }
        if (etu == null) {
            JOptionPane.showMessageDialog(this, "Sélectionne un étudiant.");
            return null;
        }

        LocalDate date = null;
        if (!dateTxt.isEmpty()) {
            try {
                date = LocalDate.parse(dateTxt);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Format date invalide. Utilise yyyy-mm-dd (ex: 2026-06-15)");
                return null;
            }
        }

        Pfe p = new Pfe();
        p.setIdPfe(id);
        p.setTitre(titre);
        p.setDescription(desc);
        p.setEtat(etat);
        p.setDateSoutenance(date);
        p.setIdEtudiant(etu.getId());
        return p;
    }

    private void fillFormFromSelection() {
        Integer id = getSelectedPfeId();
        if (id == null) return;

        int row = table.getSelectedRow();
        tfTitre.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        cbEtat.setSelectedItem(PfeEtat.valueOf(String.valueOf(tableModel.getValueAt(row, 2))));
        tfDate.setText(String.valueOf(tableModel.getValueAt(row, 3)));

        // pick student in combo by parsing "(ID=xx)"
        String etuLabel = String.valueOf(tableModel.getValueAt(row, 4));
        int idEtu = parseIdFromLabel(etuLabel);
        selectComboById(cbEtudiant, idEtu);

        // description + encadreurs: load from DB for this PFE
        try {
            // load description by refreshing list and matching id
            for (Pfe p : pfeDao.findAll()) {
                if (p.getIdPfe() != null && p.getIdPfe() == id) {
                    taDescription.setText(p.getDescription() == null ? "" : p.getDescription());
                    break;
                }
            }
            List<Integer> encIds = pfeDao.findEncadreurIdsByPfeId(id);
            selectEncadreursByIds(encIds);
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private int parseIdFromLabel(String label) {
        
        int idx = label.lastIndexOf("ID=");
        if (idx < 0) return -1;
        int end = label.indexOf(')', idx);
        if (end < 0) end = label.length();
        try {
            return Integer.parseInt(label.substring(idx + 3, end));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void selectComboById(JComboBox<IdLabel> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            IdLabel it = combo.getItemAt(i);
            if (it != null && it.getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectEncadreursByIds(List<Integer> encIds) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < encListModel.getSize(); i++) {
            IdLabel it = encListModel.getElementAt(i);
            if (encIds.contains(it.getId())) {
                indices.add(i);
            }
        }
        int[] sel = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) sel[i] = indices.get(i);
        listEncadreurs.setSelectedIndices(sel);
    }

    private Integer getSelectedPfeId() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        Object v = tableModel.getValueAt(row, 0);
        if (v == null) return null;
        return Integer.parseInt(String.valueOf(v));
    }

    private List<Integer> getSelectedEncadreurIds() {
        List<Integer> ids = new ArrayList<>();
        for (IdLabel it : listEncadreurs.getSelectedValuesList()) {
            ids.add(it.getId());
        }
        return ids;
    }

    private void clearForm() {
        tfTitre.setText("");
        taDescription.setText("");
        cbEtat.setSelectedItem(PfeEtat.EN_COURS);
        tfDate.setText("");
        table.clearSelection();
        listEncadreurs.clearSelection();
    }

}

