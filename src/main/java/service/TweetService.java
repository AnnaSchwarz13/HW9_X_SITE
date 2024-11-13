package service;

import entities.Tweet;

import java.sql.SQLException;
import java.util.List;

public interface TweetService {
    Tweet addTweet() throws SQLException;

    void showTweetList(List<Tweet> tweets) throws SQLException;

    void changeDetailsOfTweet(Tweet choosenTweet) throws SQLException;

    void displayTweet(Tweet choosenTweet) throws SQLException;

}
