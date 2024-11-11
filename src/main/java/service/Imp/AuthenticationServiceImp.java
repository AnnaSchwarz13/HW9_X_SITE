package service.Imp;

import entities.User;
import repository.Imp.UserRepositoryImp;
import service.AuthenticationService;

public class AuthenticationServiceImp implements AuthenticationService {
    static UserRepositoryImp userRepositoryImp = new UserRepositoryImp();
    private static User loggedInUser;

    @Override
    public void setLoggedUser(User user) {
        if (loggedInUser == null) {
            loggedInUser = user;
        }
    }

    @Override
    public void logout() {
        if (loggedInUser != null) {
            loggedInUser = null;
        }
    }

    @Override
    public User getLoggedUser() {
        return loggedInUser;
    }

    @Override
    public boolean isUserNameNew(String username) {
        if (userRepositoryImp.all() != null) {
            for (User checkingUser : userRepositoryImp.all()) {
                if (checkingUser.getUsername().equals(username)) {
                    return false;
                }
            }
        }
        return true;
    }
}
