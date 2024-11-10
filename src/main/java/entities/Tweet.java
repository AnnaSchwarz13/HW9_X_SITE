package entities;

import entities.enums.ArticleStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter

public class Tweet {
    private long id;
    private User user;
    private String title;
    private entities.Category category;
    private String content;
    private Date createDate;
    boolean isPublished;
    private Date lastUpdateDate;
    private Date publishDate;
    private ArticleStatus status;
    private List<Tag> brief ;

    public Tweet(long id, String title, String content, Category category,
                 Date createDate, boolean isPublished,
                 Date lastUpdateDate, ArticleStatus status , User user) {
        this.title = title;
        this.category = category;
        this.content = content;
        this.createDate = createDate;
        this.isPublished = isPublished;
        this.lastUpdateDate = lastUpdateDate;
        this.status = status;
        this.user = user;
        this.id = id;
    }

    public Tweet(User user, String title, Category category, String content) {
        this.user = user;
        this.title = title;
        this.category = category;
        this.content = content;
    }
}



