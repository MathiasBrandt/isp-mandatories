import com.sun.tools.javac.util.Pair;

import java.io.Console;
import java.util.Arrays;

/**
 * Created by brandt on 02/03/15.
 */
public class SimlMfliGameLogic implements IGameLogic {
    private final int WIN_CONDITION = 4;

    private GameState gameState;
    private MiniMaxer miniMaxer;

    public SimlMfliGameLogic() {
        miniMaxer = new MiniMaxer(this);
    }

    @Override
    public void initializeGame(int columns, int rows, int playerID) {
        gameState = new GameState(columns, rows);
    }

    @Override
    public void insertCoin(int column, int playerID) {
        gameState.insertCoin(column, playerID);
    }

    @Override
    public int decideNextMove() {
        int bestColumn = miniMaxer.minimaxDecision();

        System.out.println("MiniMax chose: " + bestColumn);

        return bestColumn;
    }

    @Override
    public Winner gameFinished() {

    }


}
