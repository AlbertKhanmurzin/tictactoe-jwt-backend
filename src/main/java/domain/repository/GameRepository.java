package domain.repository;


import domain.model.Game;
import domain.model.GameStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository {
    void saveGame(Game game);
    Optional<Game> findById(UUID uuid);
    List<Game> findByStatus(GameStatus status);
    List<Game> findFinishedGamesByUserId(UUID userId);
    List<domain.model.user.UserWinRate> findTopPlayers(int limit);
}
