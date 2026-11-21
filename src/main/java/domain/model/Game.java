package domain.model;

import domain.model.matrix.GameField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Game {
    public static final UUID BOT_ID = new UUID(0L, 0L);

    private final UUID gameId;
    private GameField field;
    private int currentPlayer;     // 1 = Крестик (X), 2 = Нолик (O)
    private UUID currentPlayerId;  // UUID пользователя, чей сейчас ход
    private UUID playerXId;        // UUID создателя игры (играет за X)
    private UUID playerOId;        // UUID соперника (играет за O, либо null при ожидании)
    private boolean vsBot;         // true — игра с компьютером, false — игра с человеком
    private GameStatus status;
    private UUID winnerId;         // UUID победившего игрока (null при игре или ничьей)
    private LocalDateTime createdAt; // Дата и время создания игры

    // конструктор для создания новой игры
    public Game(UUID creatorId, boolean vsBot) {
        this.gameId = UUID.randomUUID();
        this.field = new GameField();
        this.playerXId = creatorId;
        this.currentPlayer = 1; // Крестики всегда ходят первыми
        this.currentPlayerId = creatorId;
        this.vsBot = vsBot;
        this.createdAt = LocalDateTime.now(); // Фиксируем текущее время

        if (vsBot) {
            this.playerOId = BOT_ID;
            this.status = GameStatus.PLAYING;
        } else {
            this.playerOId = null;
            this.status = GameStatus.WAITING_FOR_PLAYERS;
        }
    }

    // конструктор для восстановления игры из базы данных
    public Game(UUID gameId, GameField field, int currentPlayer, UUID currentPlayerId,
                UUID playerXId, UUID playerOId, boolean vsBot, GameStatus status,
                UUID winnerId, LocalDateTime createdAt) {
        this.gameId = gameId;
        this.field = field;
        this.currentPlayer = currentPlayer;
        this.currentPlayerId = currentPlayerId;
        this.playerXId = playerXId;
        this.playerOId = playerOId;
        this.vsBot = vsBot;
        this.status = status;
        this.winnerId = winnerId;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public GameField getGameField() {
        return new GameField(this.field);
    }

    public void updateStatus() {
        if (this.status != GameStatus.PLAYING) {
            return;
        }

        int winner = this.field.checkWinner();
        if (winner == 1) {
            this.status = GameStatus.X_WON;
            this.winnerId = this.playerXId;
        } else if (winner == 2) {
            this.status = GameStatus.O_WON;
            this.winnerId = this.playerOId;
        } else if (this.field.isFull()) {
            this.status = GameStatus.DRAW;
        }
    }

    public void switchTurn() {
        if (this.currentPlayer == 1) {
            this.currentPlayer = 2;
            this.currentPlayerId = this.playerOId;
        } else {
            this.currentPlayer = 1;
            this.currentPlayerId = this.playerXId;
        }
    }
}