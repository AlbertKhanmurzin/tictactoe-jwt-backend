package web.controller;

import domain.model.Game;
import domain.model.exceptions.GameNotFoundException;
import domain.model.exceptions.GameOverException;
import domain.model.exceptions.InvalidMoveException;
import domain.model.matrix.GameField;
import domain.service.GameService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.mapper.GameWebMapper;
import web.model.CreateGameRequest;
import web.model.ErrorResponse;
import web.model.GameRequest;
import web.model.GameResponse;

import java.util.List;
import java.util.UUID;

 //Для фронта
import org.springframework.web.bind.annotation.CrossOrigin;
@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;
    private final GameWebMapper mapper;

    public GameController(GameService gameService, GameWebMapper mapper) {
        this.gameService = gameService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<GameResponse> createGame(@RequestBody(required = false) CreateGameRequest request,
                                                   HttpServletRequest httpRequest) {
        UUID creatorId = (UUID) httpRequest.getAttribute("userId");
        boolean vsBot = request != null && Boolean.TRUE.equals(request.getVsBot());
        Game game = gameService.createGame(creatorId, vsBot);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toWeb(game));
    }

    @GetMapping
    public ResponseEntity<List<GameResponse>> getAvailableGames() {
        List<GameResponse> games = gameService.getAvailableGames()
                .stream()
                .map(mapper::toWeb)
                .toList();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGameById(@PathVariable UUID id) {
        try {
            Game game = gameService.getGameById(id);
            return ResponseEntity.ok(mapper.toWeb(game));
        } catch (GameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        }
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinGame(@PathVariable UUID id, HttpServletRequest httpRequest) {
        UUID opponentId = (UUID) httpRequest.getAttribute("userId");
        try {
            Game game = gameService.joinGame(id, opponentId);
            return ResponseEntity.ok(mapper.toWeb(game));
        } catch (GameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("BAD_REQUEST", e.getMessage()));
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> makeMove(@PathVariable UUID id,
                                      @RequestBody GameRequest request,
                                      HttpServletRequest httpRequest) {
        UUID currentUserId = (UUID) httpRequest.getAttribute("userId");
        try {
            GameField newField = mapper.toDomain(request);
            Game updatedGame = gameService.processMove(id, newField, currentUserId);
            return ResponseEntity.ok(mapper.toWeb(updatedGame));
        } catch (GameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (InvalidMoveException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("INVALID_MOVE", e.getMessage()));
        } catch (GameOverException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("GAME_OVER", e.getMessage()));
        }
    }

    @GetMapping({"/history", "/completed"})
    public ResponseEntity<List<GameResponse>> getGameHistory(HttpServletRequest httpRequest) {
        UUID currentUserId = (UUID) httpRequest.getAttribute("userId");
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<GameResponse> history = gameService.getFinishedGames(currentUserId)
                .stream()
                .map(mapper::toWeb)
                .toList();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<web.model.LeaderboardResponse>> getLeaderboard(
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "n", required = false) Integer n) {

        // Поддерживаем оба параметра: и 'n' (по ТЗ), и 'limit' (стандартный REST)
        int effectiveLimit = (n != null && n > 0) ? n : (limit > 0 ? limit : 10);

        List<web.model.LeaderboardResponse> leaderboard = gameService.getTopPlayers(effectiveLimit)
                .stream()
                .map(stats -> new web.model.LeaderboardResponse(
                        stats.getUserId(),
                        stats.getLogin(),
                        stats.getWinRate()
                ))
                .toList();

        return ResponseEntity.ok(leaderboard);
    }
}
