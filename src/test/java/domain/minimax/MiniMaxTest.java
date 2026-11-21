package domain.minimax;

import domain.model.matrix.GameField;
import domain.model.matrix.Point;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование ИИ MiniMax для Крестиков-Ноликов")
class MiniMaxTest {

    @Test
    @DisplayName("ИИ должен выбрать победный ход для завершения игры")
    void shouldChooseWinningMove() {
        // Поле: AI (2) на [0,0] и [0,1], клетка [0,2] пуста
        // 2 2 0
        // 1 1 0
        // 0 0 0
        int[][] matrix = {
                {2, 2, 0},
                {1, 1, 0},
                {0, 0, 0}
        };
        GameField field = new GameField(matrix);

        Optional<Point> bestMove = MiniMax.findBestMove(field);

        assertTrue(bestMove.isPresent());
        assertEquals(2, bestMove.get().getX());
        assertEquals(0, bestMove.get().getY());
    }

    @Test
    @DisplayName("ИИ должен блокировать выигрышный ход соперника")
    void shouldBlockOpponentWinningMove() {
        // Поле: Игрок (1) на [0,0] и [0,1], AI (2) на [1,1]
        // 1 1 0
        // 0 2 0
        // 0 0 0
        // AI должен заблокировать ход на (2, 0)
        int[][] matrix = {
                {1, 1, 0},
                {0, 2, 0},
                {0, 0, 0}
        };
        GameField field = new GameField(matrix);

        Optional<Point> bestMove = MiniMax.findBestMove(field);

        assertTrue(bestMove.isPresent());
        assertEquals(2, bestMove.get().getX());
        assertEquals(0, bestMove.get().getY());
    }

    @Test
    @DisplayName("ИИ должен предпочесть немедленную победу затягиванию игры")
    void shouldPreferImmediateWinOverDelayedWin() {
        // 1 2 1
        // 2 0 1
        // 0 2 0
        // Немедленная победа по вертикали на (1, 1)
        int[][] matrix = {
                {1, 2, 1},
                {2, 0, 1},
                {0, 2, 0}
        };
        GameField field = new GameField(matrix);

        Optional<Point> bestMove = MiniMax.findBestMove(field);

        assertTrue(bestMove.isPresent());
        assertEquals(1, bestMove.get().getX());
        assertEquals(1, bestMove.get().getY());
    }

    @Test
    @DisplayName("ИИ должен блокировать немедленную угрозу даже в трудной позиции")
    void shouldBlockImmediateLossEvenInDifficultPosition() {
        // 1 2 0
        // 1 0 0
        // 0 0 0
        // Игрок угрожает победить на (0, 2)
        int[][] matrix = {
                {1, 2, 0},
                {1, 0, 0},
                {0, 0, 0}
        };
        GameField field = new GameField(matrix);

        Optional<Point> bestMove = MiniMax.findBestMove(field);

        assertTrue(bestMove.isPresent());
        assertEquals(0, bestMove.get().getX());
        assertEquals(2, bestMove.get().getY());
    }

    @Test
    @DisplayName("На полностью заполненном поле ход невозможен")
    void shouldReturnEmptyOnFullField() {
        int[][] matrix = {
                {1, 2, 1},
                {1, 2, 2},
                {2, 1, 1}
        };
        GameField field = new GameField(matrix);

        Optional<Point> bestMove = MiniMax.findBestMove(field);

        assertTrue(bestMove.isEmpty());
    }
}
