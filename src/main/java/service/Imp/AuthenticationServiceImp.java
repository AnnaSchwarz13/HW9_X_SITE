package service.Imp;

import entities.enums.Role;
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
    public boolean checkRole(Role role, User user) {
        return user.getRole().equals(role);
    }
@Override
    public User getLoggedUser() {
        return loggedInUser;
    }
@Override
    public boolean isUserNameNew(String username, Role role) {
        for (User checkingUser : userRepositoryImp.all()) {
            if (checkingUser.getUsername().equals(username)) {
                if (checkingUser.getRole().equals(role)) {
                    return false;
                }
            }
        }
        return true;
    }
}
