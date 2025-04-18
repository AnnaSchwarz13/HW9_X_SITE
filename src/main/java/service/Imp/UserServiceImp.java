package service.Imp;

import entities.User;
import exceptions.UserException;
import repository.Imp.UserRepositoryImp;
import service.UserService;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UserServiceImp implements UserService {

    UserRepositoryImp userRepositoryImp = new UserRepositoryImp();
    AuthenticationServiceImp authenticationServiceImp = new AuthenticationServiceImp();
    AuthenticationServiceImp.PasswordAuthentication passwordAuthenticationImp = new AuthenticationServiceImp.PasswordAuthentication();

    @Override
    public void userSignup(String username, String password, String email, String bio, String displayName) {
        User signingUser = new User(username, passwordAuthenticationImp.hash(password.toCharArray()), displayName, email, bio);
        try {
            userRepositoryImp.create(signingUser);
            System.out.println("User signed up successfully");
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void userLoginUsername(String username, String password) throws  UserException {
        try {
            if (userRepositoryImp.all() != null) {
                for (User checkingUser : userRepositoryImp.all()) {
                    if (checkingUser.getUsername().equals(username)) {
                        if (passwordAuthenticationImp.authenticate(password.toCharArray(), checkingUser.getPassword())) {
                            authenticationServiceImp.setLoggedUser(userRepositoryImp.findByUsername(username));
                            System.out.println("User logged in successfully...");
                            return;

                        }
                        break;
                    }
                }
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new UserException("Username or password is wrong!");
    }

    @Override
    public void userLoginEmail(String email, String password) throws UserException {
        try {
            if (userRepositoryImp.all() != null) {
                for (User checkingUser : userRepositoryImp.all()) {
                    if (checkingUser.getEmail().equals(email)) {
                        if (passwordAuthenticationImp.authenticate(password.toCharArray(), checkingUser.getPassword())) {
                            authenticationServiceImp.setLoggedUser(userRepositoryImp.findByUsername(checkingUser.getUsername()));
                            System.out.println("User logged in successfully...");
                            return;

                        }
                        break;
                    }
                }
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new UserException("Email or password is wrong!");
    }

    @Override
    public void userLogout() {
        authenticationServiceImp.logout();
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) throws UserException {
        try {
            if (passwordAuthenticationImp.authenticate(oldPassword.toCharArray(), authenticationServiceImp.getLoggedUser().getPassword())) {
                userRepositoryImp.updatePassword(authenticationServiceImp.getLoggedUser().getId(), passwordAuthenticationImp.hash(newPassword.toCharArray()));
                System.out.println("Password changed successfully");
                return;
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new UserException("Wrong password");
    }

    @Override
    public void changeUsername(String oldUsername, String newUsername) throws  UserException {
        if (authenticationServiceImp.getLoggedUser().getUsername().equals(oldUsername)) {
            try {
                if (authenticationServiceImp.isUsernameNew(newUsername)) {
                    userRepositoryImp.updateUsername(authenticationServiceImp.getLoggedUser().getId(), newUsername);
                    System.out.println("Username changed successfully");
                    return;
                }
            }catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            throw new UserException("this username is already in use");

        }
        throw new UserException("Wrong username");
    }

    @Override
    public void changeEmail(String oldEmail, String newEmail) throws UserException {
        if (authenticationServiceImp.getLoggedUser().getEmail().equals(oldEmail)) {
            try {
                if (authenticationServiceImp.isEmailNew(newEmail)) {
                    userRepositoryImp.updateEmail(authenticationServiceImp.getLoggedUser().getId(), newEmail);
                    System.out.println("Email changed successfully");
                    return;
                }
            }catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            throw new UserException("Email is already in use");
        }
        throw new UserException("Wrong email");
    }

    @Override
    public void changeBio(String newBio)  {
        try {
        userRepositoryImp.updateBio(authenticationServiceImp.getLoggedUser().getId(), newBio);
        System.out.println("Successful!");
            }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void changeDisplayName(String newDisplayName) {
        try {
            userRepositoryImp.updateDisplayName(authenticationServiceImp.getLoggedUser().getId(), newDisplayName);
            System.out.println("Successful!");
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public long numericInput(Scanner scanner){
        long input;
        while (true) {
            try {
                input = scanner.nextLong();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Wrong Input!");
                scanner.nextLine();
            }
        }
        return input;
    }

}