package repository.Imp;

import entities.Tweet;
import entities.Tag;
import config.Datasource;
import exceptions.TagException;
import repository.TagRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TagRepositoryImp implements TagRepository {

    private static final String INSERT_SQL =
            "INSERT INTO Tags(title) VALUES (?)";
    private static final String DELETE_BY_TWEET_ID_SQL = """
            DELETE FROM Tags_tweets
            WHERE tweet_id = ?
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT * FROM Tags
            WHERE id = ?
            """;
    private static final String FIND_COUNT_SQL = """
            SELECT COUNT(*) FROM Tags
            """;
    private static final String READ_ALL_SQL = """
            SELECT * FROM Tags
            """;
    private static final String FIND_BY_TITLE_SQL = """
            SELECT * FROM Tags
            WHERE title = ?
            """;
    private static final String FIND_TWEETS_TAG = """
            SELECT tag_id FROM Tags_tweets
            WHERE tweet_id = ?
            """;
    private static final String INSET_TWEETS_TAGS = """
            INSERT INTO Tags_tweets(tweet_id, tag_id) VALUES (?, ?)
            """;

    //create
    @Override
    public Tag create(Tag tag) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(INSERT_SQL)) {
            statement.setString(1, tag.getTitle());
            statement.executeUpdate();
            return tag;
        }
    }

    //update
    @Override
    public void setTweetTag(List<Long> tags, Tweet tweet) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(INSET_TWEETS_TAGS)) {
            for (long tag : tags) {
                statement.setLong(1, tweet.getId());
                statement.setLong(2, tag);
                statement.execute();
            }
        }
    }
    //read
    @Override
    public Tag read(long id) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Tag tag = null;
            if (resultSet.next()) {
                long tagId = resultSet.getLong(1);
                String title = resultSet.getString(2);
                tag = new Tag(title, tagId);
            }

            return tag;
        }
    }

    @Override
    public int findCount() throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_COUNT_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            int tagsIndex = 0;
            if (resultSet.next()) {
                tagsIndex = resultSet.getInt(1);
            }
            return tagsIndex;
        }
    }

    @Override
    public List<Tag> all() throws SQLException{
        try (var statement = Datasource.getConnection().prepareStatement(READ_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<Tag> tags = new LinkedList<>();
            while (resultSet.next()) {
                long tagId = resultSet.getLong(1);
                String title = resultSet.getString(2);
                Tag tag = new Tag(title, tagId);
                tags.add(tag);
            }
            return new ArrayList<>(tags);
        }
    }

    @Override
    public Tag findTagByTile(String title) throws SQLException , TagException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_BY_TITLE_SQL)) {
            statement.setString(1, title);
            ResultSet resultSet = statement.executeQuery();
            Tag tag = null;
            if (resultSet.next()) {
                tag = read(resultSet.getInt(1));
            }
            else {
                throw new TagException("Tag does not exist");
            }
            return tag;
        }
    }

    @Override
    public List<Long> getTags(Tweet tweet) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_TWEETS_TAG)) {
            statement.setLong(1, tweet.getId());
            ResultSet resultSet = statement.executeQuery();
            List<Long> tags = new LinkedList<>();
            while (resultSet.next()) {
                long id =resultSet.getLong(1);
                tags.add(id);
            }
            return new ArrayList<>(tags);
        }
    }

    //delete
    @Override
    public void delete(long id) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(DELETE_BY_TWEET_ID_SQL)) {
            statement.setLong(1, id);
            var affectedRows = statement.executeUpdate();
        }
    }

}
