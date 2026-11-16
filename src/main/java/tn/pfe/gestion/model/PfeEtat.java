package tn.pfe.gestion.model;

public enum PfeEtat {
    EN_COURS,
    VALIDE,
    SOUTENU,
    REFUSE;

    @Override
    public String toString() {
        // nicer display in UI
        return name().replace('_', ' ');
    }
}

