package repository;

import entities.User;

import java.sql.SQLException;
import java.util.List;

public interface UserRepository {
    User create(User user) throws SQLException;

    User read(long id) throws SQLException;

    void updateProfile(User user, String newContent, String which) throws SQLException;

    User findByUsername(String username) throws SQLException;

    List<User> all();
}
