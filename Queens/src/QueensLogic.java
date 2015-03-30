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
    private int x = 0;
    private int y = 0;
    private int size = 0;
    private int[][] board;
    private BDDFactory factory;
    private int[][] lookupTable;

    public QueensLogic() {
       //constructor
    }

    public void initializeGame(int size) {
        this.x = size;
        this.y = size;
        this.size = size;
        this.board = new int[x][y];

        // Initialize Lookup table.
        lookupTable = new int[x][y];

        int count = 0;
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j ++){
                lookupTable[i][j] = count;
                count++;
            }
        }

        List<Integer> test = getRestrictPositions(11);
        Collections.sort(test);

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
        }
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
        BDD restriction = null;
        for(int i = 0; i < x; i++){
            if(i != column){
                if(restriction == null){
                    restriction = factory.nithVar(i);
                } else {
                    restriction.and(factory.nithVar(i));
                }
            }
        }

        board.printSet();
        board = board.restrict(restriction);
        board.printSet();

//        for(int j = 0; j < y; j++){
//
//
//        }

        return board;
    }

    public List<Integer> getRestrictPositions(int index){
        ArrayList<Integer> result = new ArrayList<Integer>();

        // Calculate lookup coordinates corresponding to index.
        int row = (int)Math.floor(index / size);
        int column = index % size;

        // Add horizontal and vertical values to result
        for(int i = 0; i < size; i++){
            if(lookupTable[row][i] != index){
                result.add(lookupTable[row][i]);
            }
            if(lookupTable[i][column] != index){
                result.add(lookupTable[i][column]);
            }
        }

        boolean checkLeft = true;
        boolean checkRight = true;
        int offset = 1;
        while(checkLeft || checkRight){
            if(row - offset < 0  || column - offset < 0){
                checkLeft = false;
            } if(column + offset >= size || row + offset >= size) {
                checkRight = false;
            }
            if(checkLeft){
                int value = lookupTable[column - offset][row - offset];
                if(value != index){
                    result.add(value);
                }

            }
            if(checkRight){
                int value = lookupTable[column + offset][row + offset];
                if(value != index){
                    result.add(value);
                }
            }
            offset++;
        }

        checkLeft = true;
        checkRight = true;
        offset = 1;
        while(checkLeft || checkRight){
            if(row + offset >= size  || column - offset < 0){
                checkLeft = false;
            } if(column + offset >= size || row - offset < 0) {
                checkRight = false;
            }
            if(checkLeft){
                int value = lookupTable[column - offset][row + offset];
                if(value != index){
                    result.add(value);
                }

            }
            if(checkRight){
                int value = lookupTable[column + offset][row - offset];
                if(value != index){
                    result.add(value);
                }
            }
            offset++;
        }

    return result;
    }
}
