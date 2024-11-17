package service;

import entities.Tag;
import exceptions.TagException;

import java.sql.SQLException;
import java.util.List;

public interface TagService {
    void isTagExist(String title) throws SQLException, TagException;

    void updateTagList(List<Long> tags , long tweetId) throws SQLException;

    void showAllTags() throws SQLException;

    void addNewTag(String newTagName) throws SQLException, TagException;

    long addNewTagTweet(String newTagName) throws SQLException, TagException;

    Tag getTagById(long id) throws SQLException, TagException;

    List<Long> removeDuplicates(List<Long> tags);
}
