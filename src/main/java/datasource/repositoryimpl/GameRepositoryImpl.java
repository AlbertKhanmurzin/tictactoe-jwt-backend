package datasource.repositoryimpl;

import datasource.entity.GameEntity;
import datasource.mapper.GameEntityMapper;
import domain.model.Game;
import domain.model.GameStatus;
import domain.repository.GameRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameRepositoryImpl implements GameRepository {

    private final GameJpaRepository jpaCrudRepository;
    private final GameEntityMapper mapper;

    public GameRepositoryImpl(GameJpaRepository jpaCrudRepository, GameEntityMapper mapper) {
        this.jpaCrudRepository = jpaCrudRepository;
        this.mapper = mapper;
    }

    public boolean existsById(UUID id){
        return this.jpaCrudRepository.existsById(id);
    }

    public List<Game> findByStatus(GameStatus status) {
        return jpaCrudRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void saveGame(Game domainGame) {
        this.jpaCrudRepository.save(this.mapper.toDataSource(domainGame));
    }

    @Override
    public Optional<Game> findById(UUID id) {

        Optional<GameEntity> optionalGameEntity =this.jpaCrudRepository.findById(id);
        return optionalGameEntity.map(mapper::toDomain);
    }

    @Override
    public List<Game> findFinishedGamesByUserId(UUID userId) {
        return jpaCrudRepository.findFinishedGamesByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<domain.model.user.UserWinRate> findTopPlayers(int limit) {
        List<Object[]> rows = jpaCrudRepository.findLeaderboardNative(limit);
        List<domain.model.user.UserWinRate> result = new java.util.ArrayList<>();

        for (Object[] row : rows) {
            UUID userId = (row[0] instanceof UUID)
                    ? (UUID) row[0]
                    : UUID.fromString(row[0].toString());
            String login = row[1] != null ? row[1].toString() : "unknown";
            double winRate = (row[2] instanceof Number)
                    ? ((Number) row[2]).doubleValue()
                    : 0.0;

            double roundedRate = Math.round(winRate * 100.0) / 100.0;
            result.add(new domain.model.user.UserWinRate(userId, login, roundedRate));
        }

        return result;
    }

}
