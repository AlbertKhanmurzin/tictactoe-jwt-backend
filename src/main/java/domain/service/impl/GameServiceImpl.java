package domain.service.impl;

import domain.minimax.MiniMax;
import domain.model.Game;
import domain.model.GameStatus;
import domain.model.exceptions.GameNotFoundException;
import domain.model.exceptions.GameOverException;
import domain.model.exceptions.InvalidMoveException;
import domain.model.matrix.GameField;
import domain.model.matrix.Point;
import domain.repository.GameRepository;
import domain.service.GameService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepo) {
        this.gameRepository = gameRepo;
    }

    @Override
    public Game createGame(UUID creatorId, boolean vsBot){
        Game newGame = new Game(creatorId, vsBot);
        this.gameRepository.saveGame(newGame);
        return newGame;
    }

    @Override
    public Optional<Point> findBestMove(GameField field){
        return MiniMax.findBestMove(field);
    }

    @Override
    public void validMove(GameField newField, GameField oldField, int player) {
        int changesCount = 0;
        for (int i = 0; i < GameField.SIZE; i++) {
            for (int j = 0; j < GameField.SIZE; j++) {
                int oldCell = oldField.getCell(j, i);
                int newCell = newField.getCell(j, i);

                if(oldCell != 0 && oldCell != newCell){
                    throw new InvalidMoveException("previous moves have been changed");
                }

                if (oldCell == 0 && newCell != 0) {
                    changesCount++;

                    if (newCell != player) {
                        throw new InvalidMoveException("the order of the move has been disrupted");
                    }
                }
            }
        }
        if(changesCount != 1){
            throw new InvalidMoveException("More than one move has been made");
        }
    }

    @Override
    public Game processMove(UUID id, GameField newField, UUID currentUserId) {
        Game dataGame = getGameById(id);

        if (dataGame.getStatus() == GameStatus.WAITING_FOR_PLAYERS) {
            throw new GameOverException("Игра еще ожидает второго игрока");
        }
        if (dataGame.getStatus() != GameStatus.PLAYING) {
            throw new GameOverException("Игра уже окончена");
        }
        if (dataGame.getCurrentPlayerId() == null || !dataGame.getCurrentPlayerId().equals(currentUserId)) {
            throw new InvalidMoveException("Сейчас ход другого игрока");
        }
        int playerSymbol = dataGame.getCurrentPlayer(); // 1 для X, 2 для O

        validMove(newField, dataGame.getGameField(), playerSymbol);
        dataGame.setField(newField);
        dataGame.updateStatus();

        if (dataGame.getStatus() == GameStatus.PLAYING) {
            if (dataGame.isVsBot()) {
                // игра против бота
                Optional<Point> optPoint = findBestMove(newField);
                if (optPoint.isEmpty()) {
                    throw new GameOverException("Невозможно сделать ход");
                }
                newField.setCell(optPoint.get(), 2);
                dataGame.setField(newField);
                dataGame.updateStatus();
            } else {
                // игра между людьми: переключаем очередь на второго игрока
                dataGame.switchTurn();
            }
        }
        this.gameRepository.saveGame(dataGame);
        return dataGame;
    }

    @Override
    public List<Game> getAvailableGames() {
        return gameRepository.findByStatus(GameStatus.WAITING_FOR_PLAYERS);
    }

    @Override
    public Game getGameById(UUID id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    @Override
    public Game joinGame(UUID gameId, UUID opponentId){
        Game game = this.gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        if (game.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new IllegalStateException("Игра уже началась");
        }
        if (game.getPlayerXId().equals(opponentId)) {
            throw new IllegalArgumentException("Создатель игры не может играть против самого себя");
        }

        game.setPlayerOId(opponentId);
        game.setStatus(GameStatus.PLAYING);
        this.gameRepository.saveGame(game);

        return game;
    }

    @Override
    public List<Game> getFinishedGames(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UUID пользователя не может быть null");
        }
        return gameRepository.findFinishedGamesByUserId(userId);
    }

    @Override
    public List<domain.model.user.UserWinRate> getTopPlayers(int limit) {
        int effectiveLimit = limit > 0 ? limit : 10;
        return gameRepository.findTopPlayers(effectiveLimit);
    }
}