package entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User {
    private String username;
    private String password;
    private long id;
    private String displayName;
    private String email;
    private String bio;
    private final Date creationDate;


}
