import entities.Tweet;
import repository.Imp.TweetRepositoryImp;
import service.Imp.AuthenticationServiceImp;
import service.Imp.TweetServiceImp;
import service.Imp.UserServiceImp;
import service.TweetService;

import java.sql.SQLException;
import java.util.Scanner;

static TweetRepositoryImp tweetRepositoryImp = new TweetRepositoryImp();
static AuthenticationServiceImp authenticationService = new AuthenticationServiceImp();
static UserServiceImp userService = new UserServiceImp();
static TweetService tweetService = new TweetServiceImp();
static Scanner scanner = new Scanner(System.in);

public static void main(String[] args) throws SQLException {


    while (true) {
        while (authenticationService.getLoggedUser() == null) {
            System.out.println("\n\nWelcome to X . . .\n");
            System.out.println("""
                    1.login with username
                    2.login with email
                    2.singUp""");
            int option = scanner.nextInt();
            loginMenu(option);
        }
        while (authenticationService.getLoggedUser() != null) {
            System.out.println("\n\nWelcome dear " + authenticationService.getLoggedUser().getDisplayName());
            System.out.println("""
                    1.View all tweets
                    2.Tweet a new text
                    3.Edit your tweets
                    4.Edite profile
                    5.Change password
                    6.logout""");
            int option = scanner.nextInt();
            xSiteMenu(option);

        }
    }
}

public static void loginMenu(int option) throws SQLException {
    if (option == 1) {
        System.out.println("enter your username:");
        String username = scanner.next();
        System.out.println("enter your password:");
        String password = scanner.next();
        userService.userLoginUsername(username, password);

    } else if (option == 2) {
        System.out.println("enter your email :");
        String email = scanner.next();
        System.out.println("enter your password:");
        String password = scanner.next();
        userService.userLoginEmail(email, password);

    } else if (option == 3) {
        System.out.println("enter your username:");
        String username = scanner.next();
        System.out.println("enter your password:");
        String password = scanner.next();
        System.out.println("enter your email :");
        String email = scanner.next();
        System.out.println("enter your Bio :");
        String bio = scanner.next();
        System.out.println("enter your display name :\n(this name will show for other users)");
        String displayName = scanner.next();
        userService.userSignup(username, password, email, bio, displayName);

    }
}

public static void xSiteMenu(int option) throws SQLException {
    if (option == 1) {
        tweetService.showTweetList(tweetRepositoryImp.all());
    } else if (option == 2) {
        tweetService.addTweet();
    } else if (option == 3) {
        System.out.println("there is your tweets enter id to edite");
        for (Tweet tweet : tweetRepositoryImp.getTweetsOfAUser(authenticationService.getLoggedUser())) {
            tweetService.displayTweet(tweet);
        }
        long id = scanner.nextLong();
        tweetService.changeDetailsOfTweet(TweetRepositoryImp.read(id));
    } else if (option == 4) {
        userService.changeProfile();
    } else if (option == 5) {
        System.out.println("enter your old password:");
        String oldPassword = scanner.next();
        System.out.println("enter your new password:");
        String newPassword = scanner.next();
        userService.changePassword(oldPassword, newPassword);
    } else if (option == 6) {
        System.out.println("See you dear " + authenticationService.getLoggedUser().getDisplayName());
        userService.userLogout();
    }
}