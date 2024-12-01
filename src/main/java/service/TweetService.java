package service;

import entities.Tweet;
import entities.User;
import exceptions.TweetException;

import java.util.List;

public interface TweetService {
    Tweet addTweet(String tweetText, List<Long> brief);

    void displayTweet(Tweet choosenTweet);

    void displayRetweet(Tweet choosenTweet, int tabs);

    List<Tweet> getTweetsOfAUser(User user);

    Tweet getTweetById(long id);

    void editTweetText(String newText, Tweet chosenTweet);

    void deleteTweetRetweet(Tweet tweet);

    List<Tweet> getAllTweets() throws TweetException;

    boolean isTweetIdExist(long id) throws TweetException;

    void addLike(long id) throws TweetException;

    void addDislike(long id) throws TweetException;

    void addRetweet(String retweetText, long id, List<Long> brief);
}
