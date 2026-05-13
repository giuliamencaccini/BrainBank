package it.ispwproject.brainbank.model;

import it.ispwproject.brainbank.enumerator.Role;

public abstract class User {

    /**
     * id — IDENTIFICATORE PROPRIO dell'entità User.
     * Serve al DAO per:
     *   - identificare univocamente l'utente nelle query SQL
     *   - costruire i puntatori negli altri model (es. Booking.student)
     * NON è una FK — è l'identità di questo oggetto.
     */
    private int id;

    private String name;
    private String surname;
    private String email;
    private String password;
    private Role role;

    protected User() {}

    protected User(int id, String name, String surname, String email,
                String password, Role role) {
        this.id       = id;
        this.name     = name;
        this.surname  = surname;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    public String getFullName() {
        return name + " " + surname;
    }

    public boolean hasRole(Role role) {
        return this.role == role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}