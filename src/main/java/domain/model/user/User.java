package domain.model.user;

import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
public class User {
    private final UUID id;
    private final String login;
    private final String passwordHash;
    private final Set<Role> roles;

    public User(UUID id, String login, String passwordHash, Set<Role> roles) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.roles = roles;
    }

    public User(String login, String passwordHash, Set<Role> roles) {
        this.id = null;
        this.login = login;
        this.passwordHash = passwordHash;
        this.roles = roles;
    }

}
 
