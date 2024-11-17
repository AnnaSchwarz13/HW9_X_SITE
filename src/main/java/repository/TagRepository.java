package repository;

import entities.Tag;
import entities.Tweet;
import exceptions.TagException;

import java.sql.SQLException;
import java.util.List;

public interface TagRepository {
    Tag create(Tag tag) throws SQLException;

    void setTweetTag(List<Long> tags, Tweet tweet) throws SQLException;

    Tag read(long id) throws SQLException;

    int findCount() throws SQLException;

    List<Tag> all() throws SQLException;

    Tag findTagByTile(String title) throws SQLException , TagException;

    List<Long> getTags(Tweet tweet) throws SQLException;

    void delete(long id) throws SQLException;
}
