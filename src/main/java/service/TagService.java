package service;

import entities.Tag;
import exceptions.TagException;

import java.sql.SQLException;
import java.util.List;

public interface TagService {
    List<Tag> setTweetTags() throws SQLException;
    void isTagExist(String title) throws SQLException, TagException;
    void updateTagList(List<Tag> tags , long tweetId) throws SQLException;
}
