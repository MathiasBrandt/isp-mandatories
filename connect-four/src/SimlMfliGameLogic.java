import com.sun.tools.javac.util.Pair;

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
    private Pair<Integer, Integer> lastCoinPosition;   // column, row

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
        lastCoinPosition = new Pair(column, nextRow);
    }

    @Override
    public int decideNextMove() {
//        while(true) {
//            Random random = new Random();
//            int column = random.nextInt(columns);
//
//            if (!isColumnFull(column)) {
//                return column;
//            }
//        }
        if (isColumnFull(columns - 1)){
            return columns - 2;
        }
        if(isColumnFull(0)){
            return columns - 1;
        } else {
            return 0;
        }

    }

    @Override
    public Winner gameFinished() {
        int column = lastCoinPosition.fst;
        int row = lastCoinPosition.snd;
        int playerID = boardState[column][row];

        Boolean gameOver = checkHorizontal(playerID, column, row) || checkVertical(playerID, column, row) || checkDiagonalTwo(playerID, column, row) || checkDiagonalOne(playerID, column, row);

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
        int offset = 1;

        while(count < WIN_CONDITION) {
            System.out.println("offset is " + offset);

            // bound checks
            if(initialColumn + offset >= columns) {
                System.out.println("right bound exceeded");
                // right bound exceeded
                // check to the left of initial position
                offset = -1;
                continue;

            } else if(initialColumn + offset < 0) {
                System.out.println("left bound exceeded");
                // left bound exceeded, return false since we have already checked to the right
                return false;
            }

            if(boardState[initialColumn + offset][initialRow] == playerID) {
                // we found one more coin in succession of the previous or initial coin
                count++;
                System.out.println("Updating count to " + count);

                // if offset is positive it means we are currently checking to the right so increase it.
                // else, we are checking to the left so decrease it.
                offset += offset > 0 ? 1 : -1;
            } else {
                if(offset > 0) {
                    System.out.println("right check done");
                    // right check is done, now check left
                    offset = -1;
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
        System.out.println("---");

        // start counting from initialRow + 1, since we placed a coin in initialRow.
        // Also, that's why count starts at 1 instead of 0. We don't need to check initialRow.
        int count = 1;
        int offset = initialRow + 1;

        while(count < WIN_CONDITION) {
            System.out.println("currentRow is " + offset);

            if(offset >= rows) {
                // lower bound exceeded
                System.out.println("lower bound exceeded");
                return false;
            }

            if(boardState[initialColumn][offset] == playerID) {
                // we found a coin in succession of the previous or initial coin.
                count++;
                System.out.println("updating count to " + count);
            } else {
                // the coin does not match
                return false;
            }

            offset++;
        }

        return true;
    }

    /**
     * Checks if the player has connected four on the diagonal going downwards from left to right.
     * @param playerID
     * @param initialColumn
     * @param initialRow
     * @return
     */
    public Boolean checkDiagonalOne(int playerID, int initialColumn, int initialRow) {
        int count = 1;
        int offset = 1;
        boolean checkLeft = true;
        boolean checkRight = true;

        // Check
        while(count < WIN_CONDITION) {
            System.out.println(count);
            // If we are done checking left and right, stop.
            if(!checkLeft && !checkRight){
                return false;
            }

            //
            if(initialRow - offset < 0 || initialColumn - offset < 0){
                // out of bounds on the left hand side.
                System.out.println("Not checking left anymore");
                System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
                checkLeft = false;
            }
            if(initialRow + offset >= rows || initialColumn + offset >= columns){
                // Check bounds on the right hand side.
                checkRight = false;
                System.out.println("Not checking right anymore");
                System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
            }

            if(checkLeft){
                if (boardState[initialColumn - offset][initialRow - offset] == playerID) {
                    count++;
                } else {
                    System.out.println("Not checking left anymore");
                    System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
                    checkLeft = false;
                }
            }
            if (checkRight){
                if (boardState[initialColumn + offset][initialRow + offset] == playerID) {
                    count++;
                } else {
                    System.out.println("Not checking right anymore");
                    System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
                    checkRight = false;
                }
            }
            offset++;
        }

        if(count == WIN_CONDITION){
            return true;
        }
        return false;
    }

    /**
     * Checks if the player has connected four on the diagonal going upwards from left to right.
     * @param playerID
     * @param initialColumn
     * @param initialRow
     * @return
     */
    public Boolean checkDiagonalTwo(int playerID, int initialColumn, int initialRow) {
        int count = 1;
        int offset = 1;
        boolean checkLeft = true;
        boolean checkRight = true;

        // Check
        while(count < WIN_CONDITION) {
            if(!checkLeft && !checkRight){
                return false;
            }
            
            if(initialRow + offset >= rows || initialColumn - offset < 0){
                // out of bounds on the left hand side.
                System.out.println("Not checking left anymore");
                System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
                checkLeft = false;
            }
            if(initialRow - offset < 0 || initialColumn + offset >= columns){
                // out of bounds on the right hand side.
                checkRight = false;
                System.out.println("Not checking right anymore");
                System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
            }

            if(checkLeft){
                if (boardState[initialColumn - offset][initialRow + offset] == playerID) {
                    count++;
                } else {
                    System.out.println("Not checking left anymore");
                    System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
                    checkLeft = false;
                }
            }
            if (checkRight){
                if (boardState[initialColumn + offset][initialRow - offset] == playerID) {
                    count++;
                } else {
                    System.out.println("Not checking right anymore");
                    System.out.println(String.format("Offset: %d InitialRow: %d InitialColumn: %d", offset, initialRow, initialColumn));
                    checkRight = false;
                }
            }
            offset++;
            System.out.println(count);
        }

        if(count == WIN_CONDITION){
            return true;
        }
        return false;
    }

    private Boolean isBoardFull() {
        for(int i = 0; i < columns; i++) {
            if(!isColumnFull(i)) {
                return false;
            }
        }

        return true;
    }

    private Boolean isColumnFull(int column) {
        return nextAvailableRow[column] == -1;
    }
}
