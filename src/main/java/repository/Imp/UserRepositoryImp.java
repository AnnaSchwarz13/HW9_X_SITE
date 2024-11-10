package repository.Imp;

import entities.User;
import repository.Datasource;
import repository.UserRepository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserRepositoryImp implements UserRepository {

    private static final String INSERT_SQL = """
            INSERT INTO x_site_users(username,password, bio,email,creation_date,display_name)
                    VALUES (?, ?, ? ,? ,?,?)
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT * FROM x_site_users
            WHERE id = ?
            """;
    private static final String UPDATE_PASSWORD_SQL = """
            UPDATE x_site_users
            SET password = ?
            WHERE username = ?
            """;
    private static final String FIND_ID_BY_USERNAME_SQL = """
            SELECT id FROM x_site_users
            WHERE username = ?
            """;

    public static final String READ_ALL_SQL = """
            SELECT * FROM x_site_users
            """;

    @Override
    public User create(User user) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(INSERT_SQL)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getBio());
            statement.setString(4, user.getEmail());
            statement.setDate(5, Date.valueOf(LocalDate.now()));
            statement.setString(6, user.getDisplayName());

            statement.executeUpdate();

            return user;
        }
    }

    public static User read(long id) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            User user = null;
            if (resultSet.next()) {
                long uerId = resultSet.getLong(1);
                String username = resultSet.getString(2);
                String password = resultSet.getString(3);
                String userEmail = resultSet.getString(4);
                String bio = resultSet.getString(5);
                String displayName = resultSet.getString(6);
                Date createdDate = resultSet.getDate(7);
                user = new User(username, password, uerId, displayName, userEmail, bio, createdDate);
            }

            return user;
        }
    }

    //update
    @Override
    public void setUpdatePassword(User user, String password) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_PASSWORD_SQL)) {
            statement.setString(1, password);
            statement.setString(2, user.getUsername());
            statement.executeUpdate();
        }
    }


    @Override
    public List<User> all() {
        try (var statement = Datasource.getConnection().prepareStatement(READ_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<User> users = new LinkedList<>();
            while (resultSet.next()) {

                User user = read(resultSet.getLong(1));
                users.add(user);
            }

            return new ArrayList<>(users);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public User findByUsername(String username) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_ID_BY_USERNAME_SQL)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            User user = null;
            if (resultSet.next()) {
                user = read(resultSet.getLong(1));
            }
            return user;
        }
    }
}
