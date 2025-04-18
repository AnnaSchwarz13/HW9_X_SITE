package repository.Imp;

import entities.User;
import config.Datasource;
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
            WHERE id = ?
            """;
    private static final String UPDATE_USERNAME_SQL = """
            UPDATE x_site_users
            SET username = ?
            WHERE id = ?
            """;
    private static final String UPDATE_EMAIL_SQL = """
            UPDATE x_site_users
            SET email = ?
            WHERE id = ?
            """;
    private static final String UPDATE_BIO_SQL = """
            UPDATE x_site_users
            SET bio = ?
            WHERE id = ?
            """;
    private static final String UPDATE_DISPLAY_NAME_SQL = """
            UPDATE x_site_users
            SET display_name = ?
            WHERE id = ?
            """;
    private static final String FIND_ID_BY_USERNAME_SQL = """
            SELECT id FROM x_site_users
            WHERE username = ?
            """;
    private static final String READ_ALL_SQL = """
            SELECT * FROM x_site_users
            """;
    private static final String IS_USERNAME_NEW = """
            SELECT id FROM x_site_users
            WHERE username = ?
            """;
    private static final String IS_EMAIL_NEW = """
            SELECT id FROM x_site_users
            WHERE email = ?
            """;

    //create
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

    //read
    @Override
    public User read(long id) throws SQLException {
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

    @Override
    public List<User> all() throws SQLException{
        try (var statement = Datasource.getConnection().prepareStatement(READ_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<User> users = new LinkedList<>();
            while (resultSet.next()) {
                User user = read(resultSet.getLong(1));
                users.add(user);
            }

            return new ArrayList<>(users);
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

    @Override
    public boolean isUsernameExist(String username) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(IS_USERNAME_NEW)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    @Override
    public boolean isEmailExist(String email) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(IS_EMAIL_NEW)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    //update
    @Override
    public void updateBio(long id, String bio) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_BIO_SQL)) {
            statement.setString(1, bio);
            statement.setLong(2, id);
            statement.executeUpdate();
        }
    }

    @Override
    public void updateDisplayName(long id, String displayName) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_DISPLAY_NAME_SQL)) {
            statement.setString(1, displayName);
            statement.setLong(2, id);
            statement.executeUpdate();
        }
    }

    @Override
    public void updateEmail(long id, String email) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_EMAIL_SQL)) {
            statement.setString(1, email);
            statement.setLong(2, id);
            statement.executeUpdate();
        }
    }

    @Override
    public void updateUsername(long id, String username) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_USERNAME_SQL)) {
            statement.setString(1, username);
            statement.setLong(2, id);
            statement.executeUpdate();
        }
    }

    @Override
    public void updatePassword(long id, String password) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_PASSWORD_SQL)) {
            statement.setString(1, password);
            statement.setLong(2, id);
            statement.executeUpdate();
        }
    }
}
