package datasource.entity;

import domain.model.user.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<Role> roles = new HashSet<>();

    public UserEntity(UUID id, String login, String passwordHash, Set<Role> roles) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        if (roles != null) {
            this.roles = new HashSet<>(roles);
        }
    }

    public UserEntity(String login, String passwordHash, Set<Role> roles) {
        this(null, login, passwordHash, roles);
    }
}