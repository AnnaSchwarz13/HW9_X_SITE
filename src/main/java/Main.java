import service.Imp.AuthenticationServiceImp;
import service.Imp.UserServiceImp;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    static AuthenticationServiceImp authenticationService = new AuthenticationServiceImp();
    static UserServiceImp userService = new UserServiceImp();
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

            }
        }
    }

    public static void loginMenu(int option) throws SQLException {
        if (option == 1) {
            System.out.println("enter your username:");
            String username = scanner.next();
            System.out.println("enter your password:");
            String password = scanner.next();
                    userService.userLoginUsername(username,password);

        } else if (option == 2) {
            System.out.println("enter your email :");
            String email = scanner.next();
            System.out.println("enter your password:");
            String password = scanner.next();
            userService.userLoginEmail(email,password);

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
            userService.userSignup(username,password,email,bio,displayName);

        }
    }
}