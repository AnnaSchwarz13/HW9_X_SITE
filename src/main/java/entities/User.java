package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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


}
