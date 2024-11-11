package service.Imp;

import entities.User;
import repository.Imp.UserRepositoryImp;
import service.UserService;

import java.sql.SQLException;
import java.util.Scanner;

public class UserServiceImp implements UserService {

    UserRepositoryImp userRepositoryImp = new UserRepositoryImp();
    AuthenticationServiceImp authenticationServiceImp = new AuthenticationServiceImp();
    AuthenticationServiceImp.PasswordAuthentication passwordAuthenticationImp = new AuthenticationServiceImp.PasswordAuthentication();
    static Scanner scanner = new Scanner(System.in);

    @Override
    public void userSignup(String username, String password, String email, String bio, String displayName) throws SQLException {
        User signingUser = new User(username, passwordAuthenticationImp.hash(password.toCharArray()), displayName, email, bio);
        userRepositoryImp.create(signingUser);
        System.out.println("User signed up successfully");
    }


    @Override
    public void userLoginUsername(String username, String password) throws SQLException {
        if (userRepositoryImp.all() != null) {
            for (User checkingUser : userRepositoryImp.all()) {
                if (checkingUser.getUsername().equals(username)) {
                    if (passwordAuthenticationImp.authenticate(password.toCharArray(),checkingUser.getPassword())) {
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


    private void changePassword(String oldPassword, String newPassword) throws SQLException {
        if (passwordAuthenticationImp.authenticate(oldPassword.toCharArray(),authenticationServiceImp.getLoggedUser().getPassword())) {
            userRepositoryImp.updateProfile(UserRepositoryImp.read(authenticationServiceImp.getLoggedUser().getId()), passwordAuthenticationImp.hash(newPassword.toCharArray()) ,"password");
            System.out.println("Password changed successfully");
            return;
        }
        System.out.println("Wrong password");
    }

    private void changeUsername(String oldUsername, String newUsername) throws SQLException {
        if (authenticationServiceImp.getLoggedUser().getUsername().equals(oldUsername)) {
            if (authenticationServiceImp.isUsernameNew(newUsername)) {
                userRepositoryImp.updateProfile(authenticationServiceImp.getLoggedUser(), newUsername, "username");
                System.out.println("Username changed successfully");
                return;
            }
            System.out.println("this username is already in use");
            return;
        }
        System.out.println("Wrong username");
    }

    private void changeEmail(String oldEmail, String newEmail) throws SQLException {
        if (authenticationServiceImp.getLoggedUser().getEmail().equals(oldEmail)) {
            if (authenticationServiceImp.isEmailNew(newEmail)) {
                userRepositoryImp.updateProfile(authenticationServiceImp.getLoggedUser(), newEmail, "email");
                System.out.println("Email changed successfully");
                return;
            }
            System.out.println("this email is already in use");
            return;
        }
        System.out.println("Wrong email");
    }


    @Override
    public void changeProfile() throws SQLException {
        System.out.println("Select to change");
        System.out.println("""
                1.Username
                2.Email
                3.Password
                4.DisplayName
                5.Bio""");
        int option = scanner.nextInt();

        if (option == 1) {
            System.out.println("Enter old Username");
            String username = scanner.next();
            System.out.println("Enter new username");
            String newUsername = scanner.next();
            changeUsername(username,newUsername);
        } else if (option == 2) {
            System.out.println("Enter old email");
            String oldEmail = scanner.next();
            System.out.println("Enter new email");
            String newEmail = scanner.next();
            changeEmail(oldEmail,newEmail);
        } else if (option == 3) {
            System.out.println("Enter old password");
            String oldPassword = scanner.next();
            System.out.println("Enter new password");
            String newPassword = scanner.next();
            changePassword(oldPassword,newPassword);
        } else if (option == 4) {
            System.out.println("Enter new DisplayName");
            String newDisplayName = scanner.next();
            userRepositoryImp.updateProfile(authenticationServiceImp.getLoggedUser(), newDisplayName, "displayName");
            System.out.println("Successful!");
        } else if (option == 5) {
            System.out.println("Enter new Bio");
            String newBio = scanner.next();
            userRepositoryImp.updateProfile(authenticationServiceImp.getLoggedUser(), newBio, "bio");
            System.out.println("Successful!");
        }
    }
}