package service.Imp;

import entities.Tag;
import exceptions.TagException;
import repository.Imp.TagRepositoryImp;
import repository.Imp.TweetRepositoryImp;
import service.TagService;

import java.sql.SQLException;
import java.util.List;

public class TagServiceImp implements TagService {
    TagRepositoryImp tagRepositoryImp = new TagRepositoryImp();
    TweetRepositoryImp tweetRepositoryImp = new TweetRepositoryImp();

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

    public void showAllTags() throws SQLException, TagException {
        if (tagRepositoryImp.findCount() == 0) {
            throw new TagException("no tag yet");
        } else {
            System.out.println("Please enter the tags of the tweet: \n at the end enter -1");
            for (Tag tag : tagRepositoryImp.all()) {
                System.out.println(tag.getTitle());
            }
        }
        System.out.println("For add a tag enter 1");
    }

    public void addNewTag(String newTagName) throws SQLException, TagException {
        if (tagRepositoryImp.findTagByTile(newTagName) != null) {
            throw new TagException("Tag already exists");
        } else {
            Tag newTag = new Tag(newTagName);
            tagRepositoryImp.create(newTag);
            System.out.println("New tags are there please choose a tag: \n at the end enter -1");
            for (Tag tag : tagRepositoryImp.all()) {
                System.out.println(tag.getTitle());
            }
            System.out.println("For add a tag enter 1");
        }
    }

    public Tag addNewTagTweet(String newTagName) throws SQLException, TagException {
        return tagRepositoryImp.findTagByTile(newTagName);
    }
}

