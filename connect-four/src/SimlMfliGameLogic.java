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
        /*while(true) {
            Random random = new Random();
            int column = random.nextInt(columns);

            if (!isColumnFull(column)) {
                return column;
            }
        }*/

        return 0;
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
        System.out.println("---");

        // count is initially 1 because we placed a coin
        int count = 1;
        // always check to the right first
        int increment = 1;

        while(count < WIN_CONDITION) {
            System.out.println("increment is " + increment);

            // bound checks
            if(initialColumn + increment >= columns) {
                System.out.println("right bound exceeded");
                // right bound exceeded
                // check to the left of initial position
                increment = -1;
                continue;

            } else if(initialColumn + increment < 0) {
                System.out.println("left bound exceeded");
                // left bound exceeded, return false since we have already checked to the right
                return false;
            }

            if(boardState[initialColumn + increment][initialRow] == playerID) {
                // we found one more coin in succession of the previous or initial coin
                count++;
                System.out.println("Updating count to " + count);

                // if increment is positive it means we are currently checking to the right so increase it.
                // else, we are checking to the left so decrease it.
                increment += increment > 0 ? 1 : -1;
            } else {
                if(increment > 0) {
                    System.out.println("right check done");
                    // right check is done, now check left
                    increment = -1;
                } else {
                    System.out.println("left check done");
                    // left check is done, already checked right, return false
                    return false;
                }
            }
        }

        return true;
    }

    public Boolean checkVertical(int playerID, int initialColumn, int initialRow) {
        return false;
    }

    public Boolean checkDiagonal(int playerID, int initialColumn, int initialRow) {
        return false;
    }

    private Boolean isBoardFull() {
        return false;
    }

    private Boolean isColumnFull(int column) {
        return nextAvailableRow[column] == -1;
    }
}
