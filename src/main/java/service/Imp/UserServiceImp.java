package service.Imp;

import entities.User;
import entities.enums.Role;
import repository.Imp.UserRepositoryImp;
import service.UserService;

import java.sql.Date;
import java.sql.SQLException;

public class UserServiceImp implements UserService {

    UserRepositoryImp userRepositoryImp = new UserRepositoryImp();
    UserRepositoryImp userRepositoryImp = new UserRepositoryImp();
    AuthenticationServiceImp authenticationServiceImp = new AuthenticationServiceImp();

    @Override
    public void userSignup(String firstName, String lastName, String username,
                           String password, String nationalCode, Date birthday) throws SQLException {

        User signingUser = new User(firstName, lastName, username, password, nationalCode, birthday);
        User user = new User(username, password, Role.AUTHOR);
        userRepositoryImp.create(user);
        userRepositoryImp.create(signingUser);
        System.out.println("Author signed up successfully");
    }

@Override
    public void changePassword(String oldPassword, String newPassword) throws SQLException {
        if (authenticationServiceImp.getLoggedUser().getPassword().equals(oldPassword)) {
            userRepositoryImp.setUpdatePassword(userRepositoryImp.findByUserId(authenticationServiceImp.getLoggedUser().getId()), newPassword);
            System.out.println("Password changed successfully");
            return;
        }
        System.out.println("Wrong password");
    }
    UserRepositoryImp userRepositoryImp = new UserRepositoryImp();
    AuthenticationServiceImp authenticationServiceImp = new AuthenticationServiceImp();

    @Override
    public void userLogin(String username, String password, Role role) throws SQLException {
        for (User checkingUser : userRepositoryImp.all()) {
            if (checkingUser.getUsername().equals(username)) {
                if (checkingUser.getPassword().equals(password)) {
                    if (authenticationServiceImp.checkRole(role, checkingUser)) {
                        authenticationServiceImp.setLoggedUser(userRepositoryImp.findByUsername(username));
                        System.out.println("User logged in successfully...");
                        return;
                    }
                }
                break;
            }
        }
        System.out.println("Username or password is wrong!");
    }
    @Override
    public void userLogout() {
        authenticationServiceImp.logout();
    }
}