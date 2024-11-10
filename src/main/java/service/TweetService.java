package service;

import entities.Tweet;

import java.sql.SQLException;
import java.util.List;

public interface TweetService {
    void addArticle() throws SQLException;
    void showAnArticleList(List<Tweet> tweets) throws SQLException;
    void changeArticleStatus(Tweet choosenTweet) throws SQLException;
    void changeDetailsOfArticle(Tweet choosenTweet) throws SQLException;
    void displayArticle(Tweet choosenTweet);
}
