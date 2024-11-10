package service;

import java.sql.SQLException;

public interface UserService {
    void userSignup(String username,String password, String bio , String email , String displayName) throws SQLException;
    void changePassword(String oldPassword, String newPassword) throws SQLException;
    void changeProfile();
    void userLogin(String username, String password) throws SQLException;
    void userLogout();
}
