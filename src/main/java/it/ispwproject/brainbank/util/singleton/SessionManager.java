package it.ispwproject.brainbank.util.singleton;

import it.ispwproject.brainbank.bean.SessionBean;
import it.ispwproject.brainbank.enumerator.Role;
import it.ispwproject.brainbank.model.User;

public class SessionManager {

    private User loggedUser;
    private SessionBean sessionBean;

    private SessionManager() {}

    private static class Holder {
        private static final SessionManager INSTANCE = new SessionManager();
    }

    public static SessionManager getInstance() {
        return Holder.INSTANCE;
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public boolean isLoggedIn() {
        return loggedUser != null;
    }

    public boolean isStudent() {
        return isLoggedIn() && loggedUser.hasRole(Role.STUDENT);
    }

    public boolean isTutor() {
        return isLoggedIn() && loggedUser.hasRole(Role.TUTOR);
    }

    public boolean isAdmin() {
        return isLoggedIn() && loggedUser.hasRole(Role.ADMIN);
    }

    public void clearSession() {
        this.loggedUser  = null;
        this.sessionBean = null;
    }
}