package service;


import entities.User;

public interface AuthenticationService {
    void setLoggedUser(User user);
    User getLoggedUser();
    void logout();
    boolean isUserNameNew(String username);
}
