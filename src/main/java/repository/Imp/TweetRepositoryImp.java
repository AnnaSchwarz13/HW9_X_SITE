package repository.Imp;

import entities.Tweet;
import entities.User;
import config.Datasource;
import repository.TweetRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TweetRepositoryImp implements TweetRepository {
    //CRUD create read update delete
    TagRepositoryImp tagRepositoryImp = new TagRepositoryImp();
    UserRepositoryImp userRepositoryImp = new UserRepositoryImp();
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
    private static final String FIND_ALL_USER_TWEETS_SQL = """
            SELECT * FROM tweets
            WHERE user_id = ?
            """;
    private static final String UPDATE_TEXT_SQL = """
            UPDATE tweets
            SET text = ?
            WHERE id = ?
            """;
    private static final String GET_LAST_INDEX = """
            SELECT id FROM tweets
            where user_id =?
            ORDER BY id DESC
            LIMIT 1
            """;
    private static final String GET_LIKES_SQL = """
            SELECT like_id FROM likes_tweet
            WHERE tweet_id = ?
            """;
    private static final String GET_DISLIKES_SQL = """
            SELECT dislike_id FROM dislikes_tweet
            WHERE tweet_id = ?
            """;
    private static final String GET_VIEWS_SQL = """
            SELECT view_id FROM views_tweet
            WHERE tweet_id = ?
            """;
    private static final String GET_RETWEETS_SQL = """
            SELECT retweet_id FROM retweet_tweets
            WHERE tweet_id = ?
            """;
    private static final String GET_TWEET_OF_RETWEET_SQL = """
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

    //read
    @Override
    public Tweet read(long id) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            Tweet tweet = null;
            if (resultSet.next()) {
                long tweetId = resultSet.getLong(1);
                String text = resultSet.getString(2);
                Date postedDate = resultSet.getDate(3);
                long userId = resultSet.getLong(4);
                User user = userRepositoryImp.read(userId);
                boolean isRetweeted = resultSet.getBoolean(5);

                tweet = new Tweet(user, tweetId, text, postedDate);
                tweet.setDislikes_ids(getDislikes(tweetId));
                tweet.setLikes_ids(getLikes(tweetId));
                tweet.setViews_ids(getViews(tweetId));
                tweet.setRetweets(getRetweets(tweetId));
                tweet.setRetweeted(isRetweeted);

                tweet.setBrief(tagRepositoryImp.getTags(tweet));
            }
            return tweet;
        }
    }

    @Override
    public Tweet getTweetOfRetweet(Tweet retweet) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(GET_TWEET_OF_RETWEET_SQL)) {
            statement.setLong(1, retweet.getId());
            ResultSet resultSet = statement.executeQuery();
            Tweet tweet = null;
            if (resultSet.next()) {
                tweet = read(resultSet.getLong(1));
            }
            return tweet;
        }
    }

    @Override
    public List<Tweet> all() throws SQLException{
        try (var statement = Datasource.getConnection().prepareStatement(ALL_TWEETS)) {
            return getTweets(statement);
        }
    }

    @Override
    public List<Tweet> getTweetsOfAUser(User user) throws SQLException{
        try (var statement = Datasource.getConnection().prepareStatement(FIND_ALL_USER_TWEETS_SQL)) {
            statement.setLong(1, user.getId());
            return getTweets(statement);
        }
    }

    private List<Long> getLikes(long tweetId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(GET_LIKES_SQL)) {
            List<Long> likes = new ArrayList<>();
            statement.setLong(1, tweetId);
            statement.execute();
            return getIds(statement);
        }
    }

    private List<Long> getDislikes(long tweetId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(GET_DISLIKES_SQL)) {
            List<Long> likes = new ArrayList<>();
            statement.setLong(1, tweetId);
            statement.execute();
            return getIds(statement);
        }
    }

    private List<Long> getViews(long tweetId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(GET_VIEWS_SQL)) {
            List<Long> likes = new ArrayList<>();
            statement.setLong(1, tweetId);
            statement.execute();
            return getIds(statement);
        }
    }

    private List<Long> getRetweets(long tweetId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(GET_RETWEETS_SQL)) {
            List<Long> likes = new ArrayList<>();
            statement.setLong(1, tweetId);
            statement.execute();
            return getIds(statement);
        }
    }

    private List<Long> getIds(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        List<Long> ids = new LinkedList<>();
        while (resultSet.next()) {
            long id = resultSet.getLong(1);
            ids.add(id);
        }
        return new ArrayList<>(ids);
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

    //create
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

    private long getLastId(User user) throws SQLException {
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

    @Override
    public void setRetweet(long tweetId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(SET_TWEETED_SQL)) {
            statement.setLong(1, tweetId);
            statement.executeUpdate();
        }
    }

    //delete
    @Override
    public void delete(long id) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(DELETE_BY_ID_SQL)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteAllLikes(long tweetId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(DELETE_ALL_LIKE_SQL)) {
            statement.setLong(1, tweetId);
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteAllDislikes(long tweetId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(DELETE_ALL_DISLIKE_SQL)) {
            statement.setLong(1, tweetId);
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteAllRetweets(long tweetId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(DELETE_ALL_RETWEET_SQL)) {
            statement.setLong(1, tweetId);
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteAllViews(long tweetId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(DELETE_ALL_view_SQL)) {
            statement.setLong(1, tweetId);
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteRetweetRecords(long tweetId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(DELETE_ALL_RETWEET_REL_SQL)) {
            statement.setLong(1, tweetId);
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteLike(long tweetId, long likeId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(DELETE_LIKE_SQL)) {
            statement.setLong(1, tweetId);
            statement.setLong(2, likeId);
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteDislike(long tweetId, long dislikeId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(DELETE_DISLIKE_SQL)) {
            statement.setLong(1, tweetId);
            statement.setLong(2, dislikeId);
            statement.executeUpdate();
        }
    }

    //update
    @Override
    public void updateText(Tweet tweet, String newValue) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_TEXT_SQL)) {
            statement.setString(1, newValue);
            statement.setLong(2, tweet.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void updateLike(long tweetId, long userId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(INSERT_like_SQL)) {
            statement.setLong(2, tweetId);
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }

    @Override
    public void updateDislike(long tweetId, long userId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(INSERT_dislike_SQL)) {
            statement.setLong(2, tweetId);
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }

    @Override
    public void updateRetweet(long tweetId, long retweetId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(INSERT_retweet_SQL)) {
            statement.setLong(2, tweetId);
            statement.setLong(1, retweetId);
            statement.executeUpdate();
        }
    }

    @Override
    public void updateView(long tweetId, long userId) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(INSERT_view_SQL)) {
            statement.setLong(2, tweetId);
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }
}