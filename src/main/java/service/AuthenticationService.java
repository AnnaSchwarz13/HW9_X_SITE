package service;


import entities.User;
import exceptions.TweetException;
import exceptions.UserException;

public interface AuthenticationService {
    void setLoggedUser(User user);

    User getLoggedUser();

    void logout();

    boolean isUsernameNew(String username) throws  UserException;

    boolean isEmailNew(String email) throws  UserException;

    boolean isTweetForLoggedInUser(Long id) throws TweetException;
}
