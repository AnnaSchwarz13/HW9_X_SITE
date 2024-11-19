package service;

import entities.Tag;
import entities.Tweet;
import entities.User;
import exceptions.TweetException;

import java.sql.SQLException;
import java.util.List;

public interface TweetService {
    Tweet addTweet(String tweetText, List<Long> brief) throws SQLException;

    void displayTweet(Tweet choosenTweet) throws SQLException;

    void displayRetweet(Tweet choosenTweet, int tabs) throws SQLException;

    List<Tweet> getTweetsOfAUser(User user) throws SQLException;

    Tweet getTweetById(long id) throws SQLException;

    void editTweetText(String newText, Tweet chosenTweet) throws SQLException;

    void deleteTweetRetweet(Tweet tweet) throws SQLException;

    List<Tweet> getAllTweets() throws SQLException, TweetException;

    boolean isTweetIdExist(long id) throws SQLException, TweetException;

    void addLike(long id) throws SQLException, TweetException;

    void addDislike(long id) throws SQLException, TweetException;

    void addRetweet(String retweetText, long id , List<Long> brief) throws SQLException;
}
