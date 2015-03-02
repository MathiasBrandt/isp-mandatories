import java.util.Random;

/**
 * Created by brandt on 02/03/15.
 */
public class SimlMfliGameLogic implements IGameLogic {
    private int columns = 0;
    private int rows = 0;
    private int playerID;
    private int[][] boardState;
    private int[] nextAvailableRow;

    public SimlMfliGameLogic() {

    }

    @Override
    public void initializeGame(int columns, int rows, int playerID) {
        this.columns = columns;
        this.rows = rows;
        this.playerID = playerID;

        boardState = new int[columns][rows];
        nextAvailableRow = new int[columns];

        // initialize all values in nextAvailableRow to row count.
        for(int i = 0; i < nextAvailableRow.length; i++) {
            nextAvailableRow[i] = rows - 1;
        }
    }

    @Override
    public void insertCoin(int column, int playerID) {
        System.out.print(String.format("Column: %d, playerID: %d", column, playerID));
        // insert a token in the next available row for the specified column.
        int nextRow = nextAvailableRow[column];
        System.out.print(nextRow);
        boardState[column][nextRow] = playerID;

        // update the next available row for the column.
        nextAvailableRow[column] -= 1;
    }

    @Override
    public int decideNextMove() {
        while(true) {
            Random random = new Random();
            int column = random.nextInt(columns);

            if (!isColumnFull(column)) {
                return column;
            }
        }
    }

    @Override
    public Winner gameFinished() {
        return Winner.NOT_FINISHED;
    }

    private Boolean isColumnFull(int column) {
        return nextAvailableRow[column] == -1;
    }
}
