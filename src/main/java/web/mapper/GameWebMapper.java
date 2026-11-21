package web.mapper;

import domain.model.Game;
import domain.model.matrix.GameField;
import web.model.GameRequest;
import web.model.GameResponse;

public class GameWebMapper {
    public GameField toDomain(GameRequest webGame){
        return new GameField(webGame.getMatrix());
    }

    public GameResponse toWeb(Game game) {
        GameResponse toWeb = new GameResponse();
        toWeb.setUuid(game.getGameId().toString());
        toWeb.setField(game.getGameField().getMatrix());
        toWeb.setCurrentPlayer(game.getCurrentPlayer());
        toWeb.setCurrentPlayerId(game.getCurrentPlayerId());
        toWeb.setPlayerXId(game.getPlayerXId());
        toWeb.setPlayerOId(game.getPlayerOId());
        toWeb.setStatus(game.getStatus().name());
        toWeb.setWinnerId(game.getWinnerId());
        toWeb.setCreatedAt(game.getCreatedAt());
        toWeb.createMessage();
        return toWeb;
    }
}