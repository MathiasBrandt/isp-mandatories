/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Stavros Amanatidis
 *
 */
import java.util.*;

import net.sf.javabdd.*;

public class QueensLogic {
//    private int cols = 0;
//    private int rows = 0;
    private int N = 0;
    private int[][] board;
    private BDDFactory factory;
    private int[][] lookupTable;    // [col][row]

    public QueensLogic() {
       //constructor
    }

    public void initializeGame(int size) {
        this.N = size;
        this.board = new int[N][N];

        // Initialize Lookup table.
        buildLookupTable();
        printLookupTable();

        /*
        // init vars
        int nodeCount = 2000000;
        int cacheSize = (int)(nodeCount * 0.10);

        factory = JFactory.init(nodeCount, cacheSize);
        factory.setVarNum(size * size);

        // Assign rules for each variable
        for(int n = 0; n < size * size; n++){
            BDD var = null;

            // Max 1 queen per column
            for(int i = 0; i < size; i++){

                if(n != i){
                    if(var == null){
                        var = factory.nithVar(n).or(factory.nithVar(i));
                    } else {
                        var = var.and(factory.nithVar(n).or(factory.nithVar(i))); // (!A || !B) && (!A || !D) ...
                    }
                }
            }

            // Max 1 queen per row
            for(int i = 0; i < size; i++){

            }
        }*/
    }

   
    public int[][] getGameBoard() {
        return board;
    }

    public boolean insertQueen(int column, int row) {
        if (board[column][row] == -1 || board[column][row] == 1) {
            return true;
        }
        
        board[column][row] = 1;

        // restrictBDD(factory column, row);
        
        // put some logic here..
      
        return true;
    }

    public BDD restrictBDD(BDD board, int column, int row){
//        BDD restriction = null;
//        for(int i = 0; i < cols; i++){
//            if(i != column){
//                if(restriction == null){
//                    restriction = factory.nithVar(i);
//                } else {
//                    restriction.and(factory.nithVar(i));
//                }
//            }
//        }
//
//        board.printSet();
//        board = board.restrict(restriction);
//        board.printSet();
//
//        for(int j = 0; j < rows; j++){
//
//
//        }
//
//        return board;

        return null;
    }

    /**
     * Return a list of indices that will be affected by positioning a queen at the specified index.
     * @param index The index where the next queen will be placed.
     * @return The list of affected indices.
     */
    public List<Integer> getRestrictPositions(int index){
        List<Integer> result = getHorizontalRestrictPositions(index);
        result.addAll(getVerticalRestrictPositions(index));
        result.addAll(getDiagonalOneRestrictPositions(index));
        result.addAll(getDiagonalTwoRestrictPositions(index));

        return result;
    }

    private List<Integer> getHorizontalRestrictPositions(int var) {
        // find out which row we're in
        int row = (int) Math.floor(var/ N);

        System.out.println(String.format("restricting var %d in row %d", var, row));

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

        System.out.println(String.format("restricting var %d in col %d", var, col));

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
