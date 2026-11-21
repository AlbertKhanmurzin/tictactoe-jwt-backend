package web.security;


import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import domain.model.user.Role;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class JwtAuthentication implements Authentication{

    private boolean authenticated;    // идентификатор пользователя
    private UUID uuid;
    private String username;

    private Set<Role> roles;
    public JwtAuthentication() {
        this.authenticated = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return uuid;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return uuid != null ? uuid.toString() : null;
    }
}
