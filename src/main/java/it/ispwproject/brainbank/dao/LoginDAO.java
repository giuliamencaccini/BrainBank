package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.exception.LoginException;
import it.ispwproject.brainbank.model.Credentials;

public interface LoginDAO {
    Credentials execute(String email, String password) throws LoginException;
}