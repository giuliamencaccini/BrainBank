package it.ispwproject.brainbank.model;

import it.ispwproject.brainbank.enumerator.Role;

/**
 * Credentials — oggetto TEMPORANEO usato solo durante il login.
 * NON è una entity persistente — non ha id perché non viene salvata nel DB.
 * Contiene solo i dati minimi restituiti dalla stored procedure login:
 * email, password hashata, ruolo.
 * Viene creata in LoginDAO e consumata in LoginController.
 */
public class Credentials {

    private String email;
    private String password;
    private Role   role;

    public Credentials(String email, String password, Role role) {
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public Role   getRole()     { return role; }
}
