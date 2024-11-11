package entities;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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
    private List<Long> likes_ids;
    private List<Long> dislikes_ids;
    private List<Long> views_ids;
    private List<Long> retweets;
    private boolean isRetweeted;

    public Tweet(User user, long id, String content, Date createDate) {
        this.user = user;
        this.id = id;
        this.content = content;
        this.createDate = createDate;
        this.likes_ids = new ArrayList<>();
        this.dislikes_ids = new ArrayList<>();
        this.views_ids = new ArrayList<>();
        this.retweets = new ArrayList<>();
        this.isRetweeted = false;
    }

    public Tweet(User user, String content) {
        this.user = user;
        this.content = content;
    }
}



