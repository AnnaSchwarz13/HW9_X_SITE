package repository;

import entities.User;

import java.sql.SQLException;
import java.util.List;

public interface UserRepository {
    User create(User user) throws SQLException;

    // Author read(int id) throws SQLException;

    void delete(long id) throws SQLException;

    void setUpdatePassword(User user, String password) throws SQLException;

    User findByUserId(long userId) throws SQLException;
    User create(User article) throws SQLException;
    void delete(long id) throws SQLException;
    User findByUsername(String username) throws SQLException;
    List<User> all();
}
