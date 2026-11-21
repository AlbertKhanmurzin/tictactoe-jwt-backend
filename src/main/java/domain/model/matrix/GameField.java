package domain.model.matrix;

import java.util.Arrays;

public class GameField {
    public static final int SIZE = 3;
    private final int[][] matrix;

    public GameField(){
        this.matrix = new int[SIZE][SIZE];
    }

    public GameField(int[][] matrix){
            this.matrix = new int [SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                this.matrix[i] = Arrays.copyOf(matrix[i], SIZE);
            }
    }

    public GameField(GameField needCopy){
        this.matrix = new int [SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            this.matrix[i] = Arrays.copyOf(needCopy.matrix[i], SIZE);
        }
    }

    public int[][] getMatrix() {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            copy[i] = Arrays.copyOf(matrix[i], SIZE);
        }
        return copy;
    }

    public int getCell(int x, int y) {
        return matrix[y][x];
    }
    public void setCell(int x, int y, int player){
            this.matrix[y][x] = player;
    }

    public void setCell(Point x_y, int player){
        int x = x_y.getX();
        int y = x_y.getY();
        this.matrix[y][x] = player;
    }

    public boolean isEmpty(int x, int y) {
        return matrix[y][x] == 0;
    }

    public void clearCell(int x, int y) {
        matrix[y][x] = 0;
    }

    public void clearCell(Point x_y) {
        int x = x_y.getX();
        int y = x_y.getY();
        matrix[y][x] = 0;
    }

    public int checkWinner() {
        // check horizontal amd vertical
        for (int i = 0; i < SIZE; i++) {
            if (checkLine(matrix[i][0], matrix[i][1], matrix[i][2])) return matrix[i][0]; // horizontal
            if (checkLine(matrix[0][i], matrix[1][i], matrix[2][i])) return matrix[0][i]; // vertical
        }
        // diagonal
        if (checkLine(matrix[0][0], matrix[1][1], matrix[2][2])) return matrix[0][0];
        if (checkLine(matrix[0][2], matrix[1][1], matrix[2][0])) return matrix[0][2];

        return 0; // draw
    }

public boolean isFull(){
    for (int i = 0; i < SIZE; i++) {
        if(isEmpty(0,i) || isEmpty(1,i) || isEmpty(2,i)){
            return false;
        }
    }
    return true;
}

    private boolean checkLine(int a, int b, int c) {
        return a != 0 && a == b && b == c;
    }

    public boolean areEqual(GameField compared){
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if(this.matrix[j][i] != compared.matrix[j][i]){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Matrix{" +
                "matrix=" + Arrays.deepToString(matrix) +
                '}';
    }
}
