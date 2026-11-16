package tn.pfe.gestion.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class Pfe {
    private Integer idPfe;
    private String titre;
    private String description;
    private PfeEtat etat;
    private LocalDate dateSoutenance;
    private Integer idEtudiant;

    // convenience for UI listing
    private String etudiantLabel;
    private final List<Integer> encadreurIds = new ArrayList<>();

    public Integer getIdPfe() {
        return idPfe;
    }

    public void setIdPfe(Integer idPfe) {
        this.idPfe = idPfe;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PfeEtat getEtat() {
        return etat;
    }

    public void setEtat(PfeEtat etat) {
        this.etat = etat;
    }

    public LocalDate getDateSoutenance() {
        return dateSoutenance;
    }

    public void setDateSoutenance(LocalDate dateSoutenance) {
        this.dateSoutenance = dateSoutenance;
    }

    public Integer getIdEtudiant() {
        return idEtudiant;
    }

    public void setIdEtudiant(Integer idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    public String getEtudiantLabel() {
        return etudiantLabel;
    }

    public void setEtudiantLabel(String etudiantLabel) {
        this.etudiantLabel = etudiantLabel;
    }

    public List<Integer> getEncadreurIds() {
        return encadreurIds;
    }
}

