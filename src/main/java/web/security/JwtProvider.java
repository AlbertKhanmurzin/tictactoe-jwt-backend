package web.security;

import domain.model.user.Role;
import domain.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    private final long jwtAccessLifetime;
    private final long jwtRefreshLifetime;

    // Внедряем ключи и время жизни из application.properties через @Value
    public JwtProvider(
            @Value("${jwt.secret.access}") String accessSecret,
            @Value("${jwt.secret.refresh}") String refreshSecret,
            @Value("${jwt.lifetime.access:900000}") long accessLifetime,
            @Value("${jwt.lifetime.refresh:2592000000}") long refreshLifetime
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
        this.jwtAccessLifetime = accessLifetime;
        this.jwtRefreshLifetime = refreshLifetime;
    }


    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtAccessLifetime);

        return Jwts.builder()
                .subject(user.getLogin())
                .claim("uuid", user.getId().toString())
                .claim("roles", user.getRoles().stream().map(Role::name).collect(Collectors.toList()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(jwtAccessSecret)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtRefreshLifetime);

        return Jwts.builder()
                .subject(user.getLogin())
                .claim("uuid", user.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(String token, SecretKey secretKey) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired: {}", expEx.getMessage());
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt: {}", unsEx.getMessage());
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt: {}", mjEx.getMessage());
        } catch (SecurityException sEx) {
            log.error("Invalid signature: {}", sEx.getMessage());
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
        }
        return false;
    }

    public Claims getAccessClaims(String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    public Claims getClaims(String token) {
        return getAccessClaims(token);
    }

    private Claims getClaims(String token, SecretKey secretKey) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}