package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private String username;
    private String password;
    private long id;
    private String displayName;
    private String email;
    private String bio;
    private final Date creationDate;

    public User(String username, String password, String displayName, String email, String bio) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.email = email;
        this.bio = bio;
        this.creationDate = java.sql.Date.valueOf(LocalDate.now());
    }
}
