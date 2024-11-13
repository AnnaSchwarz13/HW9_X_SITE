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
             INSERT INTO tweets(text,created_date ,user_id )
             VALUES (?, ? ,? )
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
    public static final String FIND_ALL_USER_TWEETS_SQL = """
            SELECT * FROM tweets
            WHERE user_id = ?
            """;
    public static final String UPDATE_TEXT_SQL = """
            UPDATE tweets
            SET text = ?
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
    public static final String GET_RETWEETS_SQL = """
            SELECT retweet_id FROM retweet_tweets
            WHERE tweet_id = ?
            """;
    private static final String GET_TWEET_OF_RETWEET_SQL= """
            SELECT tweet_id FROM retweet_tweets
            WHERE retweet_id = ?
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
    private static final String INSERT_retweet_SQL = """
             INSERT INTO retweet_tweets(retweet_id,tweet_id)
             VALUES (?, ?)
            """;
    private static final String DELETE_LIKE_SQL = """
             DELETE FROM LIKES_tweet
             WHERE tweet_id = ? and like_id = ?
            """;
    private static final String DELETE_DISLIKE_SQL = """
             DELETE FROM disLIKES_tweet
             WHERE tweet_id = ? and dislike_id = ?
            """;
    private static final String DELETE_RETWEET_SQL = """
             DELETE FROM retweets_tweet
             WHERE tweet_id = ? and retweet_id = ?
            """;
    private static final String DELETE_VIEW_SQL = """
             DELETE FROM views_tweet
             WHERE tweet_id = ? and view_id = ?
            """;
    private static final String DELETE_ALL_view_SQL = """
             DELETE FROM views_tweet
             WHERE tweet_id = ?
            """;
    private static final String DELETE_ALL_LIKE_SQL = """
             DELETE FROM LIKES_tweet
             WHERE tweet_id = ?
            """;
    private static final String DELETE_ALL_DISLIKE_SQL = """
             DELETE FROM disLIKES_tweet
             WHERE tweet_id = ?
            """;
    private static final String DELETE_ALL_RETWEET_SQL = """
             DELETE FROM retweet_tweets
             WHERE tweet_id = ?
            """;
    private static final String DELETE_ALL_RETWEET_REL_SQL = """
             DELETE FROM retweet_tweets
             WHERE retweet_id = ?
            """;
    private static final String SET_TWEETED_SQL = """
            UPDATE tweets
            SET isretweeted = true
            WHERE id = ?
            """;
//this

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
                boolean isRetweeted = resultSet.getBoolean(5);

                tweet = new Tweet(user, tweetId, text, postedDate);
                tweet.setDislikes_ids(getActionOfTweet(tweetId, "dislike"));
                tweet.setLikes_ids(getActionOfTweet(tweetId, "like"));
                tweet.setViews_ids(getActionOfTweet(tweetId, "view"));
                tweet.setRetweets(getActionOfTweet(tweetId, "retweet"));
                tweet.setRetweeted(isRetweeted);

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
    public void setRetweet(long tweetId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(SET_TWEETED_SQL)) {
            statement.setLong(1, tweetId);
            statement.executeUpdate();
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
    public void deleteActions(long tweetId, long userId, String which) throws SQLException {
        actionChoose(tweetId, userId, which, DELETE_LIKE_SQL, DELETE_DISLIKE_SQL, DELETE_VIEW_SQL, DELETE_RETWEET_SQL);
    }

    @Override
    public void deleteRecords(long tweetId, String which) throws SQLException {
        PreparedStatement statement = switch (which) {
            case "like" -> Datasource.getConnection().prepareStatement(DELETE_ALL_LIKE_SQL);
            case "dislike" -> Datasource.getConnection().prepareStatement(DELETE_ALL_DISLIKE_SQL);
            case "view" -> Datasource.getConnection().prepareStatement(DELETE_ALL_view_SQL);
            case "retweet" -> Datasource.getConnection().prepareStatement(DELETE_ALL_RETWEET_SQL);
            case "tweet" -> Datasource.getConnection().prepareStatement(DELETE_ALL_RETWEET_REL_SQL);
            default -> null;
        };

        if (statement != null) {
            statement.setLong(1, tweetId);
            statement.executeUpdate();
        }
    }


    @Override
    public List<Tweet> getTweetsOfAUser(User user) {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_ALL_USER_TWEETS_SQL)) {
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

    private List<Tweet> getTweets(PreparedStatement statement) throws SQLException {
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
            statement.setLong(2, tweet.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void updateActions(long tweetId, long userId, String which) throws SQLException {
        actionChoose(userId, tweetId, which, INSERT_like_SQL, INSERT_dislike_SQL, INSERT_view_SQL, INSERT_retweet_SQL);
    }


    //----
//delete statics
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

    private static List<Long> getActionOfTweet(long id, String which) throws SQLException {
        PreparedStatement statement = switch (which) {
            case "like" -> Datasource.getConnection().prepareStatement(GET_LIKES_SQL);
            case "dislike" -> Datasource.getConnection().prepareStatement(GET_DISLIKES_SQL);
            case "view" -> Datasource.getConnection().prepareStatement(GET_VIEWS_SQL);
            case "retweet" -> Datasource.getConnection().prepareStatement(GET_RETWEETS_SQL);
            default -> null;
        };
        if (statement != null) {
            statement.setLong(1, id);
            statement.execute();
            return getIds(statement);
        }
        return null;
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

    private void actionChoose(long tweetId, long userId, String which, String deleteLikeSql, String deleteDislikeSql, String deleteViewSql, String deleteRetweetSql) throws SQLException {
        PreparedStatement statement = switch (which) {
            case "like" -> Datasource.getConnection().prepareStatement(deleteLikeSql);
            case "dislike" -> Datasource.getConnection().prepareStatement(deleteDislikeSql);
            case "view" -> Datasource.getConnection().prepareStatement(deleteViewSql);
            case "retweet" -> Datasource.getConnection().prepareStatement(deleteRetweetSql);
            default -> null;
        };

        if (statement != null) {
            statement.setLong(1, tweetId);
            statement.setLong(2, userId);
            statement.executeUpdate();
        }
    }

    public Tweet getTweetOfRetweet(Tweet retweet) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(GET_TWEET_OF_RETWEET_SQL)){
            statement.setLong(1, retweet.getId());
            ResultSet resultSet = statement.executeQuery();
            Tweet tweet = null;
            if (resultSet.next()) {
                tweet = read(resultSet.getLong(1));
            }
            return tweet;
        }
    }


}

