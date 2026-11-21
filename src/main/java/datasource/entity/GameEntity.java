package datasource.entity;

import datasource.converter.FieldJsonConverter;
import domain.model.GameStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Table(name = "games")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameEntity {

    @Id
    private UUID id;

    @Convert(converter = FieldJsonConverter.class)
    @Column(columnDefinition = "TEXT")
    @Getter(AccessLevel.NONE)
    private int[][] matrix;

    private int currentPlayer;
    private UUID currentPlayerId;
    private UUID playerXId;
    private UUID playerOId;
    private boolean vsBot;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    private UUID winnerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public GameEntity(UUID id, int[][] matrix, int currentPlayer, UUID currentPlayerId,
                      UUID playerXId, UUID playerOId, boolean vsBot, GameStatus status,
                      UUID winnerId, LocalDateTime createdAt) {
        this.id = id;
        this.matrix = matrix;
        this.currentPlayer = currentPlayer;
        this.currentPlayerId = currentPlayerId;
        this.playerXId = playerXId;
        this.playerOId = playerOId;
        this.vsBot = vsBot;
        this.status = status;
        this.winnerId = winnerId;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public int[][] getMatrix() {
        int size = matrix.length;
        int[][] copy = new int[size][];
        for (int i = 0; i < size; i++) {
            copy[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return copy;
    }
}