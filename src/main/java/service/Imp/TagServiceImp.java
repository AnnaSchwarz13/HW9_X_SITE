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
    public void isTagExist(String title) throws TagException {
        try{
            if (tagRepositoryImp.findTagByTile(title) == null)
                throw new TagException("That tag does not exist");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateTagList(List<Long> tags, long tweetId)  {
        try {
            tagRepositoryImp.delete(tweetId);
            tagRepositoryImp.setTweetTag(tags, tweetRepositoryImp.read(tweetId));
            System.out.println("Tag list updated successfully");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void showAllTags() {
        try {
            if (tagRepositoryImp.findCount() == 0) {
                System.out.println("no tag yet");
            } else {
                System.out.println("Please enter the tags of the tweet: \n at the end enter -1");
                for (Tag tag : tagRepositoryImp.all()) {
                    System.out.println(tag.getTitle());
                }
            }
            System.out.println("For add a tag enter 1");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void addNewTag(String newTagName) throws  TagException {
        try {
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
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public long addNewTagTweet(String newTagName) throws  TagException {
        try {
            if (tagRepositoryImp.findTagByTile(newTagName) == null) {
                throw new TagException("Tag not exists");
            }
            return tagRepositoryImp.findTagByTile(newTagName).getId();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return 0;
    }

    @Override
    public Tag getTagById(long id)  {
        try {
            return tagRepositoryImp.read(id);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Tag> idToTags(List<Long> tags) {
        List<Tag> tagList = new LinkedList<>();
        for (Long id : tags) {
            try {
                tagList.add(tagRepositoryImp.read(id));
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
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

