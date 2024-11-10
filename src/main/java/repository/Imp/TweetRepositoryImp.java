package repository.Imp;

import entities.Tweet;
import entities.User;
import repository.Datasource;
import repository.TweetRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TweetRepositoryImp implements TweetRepository {
    //CRUD create read update delete
    static TagRepositoryImp tagRepositoryImp = new TagRepositoryImp();
    //SQL
    private static final String INSERT_SQL = """
             INSERT INTO tweets(text,created_date ,user_id ,views, likes,dislikes)
             VALUES (?, ? ,? ,default,default ,default)
            """;

    private static final String DELETE_BY_ID_SQL = """
            DELETE FROM tweets
            WHERE id = ?
            """;
    private static final String ALL_TWEETS = """
            SELECT * FROM tweets
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT * FROM tweets
            WHERE id = ?
            """;
    public static final String FIND_BY_TITLE_SQL = """
            SELECT * FROM tweets
            WHERE title = ?
            """;
    public static final String FIND_ALL_AUTHOR_ARTICLES_SQL = """
            SELECT * FROM tweets
            WHERE author_id = ?
            """;
    public static final String UPDATE_TEXT_SQL = """
            UPDATE tweets
            SET text = ? , last_updated_date = ?
            WHERE id = ?
            """;
    public static final String GET_LAST_INDEX = """
            SELECT id FROM tweets
            where user_id =?
            ORDER BY id DESC
            LIMIT 1
            """;

    public static Tweet read(long id) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            Tweet tweet = null;
            if (resultSet.next()) {
                long tweetId = resultSet.getLong(1);
                String text = resultSet.getString(2);
                Date postedDate = resultSet.getDate(3);
                long userId = resultSet.getLong(4);
                User user = UserRepositoryImp.read(userId);
                List<Integer> views = new ArrayList<>();
                String viewsS = resultSet.getString(5);
                String[] tokens = viewsS.split(",");
                for (String token : tokens) {
                    views.add(Integer.valueOf(token));
                }

                List<Integer> likes = new ArrayList<>();
                String likesS = resultSet.getString(6);
                String[] tokes = likesS.split(",");
                for (String toke : tokes) {
                    likes.add(Integer.valueOf(toke));
                }

                List<Integer> dislikes = new ArrayList<>();
                String dislikesS = resultSet.getString(7);
                String[] toks = dislikesS.split(",");
                for (String tok : toks) {
                    dislikes.add(Integer.valueOf(tok));
                }

                tweet = new Tweet(user, tweetId, text, postedDate, likes, dislikes, views);
                tweet.setBrief(tagRepositoryImp.getTags(tweet));
            }
            return tweet;
        }
    }

    @Override
    public Tweet create(Tweet tweet) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(INSERT_SQL)) {
            statement.setString(1, tweet.getContent());
            statement.setDate(2, Date.valueOf(LocalDate.now()));
            statement.setLong(3, tweet.getUser().getId());
            statement.executeUpdate();
            long id = getLastId(tweet.getUser());
            tweet.setId(id);
            return tweet;
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
    public List<Tweet> getTweetsOfAUser(User user) {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_ALL_AUTHOR_ARTICLES_SQL)) {
            statement.setLong(1, user.getId());
            return getArticles(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<Tweet> all() {
        try (var statement = Datasource.getConnection().prepareStatement(ALL_TWEETS)) {
            return getArticles(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Tweet> getArticles(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        List<Tweet> publishedTweets = new LinkedList<>();
        while (resultSet.next()) {
            Tweet tweet = read(resultSet.getLong(1));
            publishedTweets.add(tweet);
        }
        return new ArrayList<>(publishedTweets);
    }


    //update details

    @Override
    public void updateText(Tweet tweet, String newValue) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_TEXT_SQL)) {
            statement.setString(1, newValue);
            statement.setDate(2, Date.valueOf(LocalDate.now()));
            statement.setLong(3, tweet.getId());
            statement.executeUpdate();
        }
    }

    //----
    @Override
    public Tweet findArticleByTile(String title) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_BY_TITLE_SQL)) {
            statement.setString(1, title);
            ResultSet resultSet = statement.executeQuery();
            Tweet tweet = null;
            if (resultSet.next()) {
                tweet = read(resultSet.getInt(1));
            } else {
                System.out.println("No Article found for title: " + title);
            }

            return tweet;
        }
    }

    private static long getLastId(User user) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(GET_LAST_INDEX)) {
            statement.setLong(1, user.getId());
            ResultSet resultSet = statement.executeQuery();

            long id = 0;
            if (resultSet.next()) {
                id = resultSet.getLong(1);
            }
            return id;
        }
    }
}



