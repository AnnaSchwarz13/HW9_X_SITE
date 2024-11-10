package repository.Imp;

import entities.User;
import repository.UserRepository;
import repository.Datasource;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserRepositoryImp implements UserRepository {
    UserRepositoryImp userRepositoryImp = new UserRepositoryImp();

    private static final String INSERT_SQL = """
            INSERT INTO Authors(firstname, lastname, birthday , national_code, user_id)
                    VALUES (?, ?, ? ,? ,?)
            """;

    private static final String DELETE_BY_ID_SQL = """
            DELETE FROM Authors
            WHERE id = ?
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT * FROM Authors
            WHERE id = ?
            """;
    private static final String FIND_AUTHOR_BY_USERID_SQL = """
            SELECT * FROM Authors
            WHERE user_id = ?
            """;
    private static final String UPDATE_PASSWORD_SQL = """
            UPDATE Users
            SET password = ?
            WHERE username = ?
            """;

    @Override
    public User create(User user) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(INSERT_SQL)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setDate(3, (Date) user.getBirthDate());
            statement.setString(4, user.getNationalCode());
            long userId = userRepositoryImp.findByUsername(user.getUsername()).getId();
            statement.setLong(5, userId);

            statement.executeUpdate();

            return user;
        }
    }

    public static User read(long id) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            User author = null;
            if (resultSet.next()) {
                long authorId = resultSet.getLong(1);
                String authorFirstname = resultSet.getString(2);
                String authorLastname = resultSet.getString(3);
                Date bithdate = resultSet.getDate(4);
                String nationalCode = resultSet.getString(5);
                int userId = resultSet.getInt(6);
                User user = UserRepositoryImp.read(userId);
                author = new User(authorFirstname, authorLastname, user.getUsername()
                        , user.getPassword(), nationalCode, bithdate);
                author.setId(authorId);
            }

            return author;
        }
    }

    @Override
    public User findByUserId(long userId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_AUTHOR_BY_USERID_SQL)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            User user = null;
            if (resultSet.next()) {
                user = read(resultSet.getLong(1));
            }
            return user;
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(DELETE_BY_ID_SQL)) {
            statement.setLong(1, id);
            var affectedRows = statement.executeUpdate();
            System.out.println("# of Contacts deleted: " + affectedRows);
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
    private static final String INSERT_SQL =
            "INSERT INTO Users(username, password, user_role) VALUES (?, ?, ?)";

    private static final String DELETE_BY_ID_SQL = """
            DELETE FROM Users
            WHERE id = ?
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT * FROM Users
            WHERE id = ?
            """;
    private static final String FIND_ID_BY_USERNAME_SQL = """
            SELECT id FROM Users
            WHERE username = ?
            """;

    public static final String READ_ALL_SQL = """
            SELECT * FROM Users
            """;

    @Override
    public  User create(User user) throws SQLException {
        var statement = Datasource.getConnection().prepareStatement(INSERT_SQL);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.setString(3, valueOf(user.getRole()));

        statement.executeUpdate();
        statement.close();
        return user;
    }

    public static User read(long id) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            User user = null;
            if (resultSet.next()) {
                long userId = resultSet.getLong(1);
                String username = resultSet.getString(2);
                String password = resultSet.getString(3);
                String role = resultSet.getString(4);
                user = new User(username, password, Role.valueOf(role),userId);
            }

            return user;
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(DELETE_BY_ID_SQL)) {
            statement.setLong(1, id);
            var affectedRows = statement.executeUpdate();
            System.out.println("# of Contacts deleted: " + affectedRows);
        }

    }

    @Override
    public List<User> all() {
        try (var statement = Datasource.getConnection().prepareStatement(READ_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<User> users = new LinkedList<>();
            while (resultSet.next()) {
                long userId = resultSet.getLong(1);
                String username = resultSet.getString(2);
                String password = resultSet.getString(3);
                String role = resultSet.getString(4);
                User user = new User( username, password, Role.valueOf(role),userId);
                users.add(user);
            }

            return new ArrayList<>(users);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public User findByUsername(String username) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_ID_BY_USERNAME_SQL)){
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            User user=null;
            if (resultSet.next()) {
                user =read( resultSet.getLong(1));
            }
            return user;
        }
    }
}
