package repository;

import entities.Tweet;
import entities.User;
import entities.Category;

import java.sql.SQLException;
import java.util.List;

public interface TweetRepository {
    Tweet create(Tweet tweet) throws SQLException;

    void delete(long id) throws SQLException;

    //  Article read(int id) throws SQLException;
    Tweet findArticleByTile(String title) throws SQLException;

    void setLastUpdateDate(Tweet tweet) throws SQLException;

    void updateCategory(Tweet tweet, Category category) throws SQLException;

    void updateText(Tweet tweet, String newValue) throws SQLException;

    void updateTitle(Tweet tweet, String newValue) throws SQLException;

    void updateStatusPending(Tweet tweet) throws SQLException;

    void updateStatusNotPublished(Tweet tweet) throws SQLException;

    void updateStatusPublished(Tweet tweet) throws SQLException;

    List<Tweet> getArticlesOfAnAuthor(User user);

    List<Tweet> allPending();

    List<Tweet> allPublished();

}
