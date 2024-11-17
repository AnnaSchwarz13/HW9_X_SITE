package repository;

import entities.User;

import java.sql.SQLException;
import java.util.List;

public interface UserRepository {
    User create(User user) throws SQLException;

    User read(long id) throws SQLException;

    User findByUsername(String username) throws SQLException;

    boolean isUsernameExist(String username) throws SQLException;

    boolean isEmailExist(String email) throws SQLException;

    List<User> all() throws SQLException;

    void updateBio(long id, String bio) throws SQLException;

    void updateDisplayName(long id, String displayName) throws SQLException;

    void updateEmail(long id, String email) throws SQLException;

    void updateUsername(long id, String username) throws SQLException;

    void updatePassword(long id, String password) throws SQLException;
}
