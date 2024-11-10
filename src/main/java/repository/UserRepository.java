package repository;

import entities.User;

import java.sql.SQLException;
import java.util.List;

public interface UserRepository {
    User create(User user) throws SQLException;
    void setUpdatePassword(User user, String password) throws SQLException;
    User findByUsername(String username) throws SQLException;
    List<User> all();
}
