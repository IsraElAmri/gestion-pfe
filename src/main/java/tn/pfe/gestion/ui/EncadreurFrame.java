package tn.pfe.gestion.ui;

import tn.pfe.gestion.dao.EncadreurDao;
import tn.pfe.gestion.model.Encadreur;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import java.sql.SQLException;
import java.util.List;

import tn.pfe.gestion.util.Validators;
import tn.pfe.gestion.util.ErrorHandler;

public final class EncadreurFrame extends JFrame {
    private final EncadreurDao dao = new EncadreurDao();

    private final JTextField tfNom = new JTextField(18);
    private final JTextField tfPrenom = new JTextField(18);
    private final JTextField tfGrade = new JTextField(18);
    private final JTextField tfEmail = new JTextField(18);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nom", "Prénom", "Grade", "Email"},
            0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public EncadreurFrame() {
        super("Gestion des encadreurs");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 520));
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

        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Encadreur"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;
        c.gridy = 0;

        c.gridx = 0;
        p.add(new JLabel("Nom"), c);
        c.gridx = 1;
        p.add(tfNom, c);

        c.gridx = 2;
        p.add(new JLabel("Prénom"), c);
        c.gridx = 3;
        p.add(tfPrenom, c);

        c.gridy++;
        c.gridx = 0;
        p.add(new JLabel("Grade"), c);
        c.gridx = 1;
        p.add(tfGrade, c);

        c.gridx = 2;
        p.add(new JLabel("Email"), c);
        c.gridx = 3;
        p.add(tfEmail, c);

        return p;
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Liste des encadreurs"));
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildActionsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnAdd = new JButton("Ajouter");
        JButton btnUpdate = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");
        JButton btnClear = new JButton("Vider");
        JButton btnRefresh = new JButton("Rafraîchir");

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> refreshTable());

        p.add(btnRefresh);
        p.add(btnClear);
        p.add(btnDelete);
        p.add(btnUpdate);
        p.add(btnAdd);
        return p;
    }

    private void onAdd() {
        Encadreur e = readForm(null);
        if (e == null) return;
        try {
            int id = dao.insert(e);
            JOptionPane.showMessageDialog(this, "Ajouté (ID=" + id + ")");
            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private void onUpdate() {
        Integer id = getSelectedId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Sélectionne un encadreur dans le tableau.");
            return;
        }
        Encadreur e = readForm(id);
        if (e == null) return;
        try {
            boolean ok = dao.update(e);
            JOptionPane.showMessageDialog(this, ok ? "Modifié." : "Aucune ligne modifiée.");
            refreshTable();
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private void onDelete() {
        Integer id = getSelectedId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Sélectionne un encadreur dans le tableau.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer l’encadreur ID=" + id + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = dao.deleteById(id);
            JOptionPane.showMessageDialog(this, ok ? "Supprimé." : "Aucune ligne supprimée.");
            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private Encadreur readForm(Integer id) {
        String nom = tfNom.getText().trim();
        String prenom = tfPrenom.getText().trim();
        String grade = tfGrade.getText().trim();
        String email = tfEmail.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || grade.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.");
            return null;
        }

        if (!Validators.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email invalide (ex: prenom.nom@exemple.com)");
            return null;
        }
        Encadreur e = new Encadreur();
        e.setIdEncadreur(id);
        e.setNom(nom);
        e.setPrenom(prenom);
        e.setGrade(grade);
        e.setEmail(email);
        return e;
    }

    private void clearForm() {
        tfNom.setText("");
        tfPrenom.setText("");
        tfGrade.setText("");
        tfEmail.setText("");
        table.clearSelection();
    }

    private void refreshTable() {
        try {
            List<Encadreur> list = dao.findAll();
            model.setRowCount(0);
            for (Encadreur e : list) {
                model.addRow(new Object[]{
                        e.getIdEncadreur(),
                        e.getNom(),
                        e.getPrenom(),
                        e.getGrade(),
                        e.getEmail()
                });
            }
        } catch (SQLException ex) {
            ErrorHandler.showSqlError(this, ex);
        }
    }

    private Integer getSelectedId() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        Object v = model.getValueAt(row, 0);
        if (v == null) return null;
        return Integer.parseInt(String.valueOf(v));
    }

    private void fillFormFromSelection() {
        Integer id = getSelectedId();
        if (id == null) return;
        int row = table.getSelectedRow();
        tfNom.setText(String.valueOf(model.getValueAt(row, 1)));
        tfPrenom.setText(String.valueOf(model.getValueAt(row, 2)));
        tfGrade.setText(String.valueOf(model.getValueAt(row, 3)));
        tfEmail.setText(String.valueOf(model.getValueAt(row, 4)));
    }

}

