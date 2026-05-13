package it.ispwproject.brainbank.model;

import it.ispwproject.brainbank.enumerator.Role;

public class Admin extends User {
    public Admin(int id, String name, String surname, String email, String password) {
        super(id, name, surname, email, password, Role.ADMIN);
    }
}