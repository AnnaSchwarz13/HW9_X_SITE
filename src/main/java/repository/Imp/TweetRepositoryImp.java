package repository.Imp;

import entities.Tweet;
import entities.User;
import repository.Datasource;
import repository.TweetRepository;

import java.sql.*;
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
    public static final String GET_LIKES_SQL = """
            SELECT like_id FROM likes_tweet
            WHERE tweet_id = ?
            """;
    public static final String GET_DISLIKES_SQL = """
            SELECT dislike_id FROM dislikes_tweet
            WHERE tweet_id = ?
            """;
    public static final String GET_VIEWS_SQL = """
            SELECT view_id FROM views_tweet
            WHERE tweet_id = ?
            """;
    public static final String is_user_liked = """
            SELECT * FROM likes_tweet
            WHERE tweet_id = ? AND like_id = ?
            """;
    public static final String is_user_disliked = """
            SELECT * FROM dislikes_tweet
            WHERE tweet_id = ? AND dislike_id = ?
            """;
    public static final String is_user_viewed = """
            SELECT * FROM views_tweet
            WHERE tweet_id = ? AND view_id=?
            """;
    private static final String INSERT_like_SQL = """
             INSERT INTO likes_tweet(like_id,tweet_id)
             VALUES (?, ?)
            """;
    private static final String INSERT_dislike_SQL = """
             INSERT INTO dislikes_tweet(dislike_id,tweet_id)
             VALUES (?, ?)
            """;
    private static final String INSERT_view_SQL = """
             INSERT INTO views_tweet(view_id,tweet_id)
             VALUES (?, ?)
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

                tweet = new Tweet(user, tweetId, text, postedDate);
                tweet.setDislikes_ids(getDislikesOfTweet(tweetId));
                tweet.setViews_ids(getViewsOfTweet(tweetId));
                tweet.setLikes_ids(getLikesOfTweet(tweetId));
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
            return getTweets(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Tweet> all() {
        try (var statement = Datasource.getConnection().prepareStatement(ALL_TWEETS)) {
            return getTweets(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Tweet> getTweets(PreparedStatement statement) throws SQLException {
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

    public void updateActions(long tweetId, long userId, String which) throws SQLException {
        PreparedStatement statement = switch (which) {
            case "like" -> Datasource.getConnection().prepareStatement(INSERT_like_SQL);
            case "dislike" -> Datasource.getConnection().prepareStatement(INSERT_dislike_SQL);
            case "view" -> Datasource.getConnection().prepareStatement(INSERT_view_SQL);
            default -> null;
        };

        if (statement != null) {
            statement.setLong(1, userId);
            statement.setLong(2, tweetId);
            statement.executeUpdate();
        }
    }


    //----
    @Override
    public Tweet findTweetByTile(String title) throws SQLException {
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

    private static List<Long> getViewsOfTweet(long id) {
        try (var statement = Datasource.getConnection().prepareStatement(GET_VIEWS_SQL)) {
            statement.setLong(1, id);
            return getIds(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static List<Long> getDislikesOfTweet(long id) {
        try (var statement = Datasource.getConnection().prepareStatement(GET_DISLIKES_SQL)) {
            statement.setLong(1, id);
            return getIds(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Long> getLikesOfTweet(long id) {
        try (var statement = Datasource.getConnection().prepareStatement(GET_LIKES_SQL)) {
            statement.setLong(1, id);
            return getIds(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Long> getIds(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        List<Long> ids = new LinkedList<>();
        while (resultSet.next()) {
            long id = resultSet.getLong(1);
            ids.add(id);
        }
        return new ArrayList<>(ids);
    }
@Override
    public boolean isUserIn(long tweetId, long userId, String which) throws SQLException {
        PreparedStatement statement = switch (which) {
            case "like" -> Datasource.getConnection().prepareStatement(is_user_liked);
            case "dislike" -> Datasource.getConnection().prepareStatement(is_user_disliked);
            case "view" -> Datasource.getConnection().prepareStatement(is_user_viewed);
            default -> null;
        };

        if (statement != null) {
            statement.setLong(1, tweetId);
            statement.setLong(2, userId);
           ResultSet resultSet = statement.executeQuery();
            long id = 0;
            if (resultSet.next()) {
                id = resultSet.getLong(1);
            }
            return id!=0;
        }
        return false;
    }
}



