package service.Imp;
import entities.Tweet;
import entities.Category;
import entities.Tag;
import entities.enums.ArticleStatus;
import repository.Imp.TweetRepositoryImp;
import repository.Imp.UserRepositoryImp;
import repository.Imp.TagRepositoryImp;
import service.TweetService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class TweetServiceImp implements TweetService {
    TweetRepositoryImp tweetRepositoryImp = new TweetRepositoryImp();
    UserRepositoryImp userRepositoryImp = new UserRepositoryImp();
    TagRepositoryImp tagRepositoryImp = new TagRepositoryImp();
    TagServiceImp tagServiceImp = new TagServiceImp();
    CategoryServiceImp categoryServiceImp = new CategoryServiceImp();
    AuthenticationServiceImp authenticationServiceImp = new AuthenticationServiceImp();
    Scanner sc = new Scanner(System.in);

    @Override
    public void addArticle() throws SQLException {
        Category articleCategory = categoryServiceImp.chooseCategory();
        System.out.println("Enter title: ");
        String title = sc.nextLine();
        System.out.println("Enter article text: ");
        String articleText = sc.nextLine();
        List<Tag> brief = tagServiceImp.setArticleTags();
        Tweet tweet = new Tweet(userRepositoryImp.findByUserId(authenticationServiceImp.getLoggedUser().getId()), title, articleCategory, articleText);
        tweet = tweetRepositoryImp.create(tweet);
        System.out.println(tweet.getId());
        tagRepositoryImp.setTweetTag(brief, tweet);
    }
@Override
    public void showAnArticleList(List<Tweet> tweets) throws SQLException {
        if (tweets.isEmpty()) {
            System.out.println("there is no article");
        } else {
            System.out.println("Please enter the title of the article's list \n for see more details: ");

            for (Tweet tempTweet : tweets) {
                System.out.println(tempTweet.getTitle());
            }

            String title = this.sc.nextLine();
            Tweet userChoice = tweetRepositoryImp.findArticleByTile(title);
            if (userChoice != null) {
                displayArticle(userChoice);
            }
        }

    }

    @Override
    public void changeArticleStatus(Tweet choosenTweet) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("The selected article status is " + choosenTweet.getStatus());
        System.out.println("If you dont want to change the status of this article, please enter -1");
        System.out.println("Please enter 1 to following change: ");
        int choose;
        if (choosenTweet.getStatus() == ArticleStatus.NOT_PUBLISHED) {
            System.out.println("Send request to get published article");
            choose = scanner.nextInt();
            if (choose == 1) {
                tweetRepositoryImp.updateStatusPending(choosenTweet);
            }
        } else if (choosenTweet.getStatus() == ArticleStatus.PUBLISHED) {
            Tweet exsistedAuthorTweet = tweetRepositoryImp.findArticleByTile(choosenTweet.getTitle());
            System.out.println("Remove article from published articles");
            choose = scanner.nextInt();
            if (choose == 1) {
                tweetRepositoryImp.updateStatusNotPublished(choosenTweet);
            }
        } else if (choosenTweet.getStatus() == ArticleStatus.PENDING) {
            Tweet exsistedAuthorTweet = tweetRepositoryImp.findArticleByTile(choosenTweet.getTitle());

            System.out.println("Cancel request to get published articles");
            choose = scanner.nextInt();
            if (choose == 1) {
                tweetRepositoryImp.updateStatusNotPublished(choosenTweet);
            }
        }
        System.out.println("The selected article status is " + TweetRepositoryImp.read(choosenTweet.getId()).getStatus());
    }
@Override
    public void changeDetailsOfArticle(Tweet choosenTweet) throws SQLException {
        System.out.println("Which do you want to edit?");
        System.out.println("""
                        1.Edit title
                        2.Edit category
                        3.Edit content
                        4.Edit TagList\s
                """
        );
        int choose = sc.nextInt();
        if (choose == 1) {
            System.out.println("Please enter the new title:");
            String newTitle = sc.nextLine() + sc.nextLine();
            tweetRepositoryImp.updateTitle(choosenTweet, newTitle);
            System.out.println("successful!");

        } else if (choose == 2) {
            Category newCategory = categoryServiceImp.chooseCategory();
            tweetRepositoryImp.updateCategory(choosenTweet, newCategory);
            System.out.println("successful!");

        } else if (choose == 3) {
            System.out.println("Please enter the new content:");
            String newText = sc.nextLine() + sc.nextLine();
            tweetRepositoryImp.updateText(choosenTweet, newText);
            System.out.println("successful!");

        } else if (choose == 4) {
            List<Tag> newTags = choosenTweet.getBrief();
            while (true) {
                System.out.println("Your articles tag are there");
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
                    tweetRepositoryImp.setLastUpdateDate(choosenTweet);
                    System.out.println("Tag list updated successfully");
                    break;
                }
            }
        }

    }
@Override
    public void displayArticle(Tweet choosenTweet) {
        System.out.println(choosenTweet.getTitle());
        System.out.println("category : " + choosenTweet.getCategory().getTitle());
        System.out.println("Status : " + choosenTweet.getStatus());
        System.out.println("created date : " + choosenTweet.getCreateDate());
        System.out.println("last update date : " + choosenTweet.getLastUpdateDate());
        if (choosenTweet.getStatus() == ArticleStatus.PUBLISHED) {
            System.out.println("published date : " + choosenTweet.getPublishDate());
        }
        System.out.println("\n" + choosenTweet.getContent());
        if (choosenTweet.getBrief() != null) {
            System.out.println("\n brief: " + choosenTweet.getBrief());
        }
        System.out.println(choosenTweet.getUser().getUsername() + " " + choosenTweet.getUser().getLastName());
    }

}
