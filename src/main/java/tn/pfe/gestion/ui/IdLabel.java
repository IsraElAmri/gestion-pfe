package tn.pfe.gestion.ui;

/**
 * Small helper for combo/list items: stores an integer ID and a display label.
 */
public final class IdLabel {
    private final int id;
    private final String label;

    public IdLabel(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return label;
    }
}

