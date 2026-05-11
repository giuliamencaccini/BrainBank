package it.ispwproject.brainbank.controller.demo;

import it.ispwproject.brainbank.bean.SessionBean;
import it.ispwproject.brainbank.controller.applicativo.LoginController;
import it.ispwproject.brainbank.enumerator.Role;
import it.ispwproject.brainbank.exception.LoginException;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.model.Tutor;
import it.ispwproject.brainbank.util.singleton.SessionManager;

public class LoginControllerDemo extends LoginController {

    @Override
    public LoginResult login(String email, String password) throws LoginException {
        return switch (email.toLowerCase()) {
            case "student@demo" -> {
                Student student = new Student(1, "Demo", "Student", email, null);
                SessionManager.getInstance().setLoggedUser(student);
                SessionManager.getInstance().setSessionBean(
                        new SessionBean(email, Role.STUDENT)
                );
                yield LoginResult.SUCCESSO_STUDENT;
            }
            case "tutor@demo" -> {
                Tutor tutor = new Tutor(2, "Demo", "Tutor", email, null, "Tutor demo");
                SessionManager.getInstance().setLoggedUser(tutor);
                SessionManager.getInstance().setSessionBean(
                        new SessionBean(email, Role.TUTOR)
                );
                yield LoginResult.SUCCESSO_TUTOR;
            }
            default -> throw new LoginException("Credenziali demo non valide. Usa student@demo o tutor@demo.");
        };
    }
}