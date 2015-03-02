import com.sun.tools.javac.util.Pair;

import java.util.Random;

/**
 * Created by brandt on 02/03/15.
 */
public class SimlMfliGameLogic implements IGameLogic {
    private final int WIN_CONDITION = 4;

    private int columns = 0;
    private int rows = 0;
    private int playerID;
    private int[][] boardState;
    private int[] nextAvailableRow;
    private Pair<Integer, Integer> lastCointPosition;   // column, row

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
        // insert a token in the next available row for the specified column.
        int nextRow = nextAvailableRow[column];

        boardState[column][nextRow] = playerID;

        // update the next available row for the column.
        nextAvailableRow[column] -= 1;

        // save last coin placement
        lastCointPosition = new Pair(column, nextRow);
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
        int column = lastCointPosition.fst;
        int row = lastCointPosition.snd;
        int playerID = boardState[column][row];

        Boolean gameOver = checkHorizontal(playerID, column, row);

        if(gameOver) {
            return playerID == 1 ? Winner.PLAYER1 : Winner.PLAYER2;
        } else {
            return isBoardFull() ? Winner.TIE : Winner.NOT_FINISHED;
        }
    }

    public Boolean checkHorizontal(int playerID, int initialColumn, int initialRow) {
        int count = 1;

        // check right
        for(int i = 1; i <= WIN_CONDITION; i++) {
            if(initialColumn + i >= columns) {
                // out of bounds
                break;
            }

            if (boardState[initialColumn + i][initialRow] == playerID) {
                count++;
            } else {
                break;
            }
        }

        if(count == WIN_CONDITION) {
            System.out.println("count is == win" + count);
            return true;
        }

        return false;
    }

    public Boolean checkVertical(int playerID, int initialColumn, int initialRow) {

    }

    public Boolean checkDiagonal(int playerID, int initialColumn, int initialRow) {

    }

    private Boolean isBoardFull() {
        return false;
    }

    private Boolean isColumnFull(int column) {
        return nextAvailableRow[column] == -1;
    }
}
