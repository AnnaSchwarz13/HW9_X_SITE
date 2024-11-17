package service.Imp;

import entities.Tag;
import entities.Tweet;
import entities.User;
import repository.Imp.TagRepositoryImp;
import repository.Imp.TweetRepositoryImp;
import service.TweetService;

import java.sql.SQLException;
import java.util.List;

public class TweetServiceImp implements TweetService {
    TweetRepositoryImp tweetRepositoryImp = new TweetRepositoryImp();
    TagRepositoryImp tagRepositoryImp = new TagRepositoryImp();
    TagServiceImp tagServiceImp = new TagServiceImp();
    AuthenticationServiceImp authenticationServiceImp = new AuthenticationServiceImp();

    @Override
    public Tweet addTweet(String tweetText) throws SQLException {
        List<Tag> brief = tagServiceImp.setTweetTags();
        Tweet tweet = new Tweet(authenticationServiceImp.getLoggedUser(), tweetText);
        tweet = tweetRepositoryImp.create(tweet);
        tagRepositoryImp.setTweetTag(brief, tweet);
        System.out.println("Tweeted!!");
        return tweet;
    }

    @Override
    public void addRetweet(String retweetText, long id) throws SQLException {
        Tweet tweet = addTweet(retweetText);
        tweetRepositoryImp.updateRetweet(tweetRepositoryImp.read(id).getId(), tweet.getId());
        tweetRepositoryImp.read(id).getRetweets().add(tweet.getId());
        tweetRepositoryImp.setRetweet(tweet.getId());
        System.out.println("retweeted!");
    }

    @Override
    public void addActions(int action, long id) throws SQLException {
        if (action == 1) {
            if (!tweetRepositoryImp.read(id).getLikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                tweetRepositoryImp.updateLike(tweetRepositoryImp.read(id).getId(), authenticationServiceImp.getLoggedUser().getId());
                tweetRepositoryImp.read(id).getLikes_ids().add(authenticationServiceImp.getLoggedUser().getId());
                if (tweetRepositoryImp.read(id).getDislikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                    tweetRepositoryImp.deleteDislike(id, authenticationServiceImp.getLoggedUser().getId());
                    System.out.println("liked!");
                }
            } else {
                System.out.println("You are already liked!");
            }
        } else if (action == 2) {
            if (!tweetRepositoryImp.read(id).getDislikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                tweetRepositoryImp.updateDislike(tweetRepositoryImp.read(id).getId(), authenticationServiceImp.getLoggedUser().getId());
                tweetRepositoryImp.read(id).getDislikes_ids().add(authenticationServiceImp.getLoggedUser().getId());
                if (tweetRepositoryImp.read(id).getLikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                    tweetRepositoryImp.deleteLike(id, authenticationServiceImp.getLoggedUser().getId());
                    System.out.println("disliked!");
                }
            } else {
                System.out.println("You are already disliked!");
            }
        }
    }

    @Override
    public void editTweetText(String newText, Tweet chosenTweet) throws SQLException {
        tweetRepositoryImp.updateText(chosenTweet, newText);
        System.out.println("successful!");
    }

    @Override
    public void displayTweet(Tweet choosenTweet) throws SQLException {
        if (!choosenTweet.isRetweeted()) {
            System.out.println("---------");
            System.out.println(choosenTweet.getUser().getDisplayName());
            System.out.println("Tweet at  " + choosenTweet.getCreateDate());
            System.out.println("\n" + choosenTweet.getContent());
            if (!choosenTweet.getBrief().isEmpty()) {
                System.out.println("\n brief: " + choosenTweet.getBrief());
            }
            System.out.print("likes: " + choosenTweet.getLikes_ids().size());
            System.out.println("\tdislikes: " + choosenTweet.getDislikes_ids().size());
            System.out.println("tweet id: " + choosenTweet.getId());
            System.out.println("viewed " + choosenTweet.getViews_ids().size() + " times\n");

            addView(choosenTweet);
        }


    }

    @Override
    public void displayRetweet(Tweet choosenTweet, int tabs) throws SQLException {
        if (choosenTweet.isRetweeted()) {
            System.out.println();
            for (int i = 0; i < tabs; i++) {
                System.out.print("\t");
            }
            System.out.println("replayed to tweetId " + tweetRepositoryImp.getTweetOfRetweet(choosenTweet).getId());
            for (int i = 0; i < tabs; i++) {
                System.out.print("\t");
            }
            System.out.println("Retweeted at  " + choosenTweet.getCreateDate());
            for (int i = 0; i < tabs; i++) {
                System.out.print("\t");
            }
            System.out.println(choosenTweet.getUser().getDisplayName() + "\n");
            for (int i = 0; i < tabs; i++) {
                System.out.print("\t");
            }
            System.out.println(choosenTweet.getContent() + "\n");
            if (!choosenTweet.getBrief().isEmpty()) {
                for (int i = 0; i < tabs; i++) {
                    System.out.print("\t");
                }
                System.out.println("brief: " + choosenTweet.getBrief());
            }
            for (int i = 0; i < tabs; i++) {
                System.out.print("\t");
            }
            System.out.print("likes: " + choosenTweet.getLikes_ids().size());
            System.out.println("\tdislikes: " + choosenTweet.getDislikes_ids().size());
            for (int i = 0; i < tabs; i++) {
                System.out.print("\t");
            }
            System.out.println("tweet id: " + choosenTweet.getId());
            for (int i = 0; i < tabs; i++) {
                System.out.print("\t");
            }
            System.out.println("viewed " + choosenTweet.getViews_ids().size() + " times");
        }
        addView(choosenTweet);

    }

    private void addView(Tweet choosenTweet) throws SQLException {
        if (!choosenTweet.getViews_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
            tweetRepositoryImp.updateView(choosenTweet.getId(), authenticationServiceImp.getLoggedUser().getId());
            choosenTweet.getViews_ids().add(authenticationServiceImp.getLoggedUser().getId());
        }
        if (!choosenTweet.getRetweets().isEmpty()) {
            for (Long retweet : choosenTweet.getRetweets()) {
                int tabs = countOfTabs(tweetRepositoryImp.read(retweet));
                displayRetweet(tweetRepositoryImp.read(retweet), tabs);
            }
        }
    }

    public void deleteTweetRetweet(Tweet tweet) throws SQLException {
        if (!tweet.getRetweets().isEmpty()) {
            tweetRepositoryImp.deleteAllRetweets(tweet.getId());
            for (Long retweet : tweet.getRetweets()) {
                deleteTweetRetweet(tweetRepositoryImp.read(retweet));
            }
        }
        tweetRepositoryImp.deleteAllLikes(tweet.getId());
        tweetRepositoryImp.deleteAllDislikes(tweet.getId());
        tweetRepositoryImp.deleteAllViews(tweet.getId());
        tagRepositoryImp.delete(tweet.getId());
        if (tweet.isRetweeted()) {
            tweetRepositoryImp.deleteRetweetRecords(tweet.getId());
        }
        tweetRepositoryImp.delete(tweet.getId());
    }

    private int countOfTabs(Tweet retweet) throws SQLException {
        int count = 0;
        while (retweet.isRetweeted()) {
            count++;
            if (tweetRepositoryImp.getTweetOfRetweet(retweet).isRetweeted()) {
                count++;
            }
            retweet = tweetRepositoryImp.getTweetOfRetweet(retweet);
        }

        return count;
    }

    @Override
    public List<Tweet> getAllTweets() throws SQLException{
        return tweetRepositoryImp.all();
    }

    @Override
    public List<Tweet> getTweetsOfAUser(User user) throws SQLException{
        return tweetRepositoryImp.getTweetsOfAUser(user);
    }

    @Override
    public Tweet getTweetById(long id) throws SQLException {
        return tweetRepositoryImp.read(id);
    }

    @Override
    public boolean isTweetIdExist(long id) throws SQLException {
        return tweetRepositoryImp.read(id) != null;
    }
}
