package it.ispwproject.brainbank.model;

import it.ispwproject.brainbank.enumerator.Role;

public class Tutor extends User {

    /**
     * bio — attributo proprio del Tutor.
     * Nessun ID aggiuntivo — Tutor eredita id da User.
     * Non ha FK verso altre entità.
     */
    private String bio;

    public Tutor() {
        super();
    }

    public Tutor(int id, String name, String surname,
                 String email, String password, String bio) {
        super(id, name, surname, email, password, Role.TUTOR);
        this.bio = bio;
    }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}