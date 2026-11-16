package tn.pfe.gestion.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class Soutenance {
    private Integer idSoutenance;
    private LocalDateTime date;
    private String salle;
    private BigDecimal note;
    private Integer idPfe;

    // for UI listing
    private String pfeLabel;

    public Integer getIdSoutenance() {
        return idSoutenance;
    }

    public void setIdSoutenance(Integer idSoutenance) {
        this.idSoutenance = idSoutenance;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }

    public BigDecimal getNote() {
        return note;
    }

    public void setNote(BigDecimal note) {
        this.note = note;
    }

    public Integer getIdPfe() {
        return idPfe;
    }

    public void setIdPfe(Integer idPfe) {
        this.idPfe = idPfe;
    }

    public String getPfeLabel() {
        return pfeLabel;
    }

    public void setPfeLabel(String pfeLabel) {
        this.pfeLabel = pfeLabel;
    }
}

