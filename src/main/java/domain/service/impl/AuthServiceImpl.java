package domain.service.impl;

import domain.model.exceptions.UserAlreadyExistsException;
import domain.model.signup.SignUp;
import domain.model.user.User;
import domain.service.AuthService;
import domain.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import web.model.jwt.JwtRequest;
import web.model.jwt.JwtResponse;
import web.security.JwtAuthentication;
import web.security.JwtProvider;

public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthServiceImpl(UserService userService,
                           PasswordEncoder passwordEncoder,
                           JwtProvider jwtProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public boolean register(SignUp request) {
        if (userService.existsByLogin(request.getLogin())) {
            throw new UserAlreadyExistsException("Пользователь " + request.getLogin() + " уже существует");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        userService.createUser(request.getLogin(), encodedPassword);
        return true;
    }

    @Override
    public JwtResponse login(JwtRequest authRequest) {
        if (authRequest == null || authRequest.getLogin() == null || authRequest.getPassword() == null) {
            throw new IllegalArgumentException("Логин и пароль обязательны для заполнения");
        }

        // 1. ищем пользователя в БД
        User user = userService.findByLogin(authRequest.getLogin())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // 2.сверяем хеш пароля с солью
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Неверный пароль");
        }

        // 3. генерируем оба токена
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    public JwtResponse getAccessToken(String refreshToken) {
        // 1. валидируем refresh токен именно ключом для refresh-токенов
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Невалидный refresh токен");
        }

        // 2. извлекаем claims и subject (логин)
        Claims claims = jwtProvider.getRefreshClaims(refreshToken);
        String login = claims.getSubject();

        // 3. проверяем наличие пользователя в БД на случай бана или смены прав
        User user = userService.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // 4. генерируем только новый accessToken (refreshToken возвращаем null)
        String accessToken = jwtProvider.generateAccessToken(user);
        return new JwtResponse(accessToken, null);
    }

    @Override
    public JwtResponse refresh(String refreshToken) {
        // 1. валидируем refresh токен
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Невалидный refresh токен");
        }

        // 2. извлекаем данные
        Claims claims = jwtProvider.getRefreshClaims(refreshToken);
        String login = claims.getSubject();

        User user = userService.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // 3. ротируем: выпускаем новый accessToken И новый refreshToken
        String accessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        return new JwtResponse(accessToken, newRefreshToken);
    }

    @Override
    public JwtAuthentication getAuthInfo() {
        // извлекаем текущую аутентификацию из потока запроса (ThreadLocal)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthentication jwtAuthentication) {
            return jwtAuthentication;
        }
        return null;
    }
}