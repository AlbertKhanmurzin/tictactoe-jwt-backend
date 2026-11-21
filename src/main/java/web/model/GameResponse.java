package web.model;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class GameResponse {
    private String uuid;
    private int[][] field;
    private int currentPlayer;
    private UUID currentPlayerId;
    private UUID playerXId;
    private UUID playerOId;
    private String status;
    private UUID winnerId;
    private String message;
    private java.time.LocalDateTime createdAt;

    public void createMessage() {
        if (this.status == null) return;

        switch (this.status) {
            case "WAITING_FOR_PLAYERS":
                this.message = "Ожидание игроков";
                break;
            case "PLAYING":
                this.message = "Ход игрока с UUID: " + this.currentPlayerId;
                break;
            case "X_WON":
            case "O_WON":
                this.message = "Победа игрока с UUID: " + this.winnerId;
                break;
            case "DRAW":
                this.message = "Ничья";
                break;
        }
    }
}