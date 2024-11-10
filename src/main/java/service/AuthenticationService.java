package service;

import entities.enums.Role;

public interface AuthenticationService {
    void setLoggedUser(User user);
    User getLoggedUser();
    void logout();
    boolean checkRole(Role role, User user);
    boolean isUserNameNew(String username, Role role);
}
