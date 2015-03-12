import com.sun.tools.javac.util.Pair;

import java.util.Arrays;

/**
 * Created by brandt on 10/03/15.
 */
public class GameState {
    public static final int WIN_CONDITION = 4;

    private int[][] board;
    private int columns;
    private int rows;
    private Pair<Integer, Integer> lastCoinPosition;
    //private int lastPlayer;

    public GameState(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;

        board = new int[columns][rows];
    }

    //public GameState(int columns, int rows, int[][] board, Pair<Integer, Integer> lastCoinPosition, int lastPlayer) {
    public GameState(int[][] board, Pair<Integer, Integer> lastCoinPosition) {
        this.columns = board.length;
        this.rows = board[0].length;

        this.board = board;
        this.lastCoinPosition = lastCoinPosition;
        //this.lastPlayer = lastPlayer;
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

//        printState();
//        System.out.println();


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

        // printState();

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

    public GameState copyState() {
        int[][] newState = new int[columns][rows];

        for(int i = 0; i < columns; i++) {
            newState[i] = Arrays.copyOf(board[i], board[i].length);
        }

        //return new GameState(columns, rows, newState, lastCoinPosition, lastPlayer);
        return new GameState(newState, lastCoinPosition);
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
     * Counts the consecutive amount of coins of the player who last placed a coin in the row of which
     * that coin was placed.
     * @param playerID
     * @param initialColumn
     * @param initialRow
     * @return
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
                } else if(includeBlanks && board[initialColumn - offset][initialRow] == MiniMaxer.PLAYER_BLANK) {
                    // we are also counting blank, or "available" spots on the board
                    count++;
                } else {
                    checkLeft = false;
                }
            }

            if(checkRight) {
                if(board[initialColumn + offset][initialRow] == playerID) {
                    count++;
                } else if(includeBlanks && board[initialColumn + offset][initialRow] == MiniMaxer.PLAYER_BLANK) {
                    count++;
                } else {
                    checkRight = false;
                }
            }

            offset++;
        }

        return count;
    }

    public int checkVertical(int playerID, int initialColumn, int initialRow, boolean includeBlanks) {
        // start counting from initialRow + 1, since we placed a coin in initialRow.
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
                if(board[initialColumn][initialRow - offset] == MiniMaxer.PLAYER_BLANK) {
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
     * Counts the consecutive amount of coins of the player who last placed a coin downwards from left to right.
     * @param playerID
     * @param initialColumn
     * @param initialRow
     * @return
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
                } else if(includeBlanks && board[initialColumn - offset][initialRow - offset] == MiniMaxer.PLAYER_BLANK) {
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
                } else if(includeBlanks && board[initialColumn + offset][initialRow + offset] == MiniMaxer.PLAYER_BLANK) {
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
    public int checkDiagonalTwo(int playerID, int initialColumn, int initialRow, boolean includeBlanks) {
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

            if(checkLeft) {
                if (board[initialColumn - offset][initialRow + offset] == playerID) {
                    count++;
                } else if(includeBlanks && board[initialColumn - offset][initialRow + offset] == MiniMaxer.PLAYER_BLANK) {
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
                } else if(includeBlanks && board[initialColumn + offset][initialRow - offset] == MiniMaxer.PLAYER_BLANK) {
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

    public static void main(String[] args) {
        /*int[][] b1 = {{1}};
        GameState g1 = new GameState(b1, null);
        GameState.debugPrint(g1, 0);

        int[][] b2 = {{1}, {0}};
        GameState g2 = new GameState(b2, null);
        GameState.debugPrint(g2, 0);

        int[][] b3 = {{1}, {0}, {0}};
        GameState g3 = new GameState(b3, null);
        GameState.debugPrint(g3, 0);

        int[][] b4 = {{1}, {0}, {0}, {0}};
        GameState g4 = new GameState(b4, null);
        GameState.debugPrint(g4, 0);

        int[][] b5 = {{0}, {1}, {0}, {0}, {0}};
        GameState g5 = new GameState(b5, null);
        GameState.debugPrint(g5, 1);

        int[][] b6 = {{0}, {0}, {1}, {0}, {0}, {0}};
        GameState g6 = new GameState(b6, null);
        GameState.debugPrint(g6, 2);

        int[][] b7 = {{1}, {0}, {0}, {0}, {0}, {0}, {0}};
        GameState g7 = new GameState(b7, null);
        GameState.debugPrint(g7, 3);*/

        int[][] b8 = {{0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 2, 2}, {0, 0, 0, 0, 0, 1}, {0, 0, 0, 0, 2, 2}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}};
        GameState g8 = new GameState(b8, null);
        GameState.debugPrint(g8, 3, 5);
    }

    public static void debugPrint(GameState g, int col, int row) {
        System.out.println("### NEWEST COIN");
        System.out.println("Col: " + col + ", row: " + row);

        System.out.println("### STATE");
        g.printState();

        int possibleWins = 0;
        if(g.checkHorizontal(1, col, row, true) >= g.WIN_CONDITION) { possibleWins++; }
        if(g.checkVertical(1, col, row, true) >= g.WIN_CONDITION) { possibleWins++; }
        if(g.checkDiagonalOne(1, col, row, true) >= g.WIN_CONDITION) { possibleWins++; }
        if(g.checkDiagonalTwo(1, col, row, true) >= g.WIN_CONDITION) { possibleWins++; }

        System.out.println("### POSSIBLE WINS: " + possibleWins);
    }
}
