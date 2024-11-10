package entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter

public class Tweet {
    private long id;
    private User user;
    private String content;
    private Date createDate;
    private List<Tag> brief;
    private List<User> likes;
    private List<User> dislikes;
    private List<User> views;
    private List<Tweet> retweets;


    public Tweet(User user, String title, Category category, String content) {
        this.user = user;
        this.title = title;
        this.category = category;
        this.content = content;
    }
}



