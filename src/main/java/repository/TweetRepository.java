package repository;

import entities.Tweet;
import entities.User;

import java.sql.SQLException;
import java.util.List;

public interface TweetRepository {
    Tweet create(Tweet tweet) throws SQLException;

    void setRetweet(long tweetId) throws SQLException;

    void delete(long id) throws SQLException;

    void deleteAllLikes(long tweetId) throws SQLException;

    void deleteAllDislikes(long tweetId) throws SQLException;

    void deleteAllRetweets(long tweetId) throws SQLException;

    void deleteAllViews(long tweetId) throws SQLException;

    void deleteRetweetRecords(long tweetId) throws SQLException;

    void deleteLike(long tweetId , long likeId) throws SQLException;

    void deleteDislike(long tweetId , long dislikeId) throws SQLException;

    Tweet read(long id) throws SQLException;

    List<Tweet> getTweetsOfAUser(User user);

    Tweet getTweetOfRetweet(Tweet retweet) throws SQLException;

    List<Tweet> all();

    void updateLike(long tweetId, long userId) throws SQLException;

    void updateDislike(long tweetId, long userId) throws SQLException;

    void updateView(long tweetId, long userId) throws SQLException;

    void updateRetweet(long tweetId, long userId) throws SQLException;

    void updateText(Tweet tweet, String newValue) throws SQLException;
}
