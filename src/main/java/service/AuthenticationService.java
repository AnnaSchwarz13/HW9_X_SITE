package service;


import entities.User;

public interface AuthenticationService {
    void setLoggedUser(User user);

    User getLoggedUser();

    void logout();

    boolean isUsernameNew(String username);

    boolean isEmailNew(String email);
}
