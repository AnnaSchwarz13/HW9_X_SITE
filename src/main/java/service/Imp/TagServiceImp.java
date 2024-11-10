package service.Imp;

import entities.Tag;
import repository.Imp.TagRepositoryImp;
import service.TagService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TagServiceImp implements TagService {
    Scanner sc = new Scanner(System.in);
    TagRepositoryImp tagRepositoryImp = new TagRepositoryImp();
@Override
    public List<Tag> setArticleTags() throws SQLException {
        List<Tag> tags = new ArrayList<>();
        System.out.println("Please enter the tags of the article: \n at the end enter -1");
        for (Tag tag : tagRepositoryImp.all()) {
            System.out.println(tag.getTitle());
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
                Tag newTag =tagRepositoryImp.findTagByTile(tagName);
                if (newTag != null) {
                    tags.add(newTag);
                }
            }
        }
        return tags;
    }



}
