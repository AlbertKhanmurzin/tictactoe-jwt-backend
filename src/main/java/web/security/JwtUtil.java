package web.security;

import domain.model.user.Role;
import io.jsonwebtoken.Claims;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Утилитный класс-фабрика для сборки JwtAuthentication из клеймов (Claims) токена.
 */
public final class JwtUtil {

    private JwtUtil() {
    }

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();

        jwtInfoToken.setRoles(getRoles(claims));

        jwtInfoToken.setUsername(claims.getSubject());

        String uuidStr = claims.get("uuid", String.class);
        if (uuidStr != null) {
            jwtInfoToken.setUuid(UUID.fromString(uuidStr));
        }

        jwtInfoToken.setAuthenticated(true);

        return jwtInfoToken;
    }

    @SuppressWarnings("unchecked")
    private static Set<Role> getRoles(Claims claims) {
        final List<String> rolesList = claims.get("roles", List.class);
        if (rolesList == null || rolesList.isEmpty()) {
            return Collections.emptySet();
        }
        return rolesList.stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}