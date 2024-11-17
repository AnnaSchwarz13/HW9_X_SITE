package service;

import entities.Tweet;
import entities.User;

import java.sql.SQLException;
import java.util.List;

public interface TweetService {
    Tweet addTweet(String tweetText) throws SQLException;

    void displayTweet(Tweet choosenTweet) throws SQLException;

    void displayRetweet(Tweet choosenTweet, int tabs) throws SQLException;

    List<Tweet> getTweetsOfAUser(User user);

    Tweet getTweetById(long id) throws SQLException;

    void editTweetText(String newText, Tweet chosenTweet) throws SQLException;

    void deleteTweetRetweet(Tweet tweet) throws SQLException;

    List<Tweet> getAllTweets();

    boolean isTweetIdExist(long id) throws SQLException;

    void addActions(int action, long id) throws SQLException;

    void addRetweet(String retweetText, long id) throws SQLException;
}
