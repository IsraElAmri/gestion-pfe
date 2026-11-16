package tn.pfe.gestion.util;

import tn.pfe.gestion.util.Constants;

public final class Validators {
    private Validators() {
    }

    // Uses patterns from Constants to keep everything in one place.

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String s = email.trim();
        if (s.isEmpty()) return false;
        if (s.length() > 120) return false;
        return Constants.EMAIL_REGEX.matcher(s).matches();
    }

    public static boolean isValidSalle(String salle) {
        if (salle == null) return false;
        String s = salle.trim();
        if (s.isEmpty()) return false;
        if (s.length() > 40) return false;
        return Constants.SALLE_REGEX.matcher(s).matches();
    }
}

