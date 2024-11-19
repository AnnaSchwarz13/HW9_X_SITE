package service;

import exceptions.UserException;

import java.sql.SQLException;

public interface UserService {
    void userSignup(String username, String password, String bio, String email, String displayName) ;

    void userLoginUsername(String username, String password) throws UserException;

    void userLoginEmail(String email, String password) throws  UserException;

    void userLogout();

    void changePassword(String oldPassword, String newPassword) throws  UserException;

    void changeUsername(String oldUsername, String newUsername) throws UserException;

    void changeDisplayName(String displayName) ;

    void changeEmail(String oldEmail, String newEmail) throws  UserException;

    void changeBio(String newBio) ;
}
