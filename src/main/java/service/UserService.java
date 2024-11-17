package service;

import exceptions.UserException;

import java.sql.SQLException;

public interface UserService {
    void userSignup(String username, String password, String bio, String email, String displayName) throws SQLException;

    void userLoginUsername(String username, String password) throws SQLException, UserException;

    void userLoginEmail(String email, String password) throws SQLException, UserException;

    void userLogout();
}
