package service;

import entities.Tweet;
import entities.User;

import java.sql.SQLException;
import java.util.List;

public interface TweetService {
    Tweet addTweet() throws SQLException;

    void showTweetList() throws SQLException;

    void changeDetailsOfTweet(Tweet choosenTweet) throws SQLException;

    void displayTweet(Tweet choosenTweet) throws SQLException;

    void displayRetweet(Tweet choosenTweet, int tabs) throws SQLException;

    List<Tweet> getTweetsOfAUser(User user);

    Tweet getTweetById(long id) throws SQLException;



}
