import entities.Tweet;
import exceptions.TagException;
import exceptions.TweetException;
import exceptions.UserException;
import service.Imp.AuthenticationServiceImp;
import service.Imp.TagServiceImp;
import service.Imp.TweetServiceImp;
import service.Imp.UserServiceImp;
import service.TagService;
import service.TweetService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

static AuthenticationServiceImp authenticationService = new AuthenticationServiceImp();
static UserServiceImp userService = new UserServiceImp();
static TweetService tweetService = new TweetServiceImp();
static TagService tagService = new TagServiceImp();
static Scanner scanner = new Scanner(System.in);

public static void main(String[] args){

    while (true) {

        while (authenticationService.getLoggedUser() == null) {
            System.out.println("\n\nWelcome to X . . .\n");
            System.out.println("""
                    1.login with username
                    2.login with email
                    3.singUp""");

            long option = userService.numericInput(scanner);
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
            long option = userService.numericInput(scanner);
            try {
                xSiteMenu(option);
            }catch (TweetException e){
                System.out.println(e.getMessage());
            }

        }
    }
}

public static void loginMenu(long option) {
    if (option == 1) {
        System.out.println("enter your username:");
        String username = scanner.next();
        System.out.println("enter your password:");
        String password = scanner.next();
        try {
            userService.userLoginUsername(username, password);
        } catch (UserException e) {
            System.out.println(e.getMessage());
        }
    } else if (option == 2) {
        System.out.println("enter your email :");
        String email = scanner.next();
        System.out.println("enter your password:");
        String password = scanner.next();
        try {
            userService.userLoginEmail(email, password);
        } catch (UserException e) {
            System.out.println(e.getMessage());
        }
    } else if (option == 3) {
        while (true) {
            System.out.println("enter your username:");
            String username = scanner.next();
            try {
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
                                String displayName = scanner.next();
                                userService.userSignup(username, password, email, bio, displayName);
                                break;
                            }
                    }
                    break;
                }
            }catch (UserException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

public static void xSiteMenu(long option) throws TweetException {
    if (option == 1) {
        showTweetList();
    } else if (option == 2) {
        System.out.println("Enter tweet text: ");
        String tweetText = scanner.nextLine() + scanner.nextLine();
        List<Long> brief = chooseTags();
        tweetService.addTweet(tweetText, brief);
    } else if (option == 3) {
        System.out.println("there is your tweets enter id to edite");
        for (Tweet tweet : tweetService.getTweetsOfAUser(authenticationService.getLoggedUser())) {
            if (!tweet.isRetweeted())
                tweetService.displayTweet(tweet);
            else {
                System.out.println("---------");
                tweetService.displayRetweet(tweet, 0);
            }
        }
        long id = userService.numericInput(scanner);
        if (authenticationService.isTweetForLoggedInUser(id)) {
            System.out.println("Which do you want to edit?");
            System.out.println("""
                            1.Edit content
                            2.Edit TagList\s
                            3.delete
                    """
            );
            long choose = userService.numericInput(scanner);
            if (choose == 1) {
                System.out.println("Please enter the new content:");
                String newText = scanner.nextLine() + scanner.nextLine();
                newText = newText + "\n(edited)";
                tweetService.editTweetText(newText, tweetService.getTweetById(id));

            } else if (choose == 2) {
                List<Long> newTags = tweetService.getTweetById(id).getBrief();
                while (true) {
                    if (newTags.isEmpty()) {
                        System.out.println("Your tweet have no tag yet");
                        System.out.println("for add more enter 1 \n and at the end -1");
                    } else {
                        System.out.println("Your tweet's tag(s) are there");
                        for (long tag : newTags) {
                            System.out.println(tagService.getTagById(tag).getTitle());
                        }
                        System.out.println("for add more enter 1 \n remove one tag enter 2 \n and at the end -1");
                    }
                    long choose2 = userService.numericInput(scanner);
                    if (choose2 == 1) {
                        List<Long> newTagsToAdd = chooseTags();
                        newTags.addAll(newTagsToAdd);
                    }
                    if (choose2 == 2) {
                        System.out.println("Please enter a tag name to remove");
                        String tagName = scanner.nextLine() + scanner.nextLine();
                        try {
                            tagService.isTagExist(tagName);
                            for(long tag : newTags) {
                                if (tagService.getTagById(tag).getTitle().equals(tagName)){
                                    newTags.remove(tag);
                                }
                            }
                        } catch (TagException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    if (choose2 == -1) {
                        tagService.updateTagList(newTags, id);
                        break;
                    }
                }
            } else if (choose == 3) {
                System.out.println("Warning!\nthis is your tweet it will be removed \nwith all like and e.x.");
                if (!tweetService.getTweetById(id).isRetweeted())
                    tweetService.displayTweet(tweetService.getTweetById(id));
                else {
                    System.out.println("---------");
                    tweetService.displayRetweet(tweetService.getTweetById(id), 0);
                }
                System.out.println("""
                        1.CONFIRM
                        2.REJECT""");
                long action = userService.numericInput(scanner);
                if (action == 1) {
                    tweetService.deleteTweetRetweet(tweetService.getTweetById(id));
                    System.out.println("Tweet successfully removed");
                } else if (action == 2) {
                    System.out.println("Action canceled !");
                }
            }
        }
    } else if (option == 4) {
        changeProfile();
    } else if (option == 5) {
        System.out.println("See you dear " + authenticationService.getLoggedUser().getDisplayName());
        userService.userLogout();
    }
}

public static void changeProfile() {
    System.out.println("Select to change");
    System.out.println("""
            1.Username
            2.Email
            3.Password
            4.DisplayName
            5.Bio""");
    long option = userService.numericInput(scanner);

    if (option == 1) {
        System.out.println("Enter old Username");
        String username = scanner.next();
        System.out.println("Enter new username");
        String newUsername = scanner.next();
        try {
            userService.changeUsername(username, newUsername);
        } catch (UserException e) {
            System.out.println(e.getMessage());
        }
    } else if (option == 2) {
        System.out.println("Enter old email");
        String oldEmail = scanner.next();
        System.out.println("Enter new email");
        String newEmail = scanner.next();
        try {
            userService.changeEmail(oldEmail, newEmail);
        } catch (UserException e) {
            System.out.println(e.getMessage());
        }
    } else if (option == 3) {
        System.out.println("Enter old password");
        String oldPassword = scanner.next();
        System.out.println("Enter new password");
        String newPassword = scanner.next();
        try {
            userService.changePassword(oldPassword, newPassword);
        } catch (UserException e) {
            System.out.println(e.getMessage());
        }
    } else if (option == 4) {
        System.out.println("Enter new DisplayName");
        String newDisplayName = scanner.next();
        userService.changeDisplayName(newDisplayName);


    } else if (option == 5) {
        System.out.println("Enter new Bio");
        String newBio = scanner.next();
        userService.changeBio(newBio);

    }
}

public static void showTweetList() {
    try {
        while (true) {
            System.out.println("for more action enter tweet's id else -1 ");

            for (Tweet tempTweet : tweetService.getAllTweets()) {
                tweetService.displayTweet(tempTweet);
            }

            long id = userService.numericInput(scanner);

            if (id == -1) {
                break;
            } else if (tweetService.isTweetIdExist(id)) {
                System.out.println("""
                        1.Like!
                        2.Dislike!
                        3.Retweet""");
                long action = userService.numericInput(scanner);
                if (action == 1) {
                    try {
                        tweetService.addLike(id);
                    } catch (TweetException e) {
                        System.out.println(e.getMessage());
                    }
                } else if (action == 2) {
                    try {
                        tweetService.addDislike(id);
                    } catch (TweetException e) {
                        System.out.println(e.getMessage());
                    }
                } else if (action == 3) {
                    System.out.println("replay:");
                    System.out.println("Enter tweet text: ");
                    String tweetText = scanner.nextLine() + scanner.nextLine();
                    List<Long> brief = chooseTags();
                    tweetService.addRetweet(tweetText, id , brief);
                }
            }
        }

    } catch (TweetException e) {
        System.out.println(e.getMessage());
    }
}

public static List<Long> chooseTags(){
    List<Long> tags = new ArrayList<>();
    tagService.showAllTags();
    while (true) {
        try {
            String tagName = scanner.nextLine();
            if (tagName.equals("-1")) {
                break;
            }
            if (tagName.equals("1")) {
                System.out.println("Please enter your tag name");
                String newTagName = scanner.nextLine();
                tagService.addNewTag(newTagName);
            } else {
                Long newTag = tagService.addNewTagTweet(tagName);
                tags.add(newTag);
            }
        } catch (TagException e) {
            System.out.println(e.getMessage());
        }
    }
    return tagService.removeDuplicates(tags);
}
