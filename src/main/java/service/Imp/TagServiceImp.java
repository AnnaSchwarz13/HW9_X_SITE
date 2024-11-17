package service.Imp;

import entities.Tag;
import exceptions.TagException;
import repository.Imp.TagRepositoryImp;
import repository.Imp.TweetRepositoryImp;
import service.TagService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TagServiceImp implements TagService {
    Scanner sc = new Scanner(System.in);
    TagRepositoryImp tagRepositoryImp = new TagRepositoryImp();
    TweetRepositoryImp tweetRepositoryImp = new TweetRepositoryImp();

    @Override
    public List<Tag> setTweetTags() throws SQLException {
        List<Tag> tags = new ArrayList<>();
        if (tagRepositoryImp.findCount() == 0) {
            System.out.println("no tag yet");
        } else {
            System.out.println("Please enter the tags of the tweet: \n at the end enter -1");
            for (Tag tag : tagRepositoryImp.all()) {
                System.out.println(tag.getTitle());
            }
        }
        System.out.println("For add a tag enter 1");
        while (true) {
            String tagName = this.sc.nextLine();
            if (tagName.equals("-1")) {
                break;
            }
            if (tagName.equals("1")) {
                System.out.println("Please enter your tag name");
                String newTagName = this.sc.nextLine();
                if (tagRepositoryImp.findTagByTile(newTagName) != null) {
                    System.out.println("Tag already exists");
                } else {
                    Tag newTag = new Tag(newTagName);
                    tagRepositoryImp.create(newTag);
                    System.out.println("New tags are there please choose a tag: \n at the end enter -1");
                    for (Tag tag : tagRepositoryImp.all()) {
                        System.out.println(tag.getTitle());
                    }
                    System.out.println("For add a tag enter 1");
                }

            } else {
                Tag newTag = tagRepositoryImp.findTagByTile(tagName);
                if (newTag != null) {
                    tags.add(newTag);
                }
            }
        }
        return tags;
    }

    @Override
    public void isTagExist(String title) throws SQLException, TagException {
        if (tagRepositoryImp.findTagByTile(title) == null)
            throw new TagException("That tag does not exist");
    }

    @Override
    public void updateTagList(List<Tag> tags, long tweetId) throws SQLException {
        tagRepositoryImp.delete(tweetId);
        tagRepositoryImp.setTweetTag(tags, tweetRepositoryImp.read(tweetId));
        System.out.println("Tag list updated successfully");
    }
}
