package repository.Imp;


import entities.Tweet;
import entities.User;
import entities.Category;
import entities.enums.ArticleStatus;
import repository.TweetRepository;
import repository.Datasource;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TweetRepositoryImp implements TweetRepository {
    //CRUD create read update delete
    static CategoryRepositoryImp categoryRepositoryImp = new CategoryRepositoryImp();
    static TagRepositoryImp tagRepositoryImp = new TagRepositoryImp();
    //SQL
    private static final String INSERT_SQL = """
             INSERT INTO Articles(title, text,category_id, published_date  ,
                                    created_date , last_updated_date\s
                                  , author_id , is_published, article_status)
             VALUES (?, ? ,? ,? ,? ,? , ?, ?,?)
            """;

    private static final String DELETE_BY_ID_SQL = """
            DELETE FROM Articles
            WHERE id = ?
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT * FROM Articles
            WHERE id = ?
            """;
    private static final String PUBLISHED_ARTICLES_SQL = """
            SELECT id FROM Articles
            WHERE ARTICLE_STATUS = 'PUBLISHED'
            """;

    private static final String PENDING_ARTICLES_SQL = """
            SELECT id FROM Articles
            WHERE ARTICLE_STATUS = 'PENDING'
            """;
    private static final String UPDATE_Article_Status_SQL = """
            UPDATE Articles
            SET article_status = ? , published_date=? , last_updated_date=? , is_published=?
            where id = ?
            """;
    public static final String FIND_BY_TITLE_SQL = """
            SELECT * FROM Articles
            WHERE title = ?
            """;
    public static final String FIND_ALL_AUTHOR_ARTICLES_SQL = """
            SELECT * FROM Articles
            WHERE author_id = ?
            """;
    public static final String UPDATE_TITLE_SQL = """
            UPDATE Articles
            SET title = ? , last_updated_date = ?
            WHERE id = ?
            """;
    public static final String UPDATE_TEXT_SQL = """
            UPDATE Articles
            SET text = ? , last_updated_date = ?
            WHERE id = ?
            """;
    public static final String UPDATE_CATEGORY_SQL = """
            UPDATE Articles
            SET category_id = ? , last_updated_date = ?
            WHERE id = ?
            """;
    public static final String UPDATE_LAST_DATE_SQL = """
            UPDATE Articles
            SET last_updated_date = ?
            WHERE id = ?
            """;
    public static final String GET_LAST_INDEX = """
            SELECT id FROM Articles
            where author_id =?
            ORDER BY id DESC
            LIMIT 1
            """;

    public static Tweet read(long id) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            Tweet tweet = null;
            if (resultSet.next()) {
                long articleId = resultSet.getLong(1);
                String title = resultSet.getString(2);
                String text = resultSet.getString(3);
                int categoryId = resultSet.getInt(4);
                Date publishDate = resultSet.getDate(5);
                Date createDate = resultSet.getDate(6);
                Date lastUpdateDate = resultSet.getDate(7);
                int authorId = resultSet.getInt(8);
                boolean published = resultSet.getBoolean(9);
                String status = resultSet.getString(10);
                Category category = categoryRepositoryImp.read(categoryId);
                User user = UserRepositoryImp.read(authorId);
                tweet = new Tweet(articleId, title, text, category, createDate,
                        published, lastUpdateDate, ArticleStatus.valueOf(status), user);
                tweet.setBrief(tagRepositoryImp.getTags(tweet));
                if (published) {
                    tweet.setPublishDate(publishDate);
                }
            }
            return tweet;
        }
    }

    @Override
    public Tweet create(Tweet tweet) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(INSERT_SQL)) {
            statement.setString(1, tweet.getTitle());
            statement.setString(2, tweet.getContent());
            statement.setLong(3, (tweet.getCategory()).getId());
            statement.setDate(4, null);
            statement.setDate(5, Date.valueOf(LocalDate.now()));
            statement.setDate(6, Date.valueOf(LocalDate.now()));
            statement.setLong(7, tweet.getUser().getId());
            statement.setBoolean(8, false);
            statement.setString(9, "NOT_PUBLISHED");
            statement.executeUpdate();
            long id = getLastId(tweet.getUser());
            tweet.setId(id);
            System.out.println(id);
            return tweet;
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(DELETE_BY_ID_SQL)) {
            statement.setLong(1, id);
            var affectedRows = statement.executeUpdate();
            System.out.println("# of Contacts deleted: " + affectedRows);
        }
    }

    @Override
    public List<Tweet> allPublished() {
        try (var statement = Datasource.getConnection().prepareStatement(PUBLISHED_ARTICLES_SQL)) {
            return getArticles(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Tweet> allPending() {
        try (var statement = Datasource.getConnection().prepareStatement(PENDING_ARTICLES_SQL)) {
            return getArticles(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Tweet> getArticlesOfAnAuthor(User user) {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_ALL_AUTHOR_ARTICLES_SQL)) {
            statement.setLong(1, user.getId());
            return getArticles(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Tweet> getArticles(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        List<Tweet> publishedTweets = new LinkedList<>();
        while (resultSet.next()) {
            Tweet tweet = read(resultSet.getLong(1));
            publishedTweets.add(tweet);
        }
        return new ArrayList<>(publishedTweets);
    }


    //update status
    @Override
    public void updateStatusPublished(Tweet tweet) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_Article_Status_SQL)) {
            statement.setString(1, "PUBLISHED");
            statement.setDate(2, Date.valueOf(LocalDate.now()));
            statement.setDate(3, Date.valueOf(LocalDate.now()));
            statement.setBoolean(4, true);
            statement.setLong(5, tweet.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void updateStatusNotPublished(Tweet tweet) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_Article_Status_SQL)) {
            statement.setString(1, "NOT_PUBLISHED");
            statement.setDate(2, null);
            statement.setDate(3, Date.valueOf(LocalDate.now()));
            statement.setBoolean(4, false);
            statement.setLong(5, tweet.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void updateStatusPending(Tweet tweet) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_Article_Status_SQL)) {
            statement.setString(1, "PENDING");
            statement.setDate(2, null);
            statement.setDate(3, Date.valueOf(LocalDate.now()));
            statement.setBoolean(4, false);
            statement.setLong(5, tweet.getId());
            statement.executeUpdate();
        }
    }

    //update details
    @Override
    public void updateTitle(Tweet tweet, String newValue) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_TITLE_SQL)) {
            statement.setString(1, newValue);
            statement.setDate(2, Date.valueOf(LocalDate.now()));
            statement.setLong(3, tweet.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void updateText(Tweet tweet, String newValue) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_TEXT_SQL)) {
            statement.setString(1, newValue);
            statement.setDate(2, Date.valueOf(LocalDate.now()));
            statement.setLong(3, tweet.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void updateCategory(Tweet tweet, Category category) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_CATEGORY_SQL)) {
            statement.setLong(1, category.getId());
            statement.setDate(2, Date.valueOf(LocalDate.now()));
            statement.setLong(3, tweet.getId());
            statement.executeUpdate();
        }

    }

    @Override
    public void setLastUpdateDate(Tweet tweet) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(UPDATE_LAST_DATE_SQL)) {
            statement.setDate(1, Date.valueOf(LocalDate.now()));
            statement.setLong(2, tweet.getId());

            statement.executeUpdate();
        }
    }

    //----
    @Override
    public Tweet findArticleByTile(String title) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(FIND_BY_TITLE_SQL)) {
            statement.setString(1, title);
            ResultSet resultSet = statement.executeQuery();
            Tweet tweet = null;
            if (resultSet.next()) {
                tweet = read(resultSet.getInt(1));
            } else {
                System.out.println("No Article found for title: " + title);
            }

            return tweet;
        }
    }

    private static long getLastId(User user) throws SQLException {
        try (var statement = Datasource.getConnection().prepareStatement(GET_LAST_INDEX)) {
            statement.setLong(1, user.getId());
            ResultSet resultSet = statement.executeQuery();

            long id = 0;
            if (resultSet.next()) {
                id = resultSet.getLong(1);
            }
            return id;
        }
    }
}



