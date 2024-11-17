package service;

import exceptions.UserException;

import java.sql.SQLException;

public interface UserService {
    void userSignup(String username, String password, String bio, String email, String displayName) throws SQLException;

    void userLoginUsername(String username, String password) throws SQLException, UserException;

    void userLoginEmail(String email, String password) throws SQLException, UserException;

    void userLogout();

    void changePassword(String oldPassword, String newPassword) throws SQLException, UserException;

    void changeUsername(String oldUsername, String newUsername) throws SQLException, UserException;

    void changeDisplayName(String displayName) throws SQLException;

    void changeEmail(String oldEmail, String newEmail) throws SQLException, UserException;

    void changeBio(String newBio) throws SQLException;
}
