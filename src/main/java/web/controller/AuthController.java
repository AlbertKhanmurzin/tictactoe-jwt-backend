package web.controller;

import domain.model.exceptions.UserAlreadyExistsException;
import domain.model.signup.SignUp;
import domain.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.mapper.SignUpRequestMapper;
import web.model.ErrorResponse;
import web.model.SignUpRequest;
import web.model.UserResponse;
import web.model.jwt.JwtRequest;
import web.model.jwt.JwtResponse;
import web.model.jwt.RefreshJwtRequest;
import web.security.JwtAuthentication;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SignUpRequestMapper mapper;

    public AuthController(AuthService authService, SignUpRequestMapper mapper) {
        this.authService = authService;
        this.mapper = mapper;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest webSignUp) {
        try {
            SignUp domainSignUp = mapper.toDomain(webSignUp);
            boolean created = this.authService.register(domainSignUp);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", created));

        } catch (UserAlreadyExistsException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("USER_EXISTS", e.getMessage()));
        }
    }

    // 1. авторизация: принимает логин и пароль, возвращает пару токенов
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest authRequest) {
        try {
            JwtResponse token = authService.login(authRequest);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", e.getMessage()));
        }
    }

    // 2. обновление accessToken: принимает refreshToken, возвращает новый accessToken
    @PostMapping({"/token", "/access"})
    public ResponseEntity<?> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        try {
            JwtResponse token = authService.getAccessToken(request.getRefreshToken());
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", e.getMessage()));
        }
    }

    // 3. обновление refreshToken: ротация обоих токенов
    @PostMapping("/refresh")
    public ResponseEntity<?> getNewRefreshToken(@RequestBody RefreshJwtRequest request) {
        try {
            JwtResponse token = authService.refresh(request.getRefreshToken());
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", e.getMessage()));
        }
    }

    // эндпоинт для получения информации о пользователе по токену через /api/auth/me
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        JwtAuthentication auth = authService.getAuthInfo();
        if (auth == null || auth.getUuid() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "Пользователь не авторизован"));
        }
        return ResponseEntity.ok(new UserResponse(auth.getUuid(), auth.getUsername()));
    }
}