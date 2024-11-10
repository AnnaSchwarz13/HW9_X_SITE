package entities;

import lombok.AllArgsConstructor;
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
    private List<Integer> likes_ids;
    private List<Integer> dislikes_ids;
    private List<Integer> views_ids;
    private List<Tweet> retweets;

    public Tweet(User user, long id, String content, Date createDate,
                 List<Integer> likes_ids, List<Integer> dislikes_ids, List<Integer> views_ids) {
        this.user = user;
        this.id = id;
        this.content = content;
        this.createDate = createDate;
        this.likes_ids = likes_ids;
        this.dislikes_ids = dislikes_ids;
        this.views_ids = views_ids;
    }
}



