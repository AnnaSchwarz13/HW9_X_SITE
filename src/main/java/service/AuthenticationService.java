package service;


import entities.User;

import java.sql.SQLException;

public interface AuthenticationService {
    void setLoggedUser(User user);

    User getLoggedUser();

    void logout();

    boolean isUsernameNew(String username) throws SQLException;

    boolean isEmailNew(String email) throws SQLException;
}
