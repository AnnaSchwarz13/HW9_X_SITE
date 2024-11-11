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
    public void addTweet() throws SQLException {
        System.out.println("Enter tweet text: ");
        String tweetText = sc.nextLine();
        List<Tag> brief = tagServiceImp.setArticleTags();
        Tweet tweet = new Tweet(authenticationServiceImp.getLoggedUser(), tweetText);
        tweet = tweetRepositoryImp.create(tweet);
        tagRepositoryImp.setTweetTag(brief, tweet);
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
                    //add to view
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
                    if (action == 1) {}
                    else if (action == 2) {}
                    else if (action == 3) {}
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
