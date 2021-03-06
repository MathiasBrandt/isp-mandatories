/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Stavros Amanatidis
 *
 */
import java.security.InvalidParameterException;
import java.util.*;

import com.sun.tools.javac.util.Pair;
import net.sf.javabdd.*;

public class QueensLogic {
//    private int cols = 0;
//    private int rows = 0;
    private int N = 0;
    private int[][] board;
    private BDDFactory factory;
    private BDD nQueensBdd;
    private int[][] lookupTable;    // [col][row]
    private List<Integer> queens;

    public QueensLogic() {
       //constructor
    }

    public void initializeGame(int size) {
        this.N = size;
        this.board = new int[N][N];

        queens = new ArrayList<Integer>();

        // Initialize Lookup table.
        buildLookupTable();
        printLookupTable();

        int nodeCount = 2000000;
        int cacheSize = (int)(nodeCount * 0.10);

        factory = JFactory.init(nodeCount, cacheSize);
        factory.setVarNum(N * N);

        nQueensBdd = factory.one();

        buildRules();
        updateBoardPositions();
    }

   
    public int[][] getGameBoard() {
        return board;
    }

    public boolean insertQueen(int column, int row) {
        if (board[column][row] == -1 || board[column][row] == 1) {
            return true;
        }

        queens.add(lookupTable[column][row]);

        // update the GUI's underlying data structure (i.e., place a queen)
        board[column][row] = 1;

        restrictOnInsert(column, row);

        updateBoardPositions();

        return true;
    }

    /**
     * Restricts the BDD according to the placement of a queen in the specified column and row.
     * The queen's placement is restricted to TRUE, while positions threatened by the queen are
     * restricted to FALSE.
     */
    public void restrictOnInsert(int col, int row) {
        BDD restriction = factory.one();

        for(Integer queen : queens) {
            restriction = restriction.and(factory.ithVar(queen));

            List<Integer> restrictVars = getRestrictPositions(queen);

            for(Integer restrictVar : restrictVars) {
                restriction = restriction.and(factory.nithVar(restrictVar));
            }

        }

        nQueensBdd = nQueensBdd.restrict(restriction);
    }

    /**
     * The method updates the underlying data structure of the board in the graphical
     * user interface. The GUI will automatically put crosses on invalid positions.
     */
    private void updateBoardPositions() {
        // block positions that are immediately threatened by a queen
        for(Integer queen : queens) {
            List<Integer> restrictPositions = getRestrictPositions(queen);

            for(Integer restrictPosition : restrictPositions) {
                Pair<Integer, Integer> p = getColRow(restrictPosition);
                board[p.fst][p.snd] = -1;
            }
        }

        // block positions that do not lead to a solution
        HashMap<Integer, Integer> positionValues = new HashMap<Integer, Integer>();
        List<byte[]> solutions = nQueensBdd.allsat();

        for(byte[] solution : solutions) {
            for(int var = 0; var < solution.length; var++) {
                if(!positionValues.containsKey(var)) {
                    positionValues.put(var, (int) solution[var]);
                } else {
                    if(positionValues.get(var) < 1) {
                        positionValues.put(var, (int) solution[var]);
                    }
                }
            }
        }

        for(Integer i : positionValues.keySet()) {
            if(positionValues.get(i) <= 0) {
                Pair<Integer, Integer> p = getColRow(i);
                // only set position to -1 if a queen is not present
                if(board[p.fst][p.snd] != 1) {
                    board[p.fst][p.snd] = -1;
                }
            }
        }

        // If a column only has a single available position, insert a queen at that position
        for(int col = 0; col < N; col++) {
            int availablePositions = 0;
            int availableRow = -1;

            for(int row = 0; row < N; row++) {
                if(board[col][row] == 0) {
                    availablePositions++;
                    availableRow = row;
                }
            }

            if(availablePositions == 1) {
                insertQueen(col, availableRow);
                break;
            }
        }
    }

    private Pair<Integer, Integer> getColRow(int var) {
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(lookupTable[i][j] == var) {
                    return new Pair<Integer, Integer>(i, j);
                }
            }
        }

        throw new InvalidParameterException("Var " + var + " was not found in lookup table");
    }

    private void buildRules() {
        for(int var = 0; var < N * N; var++) {
            maxOneQueenPerColumn(var);
            maxOneQueenPerRow(var);
            maxOneQueenInDiagonalOne(var);
            maxOneQueenInDiagonalTwo(var);
            atLeastOneQueenPerColumn();
        }
    }

    private void maxOneQueenPerColumn(int var) {
        List<Integer> restricts = getHorizontalRestrictPositions(var);

        for(int restrictVar : restricts) {
            nQueensBdd = nQueensBdd.and(factory.nithVar(var).or(factory.nithVar(restrictVar)));
        }
    }

    private void maxOneQueenPerRow(int var) {
        List<Integer> restricts = getVerticalRestrictPositions(var);

        for(int restrictVar : restricts) {
            nQueensBdd = nQueensBdd.and(factory.nithVar(var).or(factory.nithVar(restrictVar)));
        }
    }

    private void maxOneQueenInDiagonalOne(int var) {
        List<Integer> restricts = getDiagonalOneRestrictPositions(var);

        for(int restrictVar : restricts) {
            nQueensBdd = nQueensBdd.and(factory.nithVar(var).or(factory.nithVar(restrictVar)));
        }
    }

    private void maxOneQueenInDiagonalTwo(int var) {
        List<Integer> restricts = getDiagonalTwoRestrictPositions(var);

        for(int restrictVar : restricts) {
            nQueensBdd = nQueensBdd.and(factory.nithVar(var).or(factory.nithVar(restrictVar)));
        }
    }

    private void atLeastOneQueenPerColumn() {
        BDD totalRestriction = factory.one();

        for(int col = 0; col < N; col++) {
            BDD columnRestriction = factory.zero();

            for(int row = 0; row < N; row++) {
                int var = lookupTable[col][row];
                columnRestriction = columnRestriction.or(factory.ithVar(var));
            }

            totalRestriction = totalRestriction.and(columnRestriction);
        }

        nQueensBdd = nQueensBdd.and(totalRestriction);
    }

    /**
     * Return a list of indices that will be affected by positioning a queen at the specified index.
     * @param var The index where the next queen will be placed.
     * @return The list of affected indices.
     */
    private List<Integer> getRestrictPositions(int var){
        List<Integer> result = getHorizontalRestrictPositions(var);
        result.addAll(getVerticalRestrictPositions(var));
        result.addAll(getDiagonalOneRestrictPositions(var));
        result.addAll(getDiagonalTwoRestrictPositions(var));

        return result;
    }

    private List<Integer> getHorizontalRestrictPositions(int var) {
        // find out which row we're in
        int row = (int) Math.floor(var/ N);

        List<Integer> result = new ArrayList<Integer>();

        for(int col = 0; col < N; col++) {
            if(lookupTable[col][row] != var) {
                result.add(lookupTable[col][row]);
            }
        }

        return result;
    }

    private List<Integer> getVerticalRestrictPositions(int var) {
        // find out which column we're in
        int col = var % N;

        List<Integer> result = new ArrayList<Integer>();

        for(int row = 0; row < N; row++) {
            if(lookupTable[col][row] != var) {
                result.add(lookupTable[col][row]);
            }
        }

        return result;
    }

    private List<Integer> getDiagonalOneRestrictPositions(int var) {
        int col = var % N;
        int row = (int) Math.floor(var/ N);
        int offset = 1;
        boolean checkLeft = true;
        boolean checkRight = true;
        List<Integer> result = new ArrayList<Integer>();

        while(checkLeft || checkRight) {
            if(col - offset < 0 || row - offset < 0) {
                checkLeft = false;
            }
            if(col + offset >= N || row + offset >= N) {
                checkRight = false;
            }

            if(checkLeft) {
                if(lookupTable[col - offset][row - offset] != var) {
                    result.add(lookupTable[col - offset][row - offset]);
                }
            }

            if(checkRight) {
                if(lookupTable[col + offset][row + offset] != var) {
                    result.add(lookupTable[col + offset][row + offset]);
                }
            }

            offset++;
        }

        return result;
    }

    private List<Integer> getDiagonalTwoRestrictPositions(int var) {
        int col = var % N;
        int row = (int) Math.floor(var/ N);
        int offset = 1;
        boolean checkLeft = true;
        boolean checkRight = true;
        List<Integer> result = new ArrayList<Integer>();

        while(checkLeft || checkRight) {
            if(col - offset < 0 || row + offset >= N) {
                checkLeft = false;
            }
            if(col + offset >= N || row - offset < 0) {
                checkRight = false;
            }

            if(checkLeft) {
                if(lookupTable[col - offset][row + offset] != var) {
                    result.add(lookupTable[col - offset][row + offset]);
                }
            }

            if(checkRight) {
                if(lookupTable[col + offset][row - offset] != var) {
                    result.add(lookupTable[col + offset][row - offset]);
                }
            }

            offset++;
        }

        return result;
    }

    private void buildLookupTable() {
        lookupTable = new int[N][N];

        int count = 0;
        for(int row = 0; row < N; row++) {
            for(int col = 0; col < N; col++) {
                lookupTable[col][row] = count;
                count++;
            }
        }
    }

    private void printLookupTable() {
        System.out.print("   ");
        for(int i = 0; i < N; i++) {
            System.out.print(i + "  ");
        }
        System.out.println();

        for(int row = 0; row < N; row++) {
            System.out.print(row + "  ");

            for(int col = 0; col < N; col++) {
                if(lookupTable[col][row] < 10) {
                    System.out.print(0);
                }
                System.out.print(lookupTable[col][row] + " ");
            }

            System.out.println();
        }
    }
}
