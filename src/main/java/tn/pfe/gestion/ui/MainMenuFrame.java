package tn.pfe.gestion.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public final class MainMenuFrame extends JFrame {
    public MainMenuFrame() {
        super("Gestion des PFE — Menu");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(520, 320));
        setLocationRelativeTo(null);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildButtons(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        JLabel title = new JLabel("Application de Gestion des PFE");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JLabel subtitle = new JLabel("Choisis un module");
        subtitle.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        p.add(title, BorderLayout.NORTH);
        p.add(subtitle, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildButtons() {
        JPanel p = new JPanel(new GridLayout(2, 2, 12, 12));
        p.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

        JButton btnEtudiants = new JButton("Étudiants");
        JButton btnEncadreurs = new JButton("Encadreurs");
        JButton btnPfe = new JButton("PFE");
        JButton btnSoutenance = new JButton("Soutenances");

        btnEtudiants.addActionListener(e -> new EtudiantFrame().setVisible(true));
        btnEncadreurs.addActionListener(e -> new EncadreurFrame().setVisible(true));
        btnPfe.addActionListener(e -> new PfeFrame().setVisible(true));
        btnSoutenance.addActionListener(e -> new SoutenanceFrame().setVisible(true));

        p.add(btnEtudiants);
        p.add(btnEncadreurs);
        p.add(btnPfe);
        p.add(btnSoutenance);
        return p;
    }
}


