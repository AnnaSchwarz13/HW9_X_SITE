package service;

import entities.Tag;

import java.sql.SQLException;
import java.util.List;

public interface TagService {
    List<Tag> setArticleTags() throws SQLException;
}
