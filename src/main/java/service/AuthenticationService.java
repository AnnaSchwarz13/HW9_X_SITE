package service;


import entities.User;
import exceptions.TweetException;
import exceptions.UserException;

import java.sql.SQLException;

public interface AuthenticationService {
    void setLoggedUser(User user);

    User getLoggedUser();

    void logout();

    boolean isUsernameNew(String username) throws SQLException, UserException;

    boolean isEmailNew(String email) throws SQLException, UserException;

    boolean isTweetForLoggedInUser(Long id) throws SQLException, TweetException;
}
