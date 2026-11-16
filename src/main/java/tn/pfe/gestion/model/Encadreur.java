package tn.pfe.gestion.model;

public final class Encadreur {
    private Integer idEncadreur;
    private String nom;
    private String prenom;
    private String grade;
    private String email;

    public Encadreur() {
    }

    public Encadreur(Integer idEncadreur, String nom, String prenom, String grade, String email) {
        this.idEncadreur = idEncadreur;
        this.nom = nom;
        this.prenom = prenom;
        this.grade = grade;
        this.email = email;
    }

    public Integer getIdEncadreur() {
        return idEncadreur;
    }

    public void setIdEncadreur(Integer idEncadreur) {
        this.idEncadreur = idEncadreur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

