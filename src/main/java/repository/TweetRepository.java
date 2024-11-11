package repository;

import entities.Tweet;
import entities.User;

import java.sql.SQLException;
import java.util.List;

public interface TweetRepository {
    Tweet create(Tweet tweet) throws SQLException;

    void delete(long id) throws SQLException;

    Tweet findTweetByTile(String title) throws SQLException;

    void updateText(Tweet tweet, String newValue) throws SQLException;

    List<Tweet> getTweetsOfAUser(User user);

    List<Tweet> all();

    boolean isUserIn(long tweetId, long userId, String which) throws SQLException;

    void updateActions(long tweetId, long userId, String which) throws SQLException;

    void deleteActions(long tweetId, long userId, String which) throws SQLException;


}
