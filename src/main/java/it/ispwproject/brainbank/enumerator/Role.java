package it.ispwproject.brainbank.enumerator;

public enum Role {
    STUDENT,
    TUTOR,
    ADMIN;

    public static Role fromString(String role) {
        return switch (role.toUpperCase()) {
            case "STUDENT" -> STUDENT;
            case "TUTOR" -> TUTOR;
            case "ADMIN" -> ADMIN;
            default -> throw new IllegalArgumentException(
                    "Ruolo non valido: " + role);
        };
    }
}
