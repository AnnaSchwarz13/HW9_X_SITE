package service;

import entities.Tag;
import exceptions.TagException;

import java.util.List;

public interface TagService {
    void isTagExist(String title) throws TagException;

    void updateTagList(List<Long> tags, long tweetId);

    void showAllTags();

    void addNewTag(String newTagName) throws TagException;

    long addNewTagTweet(String newTagName) throws TagException;

    Tag getTagById(long id);

    List<Long> removeDuplicates(List<Long> tags);
}
