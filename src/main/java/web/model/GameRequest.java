package web.model;

public class GameRequest {
    private int[][] matrix;

    public GameRequest() {
    }

    public int[][] getMatrix(){
        return this.matrix;
    }

    public void setMatrix(int[][] matrix){
        this.matrix = matrix;
    }
}
