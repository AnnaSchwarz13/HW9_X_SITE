package service.Imp;

import entities.Tag;
import entities.Tweet;
import repository.Imp.TagRepositoryImp;
import repository.Imp.TweetRepositoryImp;
import service.TweetService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class TweetServiceImp implements TweetService {
    static TweetRepositoryImp tweetRepositoryImp = new TweetRepositoryImp();
    TagRepositoryImp tagRepositoryImp = new TagRepositoryImp();
    TagServiceImp tagServiceImp = new TagServiceImp();
    static AuthenticationServiceImp authenticationServiceImp = new AuthenticationServiceImp();
    Scanner sc = new Scanner(System.in);

    @Override
    public Tweet addTweet() throws SQLException {
        System.out.println("Enter tweet text: ");
        String tweetText = sc.nextLine() + sc.nextLine();
        List<Tag> brief = tagServiceImp.setTweetTags();
        Tweet tweet = new Tweet(authenticationServiceImp.getLoggedUser(), tweetText);
        tweet = tweetRepositoryImp.create(tweet);
        tagRepositoryImp.setTweetTag(brief, tweet);
        System.out.println("Tweeted!!");
        return tweet;
    }

    @Override
    public void showTweetList(List<Tweet> tweets) throws SQLException {
        if (tweets.isEmpty()) {
            System.out.println("there is no Tweet");
        } else {
            while (true) {
                System.out.println("for more action enter tweet's id else -1 ");

                for (Tweet tempTweet : tweets) {
                    displayTweet(tempTweet);

                }

                long id = sc.nextLong();

                if (id == -1) {
                    break;
                } else if (TweetRepositoryImp.read(id) != null) {
                    System.out.println("""
                            1.Like!
                            2.Dislike!
                            3.Retweet""");
                    int action = sc.nextInt();
                    if (action == 1) {
                        if (!TweetRepositoryImp.read(id).getLikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                            tweetRepositoryImp.updateActions(TweetRepositoryImp.read(id).getId()
                                    , authenticationServiceImp.getLoggedUser().getId(), "like");
                            TweetRepositoryImp.read(id).getLikes_ids().add(authenticationServiceImp.getLoggedUser().getId());
                            if (TweetRepositoryImp.read(id).getDislikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                                tweetRepositoryImp.deleteActions(id, authenticationServiceImp.getLoggedUser().getId(), "dislike");
                                System.out.println("liked!");
                            }
                        } else {
                            System.out.println("You are already liked!");
                        }
                    } else if (action == 2) {
                        if (!TweetRepositoryImp.read(id).getDislikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                            tweetRepositoryImp.updateActions(TweetRepositoryImp.read(id).getId()
                                    , authenticationServiceImp.getLoggedUser().getId(), "dislike");
                            TweetRepositoryImp.read(id).getDislikes_ids().add(authenticationServiceImp.getLoggedUser().getId());
                            if (TweetRepositoryImp.read(id).getLikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                                tweetRepositoryImp.deleteActions(id, authenticationServiceImp.getLoggedUser().getId(), "like");
                                System.out.println("disliked!");
                            }
                        } else {
                            System.out.println("You are already disliked!");
                        }

                    } else if (action == 3) {
                        System.out.println("replay:");
                        Tweet tweet = addTweet();
                        tweetRepositoryImp.updateActions(TweetRepositoryImp.read(id).getId()
                                , tweet.getId(), "retweet");
                        TweetRepositoryImp.read(id).getRetweets().add(tweet.getId());
                        tweetRepositoryImp.setRetweet(tweet.getId());
                        System.out.println("retweeted!");
                    }
                } else {
                    System.out.println("wrong id");
                }
            }

        }
    }

    @Override
    public void changeDetailsOfTweet(Tweet choosenTweet) throws SQLException {
        System.out.println("Which do you want to edit?");
        System.out.println("""
                        1.Edit content
                        2.Edit TagList\s
                        3.delete
                """
        );
        int choose = sc.nextInt();
        if (choose == 1) {
            System.out.println("Please enter the new content:");
            String newText = sc.nextLine() + sc.nextLine();
            newText = newText +"\n(edited)";
            tweetRepositoryImp.updateText(choosenTweet, newText);
            System.out.println("successful!");

        } else if (choose == 2) {
            List<Tag> newTags = choosenTweet.getBrief();
            while (true) {
                System.out.println("Your tweet's tag(s) are there");
                for (Tag tag : newTags) {
                    System.out.println(tag.getTitle());
                }
                System.out.println("for add more enter 1 \n remove one tag enter 2 \n and at the end -1");
                int choose2 = sc.nextInt();
                if (choose2 == 1) {
                    List<Tag> newTagsToAdd = tagServiceImp.setTweetTags();
                    newTags.addAll(newTagsToAdd);
                }
                if (choose2 == 2) {
                    System.out.println("Please enter a tag name to remove");
                    String tagName = sc.nextLine() + sc.nextLine();
                    if (tagRepositoryImp.findTagByTile(tagName) == null) {
                        System.out.println("That tag does not exist");
                    } else {
                        newTags.removeIf(tag -> tag.getTitle().equals(tagName));
                    }
                }
                if (choose2 == -1) {
                    tagRepositoryImp.delete(choosenTweet.getId());
                    tagRepositoryImp.setTweetTag(newTags, choosenTweet);
                    System.out.println("Tag list updated successfully");
                    break;
                }
            }
        } else if (choose == 3) {
            System.out.println("Warning!\nthis is your tweet it will be removed \nwith all like and e.x.");
            if (!choosenTweet.isRetweeted())
                displayTweet(choosenTweet);
            else {
                System.out.println("---------");
                displayRetweet(choosenTweet);
            }
            System.out.println("""
                    1.CONFIRM
                    2.REJECT""");
            int action = sc.nextInt();
            if (action == 1) {
                deleteTweetRetweet(choosenTweet);
            } else if (action == 2) {
                System.out.println("Action canceled !");
            }

        }

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

    public static void displayRetweet(Tweet choosenTweet) throws SQLException {
        if (choosenTweet.isRetweeted()) {
            System.out.println("\tRetweeted at  " + choosenTweet.getCreateDate());
            System.out.println("\t" + choosenTweet.getUser().getDisplayName());
            System.out.println("\n\t" + choosenTweet.getContent());
            if (!choosenTweet.getBrief().isEmpty()) {
                System.out.println("\n\t brief: " + choosenTweet.getBrief());
            }
            System.out.print("\tlikes: " + choosenTweet.getLikes_ids().size());
            System.out.println("\tdislikes: " + choosenTweet.getDislikes_ids().size());
            System.out.println("\ttweet id: " + choosenTweet.getId());
            System.out.println("\tviewed " + choosenTweet.getViews_ids().size() + " times");
        }
        addView(choosenTweet);

    }

    private static void addView(Tweet choosenTweet) throws SQLException {
        if (!choosenTweet.getViews_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
            tweetRepositoryImp.updateActions(choosenTweet.getId()
                    , authenticationServiceImp.getLoggedUser().getId(), "view");
            choosenTweet.getViews_ids().add(authenticationServiceImp.getLoggedUser().getId());
        }
        if (!choosenTweet.getRetweets().isEmpty()) {
            for (Long retweet : choosenTweet.getRetweets()) {
                System.out.println("\n\treplayed to tweetId " + choosenTweet.getId());
                displayRetweet(TweetRepositoryImp.read(retweet));
            }
        }
    }

    private void deleteTweetRetweet(Tweet tweet) throws SQLException {
        if (!tweet.getRetweets().isEmpty()) {
            tweetRepositoryImp.deleteRecords(tweet.getId(), "retweet");
            for (Long retweet : tweet.getRetweets()) {
                deleteTweetRetweet(TweetRepositoryImp.read(retweet));
            }
        }
        tweetRepositoryImp.deleteRecords(tweet.getId(), "like");
        tweetRepositoryImp.deleteRecords(tweet.getId(), "dislike");
        tweetRepositoryImp.deleteRecords(tweet.getId(), "view");
        tagRepositoryImp.delete(tweet.getId());
        if(tweet.isRetweeted()) {
            tweetRepositoryImp.deleteRecords(tweet.getId(), "tweet");
        }
        tweetRepositoryImp.delete(tweet.getId());
    }

}
