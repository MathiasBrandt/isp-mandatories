import com.sun.tools.javac.util.Pair;

import java.io.Console;
import java.util.Arrays;
import java.util.Random;

/**
 * Intelligent Systems Programming
 * Connect Four
 * @Author Mathias Flink Brandt.(mfli@itu.dk)
 * @Author Simon Langhoff (siml@itu.dk)
 */
public class SimlMfliGameLogic implements IGameLogic {
    private GameState gameState;
    private MiniMaxer miniMaxer;

    @Override
    public void initializeGame(int columns, int rows, int playerID) {
        gameState = new GameState(columns, rows);
        miniMaxer = new MiniMaxer(playerID);
    }

    @Override
    public void insertCoin(int column, int playerID) {
        gameState.insertCoin(column, playerID);
    }

    @Override
    public int decideNextMove() {
        long start = System.nanoTime();
        int bestColumn = miniMaxer.minimaxDecision(gameState.copyState());

        long finish = System.nanoTime();
        long elapsedTime = finish - start;
        System.out.println("Time taken: " + (double)(elapsedTime)/ 1000000000.0);
        return bestColumn;
    }

    @Override
    public Winner gameFinished() {
       return gameState.gameFinished();
    }
}
