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
                    3.singUp""");
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
                    5.logout""");
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
        while (true) {
            System.out.println("enter your username:");
            String username = scanner.next();
            if (authenticationService.isUsernameNew(username)) {
                System.out.println("enter your password:");
                String password = scanner.next();
                while (true) {
                    System.out.println("enter your email :");
                    String email = scanner.next();
                    if (authenticationService.isEmailNew(email)) {
                        System.out.println("enter your Bio :");
                        String bio = scanner.next();
                        System.out.println("enter your display name :\n(this name will show for other users)");
                        String displayName = scanner.next() +  scanner.next();
                        userService.userSignup(username, password, email, bio, displayName);
                        break;
                    }
                    System.out.println("email is already taken");
                }
                break;
            }
            System.out.println("username is already taken");
        }
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
            if (!tweet.isRetweeted())
                tweetService.displayTweet(tweet);
            else {
                System.out.println("---------");
                TweetServiceImp.displayRetweet(tweet);
            }
        }
        long id = scanner.nextLong();
        if (TweetRepositoryImp.read(id).getUser().getId() == authenticationService.getLoggedUser().getId()) {
            tweetService.changeDetailsOfTweet(TweetRepositoryImp.read(id));
        } else {
            System.out.println("Wrong id");
        }
    } else if (option == 4) {
        userService.changeProfile();
    } else if (option == 5) {
        System.out.println("See you dear " + authenticationService.getLoggedUser().getDisplayName());
        userService.userLogout();
    }
}