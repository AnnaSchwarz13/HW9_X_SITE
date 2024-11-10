package entities;

import entities.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User {
    String username;
    String password;
    Role role;
    long id;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    private long id;
    private final String firstName;
    private final String lastName;
    private final String nationalCode;
    private final Date birthDate;


    public User(String firstName, String lastName, String username, String password,
                String nationalCode, Date birthday) {
        super(username, password, Role.AUTHOR);
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalCode = nationalCode;
        this.birthDate = birthday;
    }

}
