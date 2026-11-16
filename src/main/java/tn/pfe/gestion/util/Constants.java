package tn.pfe.gestion.util;

import java.util.regex.Pattern;


public final class Constants {
    private Constants() {
    }

    // Validation
    public static final Pattern EMAIL_REGEX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );
    public static final Pattern SALLE_REGEX = Pattern.compile("^[AB]$", Pattern.CASE_INSENSITIVE);

    // Notes
    public static final int NOTE_MIN = 0;
    public static final int NOTE_MAX = 20;
}

