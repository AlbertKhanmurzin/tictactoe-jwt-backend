package web.controller;

import domain.model.Game;
import domain.model.user.User;
import domain.service.AuthService;
import domain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.model.ErrorResponse;
import web.model.UserResponse;
import web.security.JwtAuthentication;

import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    // получение информации о пользователе по accessToken
    @GetMapping({"/me", ""})
    public ResponseEntity<?> getCurrentUser() {
        JwtAuthentication auth = authService.getAuthInfo();
        if (auth == null || auth.getUuid() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "Пользователь не авторизован"));
        }
        return getUserById(auth.getUuid());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        if (Game.BOT_ID.equals(id)) {
            return ResponseEntity.ok(new UserResponse(Game.BOT_ID, "AI_BOT"));
        }

        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return ResponseEntity.ok(new UserResponse(user.getId(), user.getLogin()));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", "Пользователь не найден"));
    }
}