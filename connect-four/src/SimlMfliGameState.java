import com.sun.tools.javac.util.Pair;

import java.util.Arrays;

/**
 *  Intelligent Systems Programming
 *  Connect Four
 * @Author Mathias Flink Brandt.(mfli@itu.dk)
 * @Author Simon Langhoff (siml@itu.dk)
 */
public class SimlMfliGameState {
    public static final int WIN_CONDITION = 4;

    private int[][] board;
    private int columns;
    private int rows;
    private Pair<Integer, Integer> lastCoinPosition;

    public SimlMfliGameState(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;

        board = new int[columns][rows];
    }

    public SimlMfliGameState(int[][] board, Pair<Integer, Integer> lastCoinPosition) {
        this.columns = board.length;
        this.rows = board[0].length;

        this.board = board;
        this.lastCoinPosition = lastCoinPosition;
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

        board[column][row] = playerId;

        // save last coin placement
        lastCoinPosition = new Pair(column, row);
    }

    /**
     * Returns the row id for where the coin will be placed if inserted in the specified column.
     */
    public int getNextAvailableRow(int column) {
        for(int i = rows-1; i >= 0; i--) {
            if(board[column][i] == 0) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Checks the current state of the game and returns the winner or Winner.TIE if the game is over, otherwise returns Winner.NOT_FINISHED.
     */
    public IGameLogic.Winner gameFinished() {
        if(lastCoinPosition == null){
            return IGameLogic.Winner.NOT_FINISHED;
        }
        int column = lastCoinPosition.fst;
        int row = lastCoinPosition.snd;
        int playerID = board[column][row];

        int horizontalCount = checkHorizontal(playerID, column, row, false);
        int verticalCount = checkVertical(playerID, column, row, false);
        int diagonalOneCount = checkDiagonalOne(playerID, column, row, false);
        int diagonalTwoCount = checkDiagonalTwo(playerID, column, row, false);

        boolean gameOver = horizontalCount >= WIN_CONDITION ||
                           verticalCount >= WIN_CONDITION ||
                           diagonalOneCount >= WIN_CONDITION ||
                           diagonalTwoCount >= WIN_CONDITION;

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

    public SimlMfliGameState copyState() {
        int[][] newState = new int[columns][rows];

        for(int i = 0; i < columns; i++) {
            newState[i] = Arrays.copyOf(board[i], board[i].length);
        }

        return new SimlMfliGameState(newState, lastCoinPosition);
    }

    public void printState() {
        System.out.print("  ");
        for(int i = 0; i < columns; i++) {
            System.out.print(i + " ");
        }

        System.out.println();

        for(int i = 0; i < rows; i++) {
            System.out.print(i + " ");
            for(int j = 0; j < columns; j++) {
                System.out.print(board[j][i] + " ");
            }

            System.out.println();
        }
    }

    /**
     * Counts the consecutive amount of coins on the horizontal axis starting from where the last coin was placed.
     */
    public int checkHorizontal(int playerID, int initialColumn, int initialRow, boolean includeBlanks) {
        // count is initially 1 because we placed a coin
        int count = 1;
        int offset = 1;
        boolean checkLeft = true;
        boolean checkRight = true;

        while(checkLeft || checkRight) {
            // bound checks
            if(initialColumn + offset >= columns) {
                // right bound exceeded
                checkRight = false;

            }
            if(initialColumn - offset < 0) {
                // left bound exceeded
                checkLeft = false;
            }

            if(checkLeft) {
                if(board[initialColumn - offset][initialRow] == playerID) {
                    // we found one more coin in succession of the previous coin
                    count++;
                } else if(includeBlanks && board[initialColumn - offset][initialRow] == SimlMfliMiniMaxer.PLAYER_BLANK) {
                    // we are also counting blank, or "available" spots on the board
                    count++;
                } else {
                    checkLeft = false;
                }
            }

            if(checkRight) {
                if(board[initialColumn + offset][initialRow] == playerID) {
                    count++;
                } else if(includeBlanks && board[initialColumn + offset][initialRow] == SimlMfliMiniMaxer.PLAYER_BLANK) {
                    count++;
                } else {
                    checkRight = false;
                }
            }

            offset++;
        }

        return count;
    }

    /**
     * Counts the amount of consecutive coins on the vertical axis, starting from where the last coin was placed.
     */
    public int checkVertical(int playerID, int initialColumn, int initialRow, boolean includeBlanks) {
        // start counting with an offset of 1, since we placed a coin in initialRow.
        // Also, that's why count starts at 1 instead of 0. We don't need to check initialRow.
        int count = 1;
        int offset = 1;
        boolean checkDown = true;
        boolean checkUp = includeBlanks;

        while(checkDown || checkUp) {
            if(initialRow + offset >= rows) {
                // lower bound exceeded
                checkDown = false;
            }

            if(initialRow - offset < 0) {
                // upper bound exceeded
                checkUp = false;
            }

            if(checkDown) {
                // no need to check for == blank, since there are always coins below
                if (board[initialColumn][initialRow + offset] == playerID) {
                    // we found a coin in succession of the previous coin.
                    count++;
                } else {
                    checkDown = false;
                }
            }

            if(checkUp) {
                // no need to check for == playerID, since there are never coins above
                if(board[initialColumn][initialRow - offset] == SimlMfliMiniMaxer.PLAYER_BLANK) {
                    count++;
                } else {
                    checkDown = false;
                }
            }

            offset++;
        }

        return count;
    }

    /**
     * Counts the amount of consecutive coins on the diagonal axis going downwards from left to right.
     */
    public int checkDiagonalOne(int playerID, int initialColumn, int initialRow, boolean includeBlanks) {
        int count = 1;
        int offset = 1;
        boolean checkLeft = true;
        boolean checkRight = true;

        // Check
        while(checkLeft || checkRight) {
            // check if out of bounds on the left hand side.
            if(initialRow - offset < 0 || initialColumn - offset < 0){
                checkLeft = false;
            }
            if(initialRow + offset >= rows || initialColumn + offset >= columns){
                // Check bounds on the right hand side.
                checkRight = false;
            }

            if(checkLeft){
                if (board[initialColumn - offset][initialRow - offset] == playerID) {
                    count++;
                } else if(includeBlanks && board[initialColumn - offset][initialRow - offset] == SimlMfliMiniMaxer.PLAYER_BLANK) {
                    count++;
                } else {
                    checkLeft = false;
                }
            }
            if (checkRight){
                if (board[initialColumn + offset][initialRow + offset] == playerID) {
                    count++;
                } else if(includeBlanks && board[initialColumn + offset][initialRow + offset] == SimlMfliMiniMaxer.PLAYER_BLANK) {
                    count++;
                } else {
                    checkRight = false;
                }
            }
            offset++;
        }

        return count;
    }

    /**
     * Counts the amount of consecutive coins on the diagonal axis going upwards from left to right.
     */
    public int checkDiagonalTwo(int playerID, int initialColumn, int initialRow, boolean includeBlanks) {
        int count = 1;
        int offset = 1;
        boolean checkLeft = true;
        boolean checkRight = true;

        // Check
        while(checkLeft || checkRight) {
            // check if out of bounds on the left hand side.
            if(initialRow + offset >= rows || initialColumn - offset < 0){
                checkLeft = false;
            }
            if(initialRow - offset < 0 || initialColumn + offset >= columns){
                // out of bounds on the right hand side.
                checkRight = false;
            }

            if(checkLeft) {
                if (board[initialColumn - offset][initialRow + offset] == playerID) {
                    count++;
                } else if(includeBlanks && board[initialColumn - offset][initialRow + offset] == SimlMfliMiniMaxer.PLAYER_BLANK) {
                    count++;
                } else {
                    checkLeft = false;
                }
            }
            if (checkRight){
                if (board[initialColumn + offset][initialRow - offset] == playerID) {
                    count++;
                } else if(includeBlanks && board[initialColumn + offset][initialRow - offset] == SimlMfliMiniMaxer.PLAYER_BLANK) {
                    count++;
                } else {
                    checkRight = false;
                }
            }
            offset++;
        }

        return count;
    }
}
