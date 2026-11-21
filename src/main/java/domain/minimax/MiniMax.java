package domain.minimax;

import domain.model.matrix.*;

import java.util.Optional;

public class MiniMax {

    private static final int PLAYER = 1;
    private static final int AI = 2;

    private static int minimax(GameField gameField, int depth, boolean isAI){
        //проверка победы
        int winner = gameField.checkWinner();
        if(winner == PLAYER){return -10 + depth;}
        if (winner == AI) {return 10 - depth;}
        if(gameField.isFull()) return 0;

        // рекурсивная часть
        int bestScore;
        if(isAI){
            bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < GameField.SIZE; i++) {
                for (int j = 0; j < GameField.SIZE; j++) {
                    if (gameField.getCell(j,i) == 0) {
                        gameField.setCell( j, i,AI);
                        int score = minimax(gameField, depth + 1, false);
                        gameField.clearCell(j, i);
                        if(bestScore < score) bestScore = score;
                    }
                }
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < GameField.SIZE; i++) {
                for (int j = 0; j < GameField.SIZE; j++) {
                    if (gameField.getCell(j,i) == 0) {
                        gameField.setCell( j, i,PLAYER);
                        int score = minimax(gameField, depth + 1, true);
                        gameField.clearCell(j, i);
                        if(bestScore > score) bestScore = score;
                    }
                }
            }
        }
        return bestScore;
    }

public static Optional<Point> findBestMove(GameField gameField) {
    int bestScore = Integer.MIN_VALUE;
    Point bestMove = null;

    for (int i = 0; i < GameField.SIZE; i++) {
        for (int j = 0; j < GameField.SIZE; j++) {
            if (gameField.getCell(j, i) == 0) {
                gameField.setCell(j, i, AI);
                int score = minimax(gameField, 0, false);
                gameField.clearCell(j, i);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new Point(j, i);
                }
            }
        }
    }

    return Optional.ofNullable(bestMove);
}
}