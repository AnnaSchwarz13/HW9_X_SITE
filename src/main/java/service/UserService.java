package service;

import java.sql.Date;
import java.sql.SQLException;

public interface UserService {
    void userSignup(String firstName, String lastName, String username,String password, String nationalCode, Date birthday) throws SQLException;
    void changePassword(String oldPassword, String newPassword) throws SQLException;
    void userLogin(String username, String password, Role role) throws SQLException;
    void userLogout();
}
