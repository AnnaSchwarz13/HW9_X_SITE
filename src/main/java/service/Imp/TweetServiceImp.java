package service.Imp;

import entities.Tweet;
import entities.User;
import exceptions.TweetException;
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
    public Tweet addTweet(String tweetText, List<Long> brief) {
        Tweet tweet = new Tweet(authenticationServiceImp.getLoggedUser(), tweetText);
        try {
            tweet = tweetRepositoryImp.create(tweet);
            tagRepositoryImp.setTweetTag(brief, tweet);
            System.out.println("Tweeted!!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return tweet;
    }

    @Override
    public void addRetweet(String retweetText, long id, List<Long> brief) {
        Tweet tweet = addTweet(retweetText, brief);
        try {
            tweetRepositoryImp.updateRetweet(tweetRepositoryImp.read(id).getId(), tweet.getId());
            tweetRepositoryImp.read(id).getRetweets().add(tweet.getId());
            tweetRepositoryImp.setRetweet(tweet.getId());
            System.out.println("retweeted!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addDislike(long id) throws TweetException {
        try {
            if (tweetRepositoryImp.read(id).getLikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                tweetRepositoryImp.deleteLike(id, authenticationServiceImp.getLoggedUser().getId());
            }
            if (!tweetRepositoryImp.read(id).getDislikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                if (!tweetRepositoryImp.read(id).getLikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                    tweetRepositoryImp.updateDislike(tweetRepositoryImp.read(id).getId(), authenticationServiceImp.getLoggedUser().getId());
                    tweetRepositoryImp.read(id).getDislikes_ids().add(authenticationServiceImp.getLoggedUser().getId());
                    System.out.println("disliked!");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
               throw new TweetException("You are already disliked!");

    }

    @Override
    public void addLike(long id) throws TweetException {
        try {
            if (tweetRepositoryImp.read(id).getDislikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                tweetRepositoryImp.deleteDislike(id, authenticationServiceImp.getLoggedUser().getId());
            }
            if (!tweetRepositoryImp.read(id).getLikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                if (!tweetRepositoryImp.read(id).getDislikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                    tweetRepositoryImp.updateLike(tweetRepositoryImp.read(id).getId(), authenticationServiceImp.getLoggedUser().getId());
                    tweetRepositoryImp.read(id).getLikes_ids().add(authenticationServiceImp.getLoggedUser().getId());
                    System.out.println("liked!");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
            throw new TweetException("You are already liked!");
    }


    @Override
    public void editTweetText(String newText, Tweet chosenTweet) {
        try {
            tweetRepositoryImp.updateText(chosenTweet, newText);
            System.out.println("successful!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void displayTweet(Tweet choosenTweet) {
        if (!choosenTweet.isRetweeted()) {
            System.out.println("---------");
            System.out.println(choosenTweet.getUser().getDisplayName());
            System.out.println("Tweet at  " + choosenTweet.getCreateDate());
            System.out.println("\n" + choosenTweet.getContent());
            if (!choosenTweet.getBrief().isEmpty()) {
                System.out.println("\nbrief: " + tagServiceImp.idToTags(choosenTweet.getBrief()));
            }
            System.out.print("likes: " + choosenTweet.getLikes_ids().size());
            System.out.println("\tdislikes: " + choosenTweet.getDislikes_ids().size());
            System.out.println("tweet id: " + choosenTweet.getId());
            System.out.println("viewed " + choosenTweet.getViews_ids().size() + " times\n");

            addView(choosenTweet);
        }
    }

    @Override
    public void displayRetweet(Tweet choosenTweet, int tabs) {
        if (choosenTweet.isRetweeted()) {
            System.out.println();
            for (int i = 0; i < tabs; i++) {
                System.out.print("\t");
            }
            try {
                System.out.println("replayed to tweetId " + tweetRepositoryImp.getTweetOfRetweet(choosenTweet).getId());
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
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
                System.out.println("brief: " + tagServiceImp.idToTags(choosenTweet.getBrief()));
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
            System.out.println("viewed " + choosenTweet.getViews_ids().size() + " times\n");
        }
        addView(choosenTweet);

    }

    private void addView(Tweet choosenTweet) {
        try {
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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteTweetRetweet(Tweet tweet) {
        try {
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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private int countOfTabs(Tweet retweet) {
        int count = 0;
        try {
            while (retweet.isRetweeted()) {
                count++;
                if (tweetRepositoryImp.getTweetOfRetweet(retweet).isRetweeted()) {
                    count++;
                }
                retweet = tweetRepositoryImp.getTweetOfRetweet(retweet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return count;
    }

    @Override
    public List<Tweet> getAllTweets() throws TweetException {
        try {
            if (tweetRepositoryImp.all().isEmpty()) {
                throw new TweetException("There is to tweet yet");
            }
            return tweetRepositoryImp.all();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Tweet> getTweetsOfAUser(User user) {
        try {
            return tweetRepositoryImp.getTweetsOfAUser(user);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Tweet getTweetById(long id) {
        try {
            return tweetRepositoryImp.read(id);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean isTweetIdExist(long id) throws TweetException {
        try {
            if (tweetRepositoryImp.read(id) == null) {
                throw new TweetException("Tweet dose not exist");
            }
            return tweetRepositoryImp.read(id) != null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
