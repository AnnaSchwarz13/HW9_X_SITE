package service;

import entities.Tag;
import exceptions.TagException;

import java.sql.SQLException;
import java.util.List;

public interface TagService {
    void isTagExist(String title) throws SQLException, TagException;

    void updateTagList(List<Tag> tags , long tweetId) throws SQLException;

    void showAllTags() throws SQLException, TagException;

    void addNewTag(String newTagName) throws SQLException, TagException;

    Tag addNewTagTweet(String newTagName) throws SQLException, TagException;
}
