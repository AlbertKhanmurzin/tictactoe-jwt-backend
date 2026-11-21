package web.security;

import domain.model.user.Role;
import domain.model.user.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование JWT Provider")
class JwtProviderTest {

    private JwtProvider jwtProvider;
    private User testUser;
    private UUID userId;

    // Секретные 256-битные Base64-ключи для тестов
    private static final String ACCESS_SECRET = "OTg0NDc2NjFiZGExMDhmMjUzZDU2Nzc3MDVjZjc5YmU2MWU2YjgwNGFkOTc4NTI5YjYwOWUyNWU2ZTM0YWIzMg==";
    private static final String REFRESH_SECRET = "MGE0ZjExODgwYjcyMjNiMTRhMjc1MTM0NTJiNDUwNzI3OGM0NGU4ODdmNGQ1NzQxNzM4Y2M2MTEyZjVmMzQ0NQ==";

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(ACCESS_SECRET, REFRESH_SECRET, 900_000L, 2_592_000_000L);
        userId = UUID.randomUUID();
        testUser = new User(userId, "test_player", "hashed_pwd", Set.of(Role.USER));
    }

    @Test
    @DisplayName("Генерация и валидация Access токена")
    void shouldGenerateAndValidateAccessToken() {
        String token = jwtProvider.generateAccessToken(testUser);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtProvider.validateAccessToken(token));

        Claims claims = jwtProvider.getAccessClaims(token);
        assertEquals("test_player", claims.getSubject());
        assertEquals(userId.toString(), claims.get("uuid"));
    }

    @Test
    @DisplayName("Генерация и валидация Refresh токена")
    void shouldGenerateAndValidateRefreshToken() {
        String token = jwtProvider.generateRefreshToken(testUser);

        assertNotNull(token);
        assertTrue(jwtProvider.validateRefreshToken(token));

        Claims claims = jwtProvider.getRefreshClaims(token);
        assertEquals("test_player", claims.getSubject());
        assertEquals(userId.toString(), claims.get("uuid"));
    }

    @Test
    @DisplayName("Валидация некорректного токена должна возвращать false")
    void shouldReturnFalseForInvalidToken() {
        assertFalse(jwtProvider.validateAccessToken("invalid.jwt.token"));
        assertFalse(jwtProvider.validateAccessToken(""));
        assertFalse(jwtProvider.validateAccessToken(null));
    }

    @Test
    @DisplayName("Access токен не должен валидироваться как Refresh токен")
    void accessTokenShouldNotValidateAsRefreshToken() {
        String accessToken = jwtProvider.generateAccessToken(testUser);
        assertFalse(jwtProvider.validateRefreshToken(accessToken));
    }
}
