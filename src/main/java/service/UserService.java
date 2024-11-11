package service;

import java.sql.SQLException;

public interface UserService {
    void userSignup(String username,String password, String bio , String email , String displayName) throws SQLException;
    void changeProfile() throws SQLException;
    void userLoginUsername(String username, String password) throws SQLException;
    void userLoginEmail(String email, String password) throws SQLException;
    void userLogout();
}
