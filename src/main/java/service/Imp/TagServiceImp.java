package service.Imp;

import entities.Tag;
import exceptions.TagException;
import repository.Imp.TagRepositoryImp;
import repository.Imp.TweetRepositoryImp;
import service.TagService;

import java.sql.SQLException;
import java.util.LinkedList;
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
    public void updateTagList(List<Long> tags, long tweetId) throws SQLException {
        tagRepositoryImp.delete(tweetId);
        tagRepositoryImp.setTweetTag(tags, tweetRepositoryImp.read(tweetId));
        System.out.println("Tag list updated successfully");
    }

    public void showAllTags() throws SQLException {
        if (tagRepositoryImp.findCount() == 0) {
            System.out.println("no tag yet");
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

    public long addNewTagTweet(String newTagName) throws SQLException, TagException {
        if(tagRepositoryImp.findTagByTile(newTagName) == null) {
            throw new TagException("Tag not exists");
        }
        return tagRepositoryImp.findTagByTile(newTagName).getId();
    }

    @Override
    public Tag getTagById(long id) throws SQLException {
        return tagRepositoryImp.read(id);
    }

    public List<Tag> idToTags(List<Long> tags) throws SQLException{
        List<Tag> tagList = new LinkedList<>();
        for (Long id : tags) {
            tagList.add(tagRepositoryImp.read(id));
        }
        return tagList;
    }

    public List<Long> removeDuplicates(List<Long> tags) {
        List<Long> tagList = new LinkedList<>();
        for (Long id : tags) {
            if (!tagList.contains(id)) {
                tagList.add(id);
            }
        }
        return tagList;
    }
}

