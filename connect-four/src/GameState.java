import com.sun.tools.javac.util.Pair;

import java.util.Arrays;

/**
 * Created by brandt on 10/03/15.
 */
public class GameState {
    public final int WIN_CONDITION = 4;

    private int[][] board;
    private int columns;
    private int rows;
    private Pair<Integer, Integer> lastCoinPosition;
    private int lastPlayer;

    public GameState(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;

        board = new int[columns][rows];
    }

    public GameState(int columns, int rows, int[][] board, Pair<Integer, Integer> lastCoinPosition, int lastPlayer) {
        this.columns = columns;
        this.rows = rows;

        this.board = board;
        this.lastCoinPosition = lastCoinPosition;
        this.lastPlayer = lastPlayer;
    }

    public Pair<Integer, Integer> getLastCoinPosition() {
        return lastCoinPosition;
    }

    public int[][] getBoard() {
        return board;
    }

    public int getColumnCount() {
        return columns;
    }

    public int getRowCount() {
        return rows;
    }

    public void insertCoin(int column, int playerId) {
        // insert a token in the next available row for the specified column.
        int row = getNextAvailableRow(column);
        //System.out.println("Placing coin in col: " + column + " row: " + row);

        board[column][row] = playerId;

        lastPlayer = playerId;

        // save last coin placement
        lastCoinPosition = new Pair(column, row);
    }

    public int getNextAvailableRow(int column) {
        for(int i = rows-1; i >= 0; i--) {
            if(board[column][i] == 0) {
                return i;
            }
        }

        return -1;
    }

    public IGameLogic.Winner gameFinished() {
        if(lastCoinPosition == null){
            return IGameLogic.Winner.NOT_FINISHED;
        }
        int column = lastCoinPosition.fst;
        int row = lastCoinPosition.snd;
        int playerID = board[column][row];



        Boolean gameOver = checkHorizontal(playerID, column, row) || checkVertical(playerID, column, row) || checkDiagonalOne(playerID, column, row) == WIN_CONDITION|| checkDiagonalTwo(playerID, column, row) == WIN_CONDITION;

        if(gameOver) {
            return playerID == 1 ? IGameLogic.Winner.PLAYER1 : IGameLogic.Winner.PLAYER2;
        } else {
            return isBoardFull() ? IGameLogic.Winner.TIE : IGameLogic.Winner.NOT_FINISHED;
        }
    }

    private Boolean isBoardFull() {
        for(int i = 0; i < columns; i++) {
            if(!isColumnFull(i)) {
                return false;
            }
        }

        return true;
    }

    public Boolean isColumnFull(int column) {
        return getNextAvailableRow(column) < 0;
    }

    public GameState copyState() {
        int[][] newState = new int[columns][rows];

        for(int i = 0; i < columns; i++) {
            newState[i] = Arrays.copyOf(board[i], board[i].length);
        }

        return new GameState(columns, rows, newState, lastCoinPosition, lastPlayer);
    }

    public void printState() {
        for(int i = 0; i < rows; i++) {
            System.out.print(i + " ");
            for(int j = 0; j < columns; j++) {
                System.out.print(board[j][i]);
            }

            System.out.println();
        }
    }

    public Boolean checkHorizontal(int playerID, int initialColumn, int initialRow) {
        //System.out.println("---");
        // count is initially 1 because we placed a coin
        int count = 1;
        // always check to the right first
        int offset = 1;

        while(count < WIN_CONDITION) {
            //System.out.println("offset is " + offset);

            // bound checks
            if(initialColumn + offset >= columns) {
                //System.out.println("right bound exceeded");
                // right bound exceeded
                // check to the left of initial position
                offset = -1;
                continue;

            } else if(initialColumn + offset < 0) {
                //System.out.println("left bound exceeded");
                // left bound exceeded, return false since we have already checked to the right
                return false;
            }

            if(board[initialColumn + offset][initialRow] == playerID) {
                // we found one more coin in succession of the previous or initial coin
                count++;
                //System.out.println("Updating count to " + count);

                // if offset is positive it means we are currently checking to the right so increase it.
                // else, we are checking to the left so decrease it.
                offset += offset > 0 ? 1 : -1;
            } else {
                if(offset > 0) {
                    //System.out.println("right check done");
                    // right check is done, now check left
                    offset = -1;
                } else {
                    //System.out.println("left check done");
                    // left check is done, already checked right, return false
                    return false;
                }
            }
        }

        return true;
    }

    public Boolean checkVertical(int playerID, int initialColumn, int initialRow) {
        //System.out.println("---");

        // start counting from initialRow + 1, since we placed a coin in initialRow.
        // Also, that's why count starts at 1 instead of 0. We don't need to check initialRow.
        int count = 1;
        int offset = initialRow + 1;

        while(count < WIN_CONDITION) {
            //System.out.println("currentRow is " + offset);

            if(offset >= rows) {
                // lower bound exceeded
                //System.out.println("lower bound exceeded");
                return false;
            }

            if(board[initialColumn][offset] == playerID) {
                // we found a coin in succession of the previous or initial coin.
                count++;
                //System.out.println("updating count to " + count);
            } else {
                // the coin does not match
                return false;
            }

            offset++;
        }

        return true;
    }

    /**
     * Counts the consecutive amount of coins of the player who last placed a coin downwards from left to right.
     * @param playerID
     * @param initialColumn
     * @param initialRow
     * @return
     */
    public int checkDiagonalOne(int playerID, int initialColumn, int initialRow) {
        int count = 1;
        int offset = 1;
        boolean checkLeft = true;
        boolean checkRight = true;

        // Check
        while(checkLeft || checkRight) {
            // check if out of bounds on the left hand side.
            if(initialRow - offset < 0 || initialColumn - offset < 0){
                //System.out.println("Not checking left anymore");
                //System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
                checkLeft = false;
            }
            if(initialRow + offset >= rows || initialColumn + offset >= columns){
                // Check bounds on the right hand side.
                checkRight = false;
                //System.out.println("Not checking right anymore");
                //System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
            }

            if(checkLeft){
                if (board[initialColumn - offset][initialRow - offset] == playerID) {
                    count++;
                } else {
                    //System.out.println("Not checking left anymore");
                    //System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
                    checkLeft = false;
                }
            }
            if (checkRight){
                if (board[initialColumn + offset][initialRow + offset] == playerID) {
                    count++;
                } else {
                    //System.out.println("Not checking right anymore");
                    //System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
                    checkRight = false;
                }
            }
            offset++;
        }


        return count;
    }

    /**
     * Counts the consecutive amount of coins of the player who last placed a coin upwards from left to right.
     * @param playerID
     * @param initialColumn
     * @param initialRow
     * @return
     */
    public int checkDiagonalTwo(int playerID, int initialColumn, int initialRow) {
        int count = 1;
        int offset = 1;
        boolean checkLeft = true;
        boolean checkRight = true;

        // Check
        while(checkLeft || checkRight) {
            // check if out of bounds on the left hand side.
            if(initialRow + offset >= rows || initialColumn - offset < 0){
                //System.out.println("Not checking left anymore");
                //System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
                checkLeft = false;
            }
            if(initialRow - offset < 0 || initialColumn + offset >= columns){
                // out of bounds on the right hand side.
                checkRight = false;
                //System.out.println("Not checking right anymore");
                //System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
            }

            if(checkLeft){
                if (board[initialColumn - offset][initialRow + offset] == playerID) {
                    count++;
                } else {
                    //System.out.println("Not checking left anymore");
                    //System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
                    checkLeft = false;
                }
            }
            if (checkRight){
                if (board[initialColumn + offset][initialRow - offset] == playerID) {
                    count++;
                } else {
                    //System.out.println("Not checking right anymore");
                    //System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
                    checkRight = false;
                }
            }
            offset++;
        }

        return count;
    }
}
