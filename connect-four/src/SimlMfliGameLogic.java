import com.sun.tools.javac.util.Pair;

import java.io.Console;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by brandt on 02/03/15.
 */
public class SimlMfliGameLogic implements IGameLogic {
    private GameState gameState;
    private MiniMaxer miniMaxer;

    public SimlMfliGameLogic() {

    }

    @Override
    public void initializeGame(int columns, int rows, int playerID) {
        gameState = new GameState(columns, rows);
        miniMaxer = new MiniMaxer(playerID);
    }

    @Override
    public void insertCoin(int column, int playerID) {
        gameState.insertCoin(column, playerID);

        System.out.println("Player " + playerID + " inserted a coin");
        gameState.printState();
    }

    @Override
    public int decideNextMove() {
//        while(true) {
//             Random random = new Random();
//             int column = random.nextInt(gameState.getColumnCount());
//
//             if (!gameState.isColumnFull(column)) {
//                 return column;
//             }
//        }
//
        long start = System.nanoTime();
        int bestColumn = miniMaxer.minimaxDecision(gameState.copyState());

        System.out.println("MiniMax chose: " + bestColumn);
        long finish = System.nanoTime();
        long elapsedTime = finish - start;
        System.out.println("Time taken: " + (double)(elapsedTime)/ 1000000000.0);
        return bestColumn;
    }

    @Override
    public Winner gameFinished() {
       //  return Winner.NOT_FINISHED;
       return gameState.gameFinished();
    }
}
