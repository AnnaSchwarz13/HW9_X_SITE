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
    TweetRepositoryImp tweetRepositoryImp = new TweetRepositoryImp();
    TagRepositoryImp tagRepositoryImp = new TagRepositoryImp();
    TagServiceImp tagServiceImp = new TagServiceImp();
    AuthenticationServiceImp authenticationServiceImp = new AuthenticationServiceImp();
    Scanner sc = new Scanner(System.in);

    @Override
    public Tweet addTweet() throws SQLException {
        System.out.println("Enter tweet text: ");
        String tweetText = sc.nextLine();
        List<Tag> brief = tagServiceImp.setArticleTags();
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
                    if (!tempTweet.getViews_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                        tweetRepositoryImp.updateActions(tempTweet.getId()
                                , authenticationServiceImp.getLoggedUser().getId(), "view");
                        tempTweet.getViews_ids().add(authenticationServiceImp.getLoggedUser().getId());
                    }
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
                            if (TweetRepositoryImp.read(id).getDislikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())){
                                tweetRepositoryImp.deleteActions(id,authenticationServiceImp.getLoggedUser().getId(),"dislike");
                            }
                        } else {
                            System.out.println("You are already liked!");
                        }
                    } else if (action == 2) {
                        if (!TweetRepositoryImp.read(id).getDislikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())) {
                            tweetRepositoryImp.updateActions(TweetRepositoryImp.read(id).getId()
                                    , authenticationServiceImp.getLoggedUser().getId(), "dislike");
                            TweetRepositoryImp.read(id).getDislikes_ids().add(authenticationServiceImp.getLoggedUser().getId());
                            if (TweetRepositoryImp.read(id).getLikes_ids().contains(authenticationServiceImp.getLoggedUser().getId())){
                                tweetRepositoryImp.deleteActions(id,authenticationServiceImp.getLoggedUser().getId(),"like");
                            }
                        } else {
                            System.out.println("You are already disliked!");
                        }

                    } else if (action == 3) {
                        System.out.println("replay:");
                        Tweet tweet = addTweet();
                            tweetRepositoryImp.updateActions(TweetRepositoryImp.read(id).getId()
                                    , tweet.getId(), "retweet");
                            TweetRepositoryImp.read(id).getRetweets().add(tweet);
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
                    List<Tag> newTagsToAdd = tagServiceImp.setArticleTags();
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
        }
        else if(choose == 3){
            System.out.println("Warning!\nthis is your tweet it will be removed \nwith all like and e.x.");
            displayTweet(choosenTweet);
            System.out.println("""
                    1.CONFIRM
                    2.REJECT""");
            int action = sc.nextInt();
            if (action == 1) {
                tweetRepositoryImp.deleteRecords(choosenTweet.getId(),"like");
                tweetRepositoryImp.deleteRecords(choosenTweet.getId(),"dislike");
                tweetRepositoryImp.deleteRecords(choosenTweet.getId(),"retweet");//just records?
                tweetRepositoryImp.deleteRecords(choosenTweet.getId(),"view");
                tweetRepositoryImp.delete(choosenTweet.getId());
            }
            else if (action == 2) {
                System.out.println("Action canceled !");
            }

        }

    }

    @Override
    public void displayTweet(Tweet choosenTweet) {
        System.out.println(choosenTweet.getUser().getDisplayName());
        System.out.println("tweeted at  " + choosenTweet.getCreateDate());
        System.out.println("\n" + choosenTweet.getContent());
        if (choosenTweet.getBrief() != null) {
            System.out.println("\n brief: " + choosenTweet.getBrief());
        }
        System.out.println("tweet id: " + choosenTweet.getId());

    }

}
