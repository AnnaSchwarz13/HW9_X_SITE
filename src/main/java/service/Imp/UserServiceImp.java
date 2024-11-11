package service.Imp;

import entities.User;
import repository.Imp.UserRepositoryImp;
import service.UserService;

import java.sql.SQLException;

public class UserServiceImp implements UserService {

    UserRepositoryImp userRepositoryImp = new UserRepositoryImp();
    AuthenticationServiceImp authenticationServiceImp = new AuthenticationServiceImp();

    @Override
    public void userSignup(String username, String password,  String email ,String bio, String displayName) throws SQLException {
        User signingUser = new User(username,password,displayName,email,bio);
        userRepositoryImp.create(signingUser);
        System.out.println("Author signed up successfully");
    }


    @Override
    public void userLoginUsername(String username, String password) throws SQLException {
        if (userRepositoryImp.all() != null) {
            for (User checkingUser : userRepositoryImp.all()) {
                if (checkingUser.getUsername().equals(username)) {
                    if (checkingUser.getPassword().equals(password)) {
                        authenticationServiceImp.setLoggedUser(userRepositoryImp.findByUsername(username));
                        System.out.println("User logged in successfully...");
                        return;

                    }
                    break;
                }
            }
        }
        System.out.println("Username or password is wrong!");
    }
    @Override
    public void userLoginEmail(String email, String password) throws SQLException {
        if (userRepositoryImp.all() != null) {
            for (User checkingUser : userRepositoryImp.all()) {
                if (checkingUser.getEmail().equals(email)) {
                    if (checkingUser.getPassword().equals(password)) {
                        authenticationServiceImp.setLoggedUser(userRepositoryImp.findByUsername(checkingUser.getUsername()));
                        System.out.println("User logged in successfully...");
                        return;

                    }
                    break;
                }
            }
        }
        System.out.println("Email or password is wrong!");
    }

    @Override
    public void userLogout() {
        authenticationServiceImp.logout();
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) throws SQLException {
        if (authenticationServiceImp.getLoggedUser().getPassword().equals(oldPassword)) {
            userRepositoryImp.setUpdatePassword(UserRepositoryImp.read(authenticationServiceImp.getLoggedUser().getId()), newPassword);
            System.out.println("Password changed successfully");
            return;
        }
        System.out.println("Wrong password");
    }

    @Override
    public void changeProfile() {

    }
}