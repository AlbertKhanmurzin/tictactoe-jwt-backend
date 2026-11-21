package datasource.mapper;

import datasource.entity.GameEntity;
import domain.model.Game;
import domain.model.matrix.GameField;

public class GameEntityMapper {

    public GameEntity toDataSource(Game game) {
        return new GameEntity(
                game.getGameId(),
                game.getGameField().getMatrix(),
                game.getCurrentPlayer(),
                game.getCurrentPlayerId(),
                game.getPlayerXId(),
                game.getPlayerOId(),
                game.isVsBot(),
                game.getStatus(),
                game.getWinnerId(),
                game.getCreatedAt()
        );
    }

    public Game toDomain(GameEntity entity) {
        GameField gameField = new GameField(entity.getMatrix());
        return new Game(
                entity.getId(),
                gameField,
                entity.getCurrentPlayer(),
                entity.getCurrentPlayerId(),
                entity.getPlayerXId(),
                entity.getPlayerOId(),
                entity.isVsBot(),
                entity.getStatus(),
                entity.getWinnerId(),
                entity.getCreatedAt()
        );
    }
}