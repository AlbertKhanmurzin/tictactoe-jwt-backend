package domain.service;

import domain.model.Game;
import domain.model.GameStatus;
import domain.model.matrix.GameField;
import domain.model.matrix.Point;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameService {
    Optional<Point> findBestMove(GameField field);
    void validMove(GameField newField, GameField oldField, int player);
    Game createGame(UUID creatorId, boolean vsBot);
    Game processMove(UUID gameId, GameField newField, UUID currentUserId);
    List<Game> getAvailableGames();
    Game getGameById(UUID id);
    Game joinGame(UUID gameId, UUID opponentId);
    List<Game> getFinishedGames(UUID userId);
    List<domain.model.user.UserWinRate> getTopPlayers(int limit);
}

